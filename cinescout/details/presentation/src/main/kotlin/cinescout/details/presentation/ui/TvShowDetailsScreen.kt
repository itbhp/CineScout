package cinescout.details.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import cinescout.common.model.Rating
import cinescout.design.TestTag
import cinescout.design.theme.CineScoutTheme
import cinescout.design.theme.Dimens
import cinescout.design.theme.imageBackground
import cinescout.design.ui.BannerScaffold
import cinescout.design.ui.CenteredProgress
import cinescout.design.ui.CineScoutBottomBar
import cinescout.design.ui.ConnectionStatusBanner
import cinescout.design.ui.ErrorScreen
import cinescout.design.util.NoContentDescription
import cinescout.design.util.collectAsStateLifecycleAware
import cinescout.details.presentation.model.TvShowDetailsAction
import cinescout.details.presentation.model.TvShowDetailsUiModel
import cinescout.details.presentation.previewdata.TvShowDetailsScreenPreviewDataProvider
import cinescout.details.presentation.state.TvShowDetailsState
import cinescout.details.presentation.state.TvShowDetailsTvShowState
import cinescout.details.presentation.viewmodel.TvShowDetailsViewModel
import cinescout.tvshows.domain.model.TmdbTvShowId
import cinescout.utils.compose.Adaptive
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import studio.forface.cinescout.design.R.drawable
import studio.forface.cinescout.design.R.string

@Composable
fun TvShowDetailsScreen(tvShowId: TmdbTvShowId, actions: TvShowDetailsScreen.Actions, modifier: Modifier = Modifier) {
    val viewModel: TvShowDetailsViewModel = koinViewModel(parameters = { parametersOf(tvShowId) })
    val state by viewModel.state.collectAsStateLifecycleAware()
    val movieActions = TvShowDetailsScreen.TvShowActions(
        addToWatchlist = { viewModel.submit(TvShowDetailsAction.AddToWatchlist) },
        rate = { viewModel.submit(TvShowDetailsAction.RateTvShow(it)) },
        removeFromWatchlist = { viewModel.submit(TvShowDetailsAction.RemoveFromWatchlist) }
    )
    TvShowDetailsScreen(
        state = state,
        actions = actions,
        movieActions = movieActions,
        modifier = modifier
    )
}

@Composable
fun TvShowDetailsScreen(
    state: TvShowDetailsState,
    actions: TvShowDetailsScreen.Actions,
    movieActions: TvShowDetailsScreen.TvShowActions,
    modifier: Modifier = Modifier
) {
    BannerScaffold(
        modifier = modifier.testTag(TestTag.TvShowDetails),
        bottomBar = {
            val isInWatchlist = (state.tvShowState as? TvShowDetailsTvShowState.Data)?.tvShowDetails?.isInWatchlist
            val bottomBarActions = TvShowDetailsBottomBar.Actions(
                onBack = actions.onBack,
                addToWatchlist = movieActions.addToWatchlist,
                removeFromWatchlist = movieActions.removeFromWatchlist
            )
            TvShowDetailsBottomBar(isInWatchlist = isInWatchlist, actions = bottomBarActions)
        },
        banner = { ConnectionStatusBanner(uiModel = state.connectionStatus) }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Surface(
            modifier = Modifier.padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            TvShowDetailsContent(state = state.tvShowState, movieActions = movieActions)
        }
    }
}

@Composable
fun TvShowDetailsContent(state: TvShowDetailsTvShowState, movieActions: TvShowDetailsScreen.TvShowActions) {
    var shouldShowRateDialog by remember { mutableStateOf(false) }
    when (state) {
        is TvShowDetailsTvShowState.Data -> {
            val movieDetails = state.tvShowDetails
            if (shouldShowRateDialog) {
                val dialogActions = RateItemDialog.Actions(
                    onDismissRequest = { shouldShowRateDialog = false },
                    saveRating = movieActions.rate
                )
                RateItemDialog(
                    itemTitle = movieDetails.title,
                    itemPersonalRating = movieDetails.ratings.personal.rating,
                    actions = dialogActions
                )
            }
            Adaptive { windowSizeClass ->
                val mode = TvShowDetailsLayout.Mode.forClass(windowSizeClass)
                TvShowDetailsLayout(
                    mode = mode,
                    backdrops = { Backdrops(urls = movieDetails.backdrops) },
                    poster = { Poster(url = movieDetails.posterUrl) },
                    infoBox = { InfoBox(title = movieDetails.title, releaseDate = movieDetails.firstAirDate) },
                    ratings = {
                        Ratings(
                            ratings = movieDetails.ratings,
                            openRateDialog = { shouldShowRateDialog = true }
                        )
                    },
                    genres = { Genres(mode = mode, genres = movieDetails.genres) },
                    credits = { CreditsMembers(mode = mode, creditsMembers = movieDetails.creditsMember) },
                    overview = { Overview(overview = movieDetails.overview) },
                    videos = { Videos(videos = movieDetails.videos) }
                )
            }
        }
        is TvShowDetailsTvShowState.Error -> ErrorScreen(text = state.message)
        TvShowDetailsTvShowState.Loading -> CenteredProgress()
    }
}

@Composable
@OptIn(ExperimentalSnapperApi::class)
private fun Backdrops(urls: List<String?>) {
    val lazyListState = rememberLazyListState()
    Box(contentAlignment = Alignment.TopCenter) {
        LazyRow(
            state = lazyListState,
            flingBehavior = rememberSnapperFlingBehavior(lazyListState)
        ) {
            items(urls) { url ->
                Backdrop(modifier = Modifier.fillParentMaxSize(), url = url)
            }
        }
        Row(modifier = Modifier.statusBarsPadding()) {
            val currentIndex = lazyListState.firstVisibleItemIndex
            repeat(urls.size) { index ->
                fun Modifier.background() = when (index) {
                    currentIndex -> background(color = Color.Transparent)
                    else -> background(color = Color.White, shape = CircleShape)
                }

                fun Modifier.border() = when (index) {
                    currentIndex -> border(width = Dimens.Outline, color = Color.White, shape = CircleShape)
                    else -> border(width = Dimens.Outline, color = Color.Black)
                }
                Box(
                    modifier = Modifier
                        .padding(Dimens.Margin.XXSmall)
                        .size(Dimens.Indicator)
                        .background()
                        .border()
                )
            }
        }
    }
}

@Composable
private fun Backdrop(url: String?, modifier: Modifier = Modifier) {
    GlideImage(
        modifier = modifier
            .fillMaxSize()
            .imageBackground(),
        imageModel = { url },
        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
        failure = {
            Image(
                painter = painterResource(id = drawable.ic_warning_30),
                contentDescription = NoContentDescription
            )
        },
        previewPlaceholder = drawable.img_backdrop
    )
}

@Composable
private fun Poster(url: String?) {
    GlideImage(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .imageBackground(),
        imageModel = { url },
        failure = {
            Image(
                painter = painterResource(id = drawable.ic_warning_30),
                contentDescription = NoContentDescription
            )
        },
        previewPlaceholder = drawable.img_poster
    )
}

@Composable
private fun InfoBox(title: String, releaseDate: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(Dimens.Margin.Small)
    ) {
        Text(text = title, maxLines = 2, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(Dimens.Margin.Small))
        Text(text = releaseDate, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun Ratings(ratings: TvShowDetailsUiModel.Ratings, openRateDialog: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.Margin.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            modifier = Modifier
                .border(
                    width = Dimens.Outline,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.small
                )
                .padding(Dimens.Margin.Medium)
                .width(Dimens.Icon.Medium)
                .height(Dimens.Icon.Small),
            imageModel = { drawable.img_tmdb_logo_short },
            imageOptions = ImageOptions(
                contentScale = ContentScale.FillWidth,
                contentDescription = stringResource(id = string.tmdb_logo_description)
            ),
            previewPlaceholder = drawable.img_tmdb_logo_short
        )
        Spacer(modifier = Modifier.width(Dimens.Margin.Small))
        Column {
            Text(text = ratings.publicAverage, style = MaterialTheme.typography.titleMedium)
            Text(text = ratings.publicCount, style = MaterialTheme.typography.bodySmall)
        }
    }
    PersonalRating(rating = ratings.personal, openRateDialog = openRateDialog)
}

@Composable
private fun PersonalRating(rating: TvShowDetailsUiModel.Ratings.Personal, openRateDialog: () -> Unit) {
    FilledTonalButton(
        onClick = openRateDialog,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(horizontal = Dimens.Margin.Medium, vertical = Dimens.Margin.Small)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (rating) {
                TvShowDetailsUiModel.Ratings.Personal.NotRated -> {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = NoContentDescription)
                    Text(text = stringResource(id = string.details_rate_now))
                }
                is TvShowDetailsUiModel.Ratings.Personal.Rated -> Text(
                    text = rating.stringValue,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun Genres(mode: TvShowDetailsLayout.Mode, genres: List<String>) {
    when (mode) {
        TvShowDetailsLayout.Mode.Horizontal -> FlowRow(mainAxisAlignment = FlowMainAxisAlignment.Center) {
            for (genre in genres) {
                Genre(genre = genre)
            }
        }
        is TvShowDetailsLayout.Mode.Vertical -> LazyRow {
            items(genres) { genre ->
                Genre(genre = genre)
            }
        }
    }
}

@Composable
private fun Genre(genre: String) {
    Text(
        modifier = Modifier
            .padding(Dimens.Margin.XXSmall)
            .background(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                shape = MaterialTheme.shapes.small
            )
            .padding(Dimens.Margin.Small),
        text = genre,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun CreditsMembers(mode: TvShowDetailsLayout.Mode, creditsMembers: List<TvShowDetailsUiModel.CreditsMember>) {
    when (mode) {
        TvShowDetailsLayout.Mode.Horizontal -> LazyColumn(
            contentPadding = PaddingValues(vertical = Dimens.Margin.Small)
        ) {
            items(creditsMembers) { member ->
                CreditsMember(mode = mode, member = member)
            }
        }
        is TvShowDetailsLayout.Mode.Vertical -> LazyRow {
            items(creditsMembers) { member ->
                CreditsMember(mode = mode, member = member)
            }
        }
    }
}

@Composable
private fun CreditsMember(mode: TvShowDetailsLayout.Mode, member: TvShowDetailsUiModel.CreditsMember) {
    when (mode) {
        TvShowDetailsLayout.Mode.Horizontal -> Row(
            modifier = Modifier.padding(vertical = Dimens.Margin.XSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CreditsMemberImage(url = member.profileImageUrl)
            Spacer(modifier = Modifier.width(Dimens.Margin.Small))
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = member.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = member.role.orEmpty(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        is TvShowDetailsLayout.Mode.Vertical -> Column(
            modifier = Modifier
                .width(Dimens.Image.Medium + Dimens.Margin.Medium * 2)
                .padding(Dimens.Margin.XSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreditsMemberImage(url = member.profileImageUrl)
            Spacer(modifier = Modifier.height(Dimens.Margin.XSmall))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = member.name,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = member.role.orEmpty(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CreditsMemberImage(url: String?) {
    GlideImage(
        modifier = Modifier
            .size(Dimens.Image.Medium)
            .clip(CircleShape)
            .imageBackground(),
        imageModel = { url },
        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
        failure = {
            Image(
                painter = painterResource(id = drawable.ic_warning_30),
                contentDescription = NoContentDescription
            )
        },
        previewPlaceholder = drawable.ic_user_color
    )
}

@Composable
private fun Overview(overview: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = overview,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun Videos(videos: List<TvShowDetailsUiModel.Video>) {
    val context = LocalContext.current

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        LazyRow {
            items(videos) { video ->
                Column(
                    modifier = Modifier
                        .width(maxWidth * 47 / 100)
                        .padding(horizontal = Dimens.Margin.XSmall)
                        .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(video.url))) }
                ) {
                    GlideImage(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .imageBackground(),
                        imageModel = { video.previewUrl },
                        imageOptions = ImageOptions(contentDescription = video.title),
                        previewPlaceholder = drawable.img_video
                    )
                    Spacer(modifier = Modifier.height(Dimens.Margin.XSmall))
                    Text(
                        text = video.title,
                        maxLines = 2,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun TvShowDetailsBottomBar(isInWatchlist: Boolean?, actions: TvShowDetailsBottomBar.Actions) {
    CineScoutBottomBar(
        icon = {
            IconButton(onClick = actions.onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = string.back_button_description)
                )
            }
        },
        actions = {
            when (isInWatchlist) {
                true -> IconButton(onClick = actions.removeFromWatchlist) {
                    Icon(
                        painter = painterResource(id = drawable.ic_bookmark_filled),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(id = string.remove_from_watchlist_button_description)
                    )
                }

                false -> IconButton(onClick = actions.addToWatchlist) {
                    Icon(
                        painter = painterResource(id = drawable.ic_bookmark),
                        contentDescription = stringResource(id = string.add_to_watchlist_button_description)
                    )
                }

                null -> Unit
            }
        }
    )
}

object TvShowDetailsScreen {

    const val TvShowIdKey = "movie_id"

    data class Actions(
        val onBack: () -> Unit
    ) {

        companion object {

            val Empty = Actions(onBack = {})
        }
    }

    data class TvShowActions(
        val addToWatchlist: () -> Unit,
        val rate: (Rating) -> Unit,
        val removeFromWatchlist: () -> Unit
    ) {

        companion object {

            val Empty = TvShowActions(
                addToWatchlist = {},
                rate = {},
                removeFromWatchlist = {}
            )
        }
    }
}

private object TvShowDetailsBottomBar {

    data class Actions(
        val onBack: () -> Unit,
        val addToWatchlist: () -> Unit,
        val removeFromWatchlist: () -> Unit
    )
}

@Composable
@Preview
@Preview(device = Devices.TABLET)
private fun TvShowDetailsScreenPreview(
    @PreviewParameter(TvShowDetailsScreenPreviewDataProvider::class) state: TvShowDetailsState
) {
    CineScoutTheme {
        TvShowDetailsScreen(
            state = state,
            actions = TvShowDetailsScreen.Actions.Empty,
            movieActions = TvShowDetailsScreen.TvShowActions.Empty
        )
    }
}
