package cinescout.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cinescout.design.NavHost
import cinescout.design.composable
import cinescout.design.get
import cinescout.design.navigate
import cinescout.design.theme.CineScoutTheme
import cinescout.details.presentation.ui.MovieDetailsScreen
import cinescout.details.presentation.ui.TvShowDetailsScreen
import cinescout.home.presentation.ui.HomeScreen
import cinescout.suggestions.presentation.ui.ForYouHintScreen

@Composable
internal fun App(onFinish: () -> Unit) {
    val navController = rememberNavController()
    val onBack = { navController.popOrFinish(onFinish) }
    NavHost(navController = navController, startDestination = AppDestination.Home) {
        composable(AppDestination.ForYouHint) {
            ForYouHintScreen(onBack = onBack)
        }
        composable(AppDestination.Home) {
            val homeScreenActions = HomeScreen.Actions(
                toForYouHint = { navController.navigate(AppDestination.ForYouHint) },
                toMovieDetails = { movieId -> navController.navigate(AppDestination.MovieDetails, movieId) },
                toTvShowDetails = { tvShowId -> navController.navigate(AppDestination.TvShowDetails, tvShowId) }
            )
            HomeScreen(actions = homeScreenActions)
        }
        composable(AppDestination.MovieDetails) { backStackEntry ->
            val movieDetailsActions = MovieDetailsScreen.Actions(
                onBack = onBack
            )
            MovieDetailsScreen(movieId = backStackEntry[MovieDetailsScreen.MovieIdKey], actions = movieDetailsActions)
        }
        composable(AppDestination.TvShowDetails) { backStackEntry ->
            val tvShowDetailsActions = TvShowDetailsScreen.Actions(
                onBack = onBack
            )
            TvShowDetailsScreen(
                tvShowId = backStackEntry[TvShowDetailsScreen.TvShowIdKey],
                actions = tvShowDetailsActions
            )
        }
    }
}

private fun NavController.popOrFinish(onFinish: () -> Unit) {
    if (popBackStack().not()) onFinish()
}

@Composable
@Preview(showBackground = true)
private fun AppPreview() {
    CineScoutTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            App(onFinish = {})
        }
    }
}
