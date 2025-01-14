package cinescout.suggestions.domain.usecase

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import cinescout.common.model.SuggestionError
import cinescout.error.DataError
import cinescout.suggestions.domain.model.SuggestionsMode
import cinescout.tvshows.domain.TvShowRepository
import cinescout.tvshows.domain.model.TmdbTvShowId
import cinescout.tvshows.domain.model.TvShow
import cinescout.tvshows.domain.model.TvShowWithPersonalRating
import cinescout.tvshows.domain.usecase.GetAllDislikedTvShows
import cinescout.tvshows.domain.usecase.GetAllLikedTvShows
import cinescout.tvshows.domain.usecase.GetAllRatedTvShows
import cinescout.tvshows.domain.usecase.GetAllWatchlistTvShows
import cinescout.utils.kotlin.combineLatest
import cinescout.utils.kotlin.nonEmpty
import cinescout.utils.kotlin.randomOrNone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import store.PagedData
import store.Paging
import store.Refresh

@Factory
class GenerateSuggestedTvShows(
    private val getAllDislikedTvShows: GetAllDislikedTvShows,
    private val getAllLikedTvShows: GetAllLikedTvShows,
    private val getAllRatedTvShows: GetAllRatedTvShows,
    private val getAllWatchlistTvShows: GetAllWatchlistTvShows,
    private val tvShowRepository: TvShowRepository
) {

    operator fun invoke(
        suggestionsMode: SuggestionsMode
    ): Flow<Either<SuggestionError, NonEmptyList<TvShow>>> =
        combineLatest(
            getAllDislikedTvShows(),
            getAllLikedTvShows(),
            getAllRatedTvShows(suggestionsMode),
            getAllWatchlistTvShows(suggestionsMode)
        ) { disliked, liked, ratedEither, watchlistEither ->

            val rated = ratedEither
                .fold(
                    ifLeft = { emptyList() },
                    ifRight = { it.data }
                )

            val watchlist = watchlistEither
                .fold(
                    ifLeft = { emptyList() },
                    ifRight = { it.data }
                )

            val positiveTvShows = liked + rated.filterPositiveRating() + watchlist
            val movieId = positiveTvShows.randomOrNone().map { it.tmdbId }
                .getOrElse { return@combineLatest flowOf(SuggestionError.NoSuggestions.left()) }

            getRecommendationsFor(movieId, suggestionsMode).map { dataEither ->
                dataEither.mapLeft {
                    SuggestionError.Source(it as DataError.Remote)
                }.flatMap { pagedData ->
                    val allKnownTvShows = disliked + liked + rated.map { it.tvShow } + watchlist
                    pagedData.data.filterKnownTvShows(allKnownTvShows)
                }
            }
        }

    private fun getAllRatedTvShows(
        suggestionsMode: SuggestionsMode
    ): Flow<Either<DataError, PagedData<TvShowWithPersonalRating, Paging>>> =
        when (suggestionsMode) {
            SuggestionsMode.Deep -> getAllRatedTvShows(refresh = Refresh.Once).filterIntermediatePages()
            SuggestionsMode.Quick -> getAllRatedTvShows(refresh = Refresh.IfNeeded)
        }

    private fun getAllWatchlistTvShows(
        suggestionsMode: SuggestionsMode
    ): Flow<Either<DataError, PagedData<TvShow, Paging>>> =
        when (suggestionsMode) {
            SuggestionsMode.Deep -> getAllWatchlistTvShows(refresh = Refresh.Once).filterIntermediatePages()
            SuggestionsMode.Quick -> getAllWatchlistTvShows(refresh = Refresh.IfNeeded)
        }

    private fun getRecommendationsFor(
        movieId: TmdbTvShowId,
        suggestionsMode: SuggestionsMode
    ): Flow<Either<DataError, PagedData<TvShow, Paging>>> =
        when (suggestionsMode) {
            SuggestionsMode.Deep ->
                tvShowRepository.getRecommendationsFor(movieId, refresh = Refresh.Once).filterIntermediatePages()
            SuggestionsMode.Quick ->
                tvShowRepository.getRecommendationsFor(movieId, refresh = Refresh.IfNeeded)
        }

    private fun List<TvShowWithPersonalRating>.filterPositiveRating(): List<TvShow> =
        filter { tvShowWithPersonalRating -> tvShowWithPersonalRating.personalRating.value >= 7 }
            .map { it.tvShow }

    private fun List<TvShow>.filterKnownTvShows(
        knownTvShows: List<TvShow>
    ): Either<SuggestionError, NonEmptyList<TvShow>> {
        val knownTvShowIds = knownTvShows.map { it.tmdbId }
        return filterNot { movie -> movie.tmdbId in knownTvShowIds }
            .nonEmpty { SuggestionError.NoSuggestions }
    }
}
