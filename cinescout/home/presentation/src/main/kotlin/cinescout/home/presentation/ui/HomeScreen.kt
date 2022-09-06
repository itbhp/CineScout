package cinescout.home.presentation.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.getSystemService
import androidx.navigation.compose.rememberNavController
import cinescout.design.NavHost
import cinescout.design.TestTag
import cinescout.design.composable
import cinescout.design.navigate
import cinescout.design.string
import cinescout.design.theme.CineScoutTheme
import cinescout.design.util.Consume
import cinescout.design.util.collectAsStateLifecycleAware
import cinescout.home.presentation.HomeDestination
import cinescout.home.presentation.currentHomeDestinationAsState
import cinescout.home.presentation.model.HomeAction
import cinescout.home.presentation.model.HomeState
import cinescout.home.presentation.viewmodel.HomeViewModel
import cinescout.lists.presentation.ui.DislikedListScreen
import cinescout.lists.presentation.ui.ItemsListScreen
import cinescout.lists.presentation.ui.LikedListScreen
import cinescout.lists.presentation.ui.MyListsScreen
import cinescout.lists.presentation.ui.RatedListScreen
import cinescout.lists.presentation.ui.WatchlistScreen
import cinescout.movies.domain.model.TmdbMovieId
import cinescout.suggestions.presentation.ui.ForYouScreen
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import studio.forface.cinescout.design.R.drawable
import studio.forface.cinescout.design.R.string

@Composable
fun HomeScreen(actions: HomeScreen.Actions, modifier: Modifier = Modifier) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateLifecycleAware()
    val loginActions = LoginActions(
        loginToTmdb = { viewModel.submit(HomeAction.LoginToTmdb) },
        loginToTrakt = { viewModel.submit(HomeAction.LoginToTrakt) }
    )
    HomeScreen(state = state, actions = actions, loginActions = loginActions, modifier = modifier)
}

@Composable
fun HomeScreen(
    state: HomeState,
    actions: HomeScreen.Actions,
    loginActions: LoginActions,
    modifier: Modifier = Modifier,
    startDestination: HomeDestination = HomeDestination.Start
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbarHostState = SnackbarHostState()
    var shouldShowAccountsDialog by remember { mutableStateOf(false) }

    val onDrawerItemClick: (HomeDrawer.ItemId) -> Unit = { itemId ->
        when (itemId) {
            HomeDrawer.ItemId.ForYou -> navController.navigate(HomeDestination.ForYou)
            HomeDrawer.ItemId.MyLists -> navController.navigate(HomeDestination.MyLists)
            HomeDrawer.ItemId.Login -> { shouldShowAccountsDialog = true }
            HomeDrawer.ItemId.Watchlist -> navController.navigate(HomeDestination.Watchlist)
        }
        scope.launch { drawerState.close() }
    }

    Consume(effect = state.loginEffect) { loginState ->
        when (loginState) {
            is HomeState.Login.Error -> {
                val message = string(textRes = loginState.message)
                scope.launch { snackbarHostState.showSnackbar(message) }
            }

            HomeState.Login.Linked -> {
                val message = stringResource(id = string.home_logged_in)
                scope.launch { snackbarHostState.showSnackbar(message) }
            }

            is HomeState.Login.UserShouldAuthorizeApp -> {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(loginState.authorizationUrl))
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    val clipboardManager = context.getSystemService<ClipboardManager>()
                    val clipData = ClipData.newPlainText(
                        stringResource(id = string.home_login_authorization_url_clipboard_label),
                        loginState.authorizationUrl
                    )
                    clipboardManager?.setPrimaryClip(clipData)
                    val message = stringResource(id = string.home_login_error_cannot_open_browser)
                    scope.launch { snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long) }
                }
            }
        }
    }

    if (shouldShowAccountsDialog) {
        val action = AccountsDialog.Actions(
            loginActions = loginActions,
            onDismissRequest = { shouldShowAccountsDialog = false }
        )
        AccountsDialog(state = state.accounts, actions = action)
    }

    val currentHomeDestination by navController.currentHomeDestinationAsState()
    HomeDrawer(homeState = state, drawerState = drawerState, onItemClick = onDrawerItemClick) {
        Scaffold(
            modifier = modifier
                .testTag(TestTag.Home)
                .statusBarsPadding()
                .navigationBarsPadding(),
            bottomBar = { HomeBottomBar(openDrawer = { scope.launch { drawerState.open() } }) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                HomeTopBar(
                    state.accounts.primary,
                    currentDestination = currentHomeDestination,
                    openAccounts = { shouldShowAccountsDialog = true }
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                NavHost(navController = navController, startDestination = startDestination) {
                    val itemsListActions = ItemsListScreen.Actions(toMovieDetails = actions.toMovieDetails)

                    composable(HomeDestination.Disliked) {
                        DislikedListScreen(actions = itemsListActions)
                    }
                    composable(HomeDestination.ForYou) {
                        val forYouActions = ForYouScreen.Actions(toMovieDetails = actions.toMovieDetails)
                        ForYouScreen(actions = forYouActions)
                    }
                    composable(HomeDestination.Liked) {
                        LikedListScreen(actions = itemsListActions)
                    }
                    composable(HomeDestination.MyLists) {
                        val myListsActions = MyListsScreen.Actions(
                            onDislikedClick = { navController.navigate(HomeDestination.Disliked) },
                            onLikedClick = { navController.navigate(HomeDestination.Liked) },
                            onRatedClick = { navController.navigate(HomeDestination.Rated) }
                        )
                        MyListsScreen(myListsActions)
                    }
                    composable(HomeDestination.None) {}
                    composable(HomeDestination.Rated) {
                        RatedListScreen(actions = itemsListActions)
                    }
                    composable(HomeDestination.Watchlist) {
                        WatchlistScreen(actions = itemsListActions)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    primaryAccount: HomeState.Accounts.Account,
    currentDestination: HomeDestination,
    openAccounts: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(id = string.app_name))
                Text(text = string(textRes = currentDestination.label), style = MaterialTheme.typography.labelMedium)
            }
        },
        actions = {
            if (primaryAccount is HomeState.Accounts.Account.Data) {
                IconButton(onClick = openAccounts) {
                    AsyncImage(
                        modifier = Modifier.clip(CircleShape),
                        model = primaryAccount.imageUrl,
                        contentDescription = stringResource(id = string.profile_picture_description),
                        placeholder = painterResource(id = drawable.ic_user_color)
                    )
                }
            }
        }
    )
}

@Composable
private fun HomeBottomBar(openDrawer: () -> Unit) {
    BottomAppBar(actions = {
        IconButton(onClick = openDrawer) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(id = string.menu_button_description)
            )
        }
    })
}

object HomeScreen {

    data class Actions(
        val toMovieDetails: (movieId: TmdbMovieId) -> Unit
    ) {

        companion object {

            val Empty = Actions(toMovieDetails = {})
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    CineScoutTheme {
        HomeScreen(
            state = HomeState.Loading,
            actions = HomeScreen.Actions.Empty,
            loginActions = LoginActions.Empty,
            startDestination = HomeDestination.ForYou
        )
    }
}
