package tests

import app.cash.turbine.test
import arrow.core.nonEmptyListOf
import arrow.core.right
import cinescout.account.tmdb.data.remote.testutil.MockTmdbAccountEngine
import cinescout.account.trakt.data.remote.testutil.MockTraktAccountEngine
import cinescout.auth.tmdb.data.remote.testutil.MockTmdbAuthEngine
import cinescout.auth.trakt.data.remote.testutil.MockTraktAuthEngine
import cinescout.common.model.Rating
import cinescout.movies.data.remote.tmdb.testutil.MockTmdbMovieEngine
import cinescout.movies.data.remote.tmdb.testutil.TmdbMovieDetailsJson
import cinescout.movies.data.remote.tmdb.testutil.addMovieDetailsHandler
import cinescout.movies.data.remote.trakt.testutil.MockTraktMovieEngine
import cinescout.movies.domain.sample.MovieSample
import cinescout.movies.domain.sample.TmdbMovieIdSample
import cinescout.movies.domain.testdata.MovieWithDetailsTestData
import cinescout.movies.domain.testdata.MovieWithPersonalRatingTestData
import cinescout.movies.domain.usecase.GetAllRatedMovies
import cinescout.movies.domain.usecase.GetAllWatchlistMovies
import cinescout.movies.domain.usecase.GetMovieDetails
import cinescout.movies.domain.usecase.RateMovie
import cinescout.network.testutil.plus
import cinescout.network.tmdb.CineScoutTmdbV3Client
import cinescout.network.tmdb.CineScoutTmdbV4Client
import cinescout.network.tmdb.TmdbNetworkQualifier
import cinescout.network.trakt.CineScoutTraktClient
import cinescout.network.trakt.TraktNetworkQualifier
import cinescout.suggestions.domain.model.SuggestionsMode
import cinescout.suggestions.domain.usecase.GenerateSuggestedMovies
import cinescout.suggestions.domain.usecase.StartUpdateSuggestions
import cinescout.test.kotlin.TestTimeout
import cinescout.test.mock.TestSqlDriverModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.inject
import store.builder.dualSourcesPagedDataOf
import util.BaseAppTest
import util.BaseTmdbTest
import util.BaseTraktTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesTest : BaseAppTest(), BaseTmdbTest, BaseTraktTest {

    private val getAllRatedMovies: GetAllRatedMovies by inject()
    private val getAllWatchlistMovies: GetAllWatchlistMovies by inject()
    private val getMovieDetails: GetMovieDetails by inject()
    private val generateSuggestedMovies: GenerateSuggestedMovies by inject()
    private val rateMovie: RateMovie by inject()

    private val tmdbMovieEngine = MockTmdbMovieEngine()

    override val extraModule = module {
        includes(TestSqlDriverModule)

        single<CoroutineScope> { TestScope(context = UnconfinedTestDispatcher()) }
        factory(named(TmdbNetworkQualifier.V3.Client)) {
            CineScoutTmdbV3Client(
                engine = MockTmdbAccountEngine() + MockTmdbAuthEngine() + tmdbMovieEngine,
                authProvider = get()
            )
        }
        factory(named(TmdbNetworkQualifier.V4.Client)) {
            CineScoutTmdbV4Client(
                engine = MockTmdbAuthEngine(),
                authProvider = get()
            )
        }
        factory(named(TraktNetworkQualifier.Client)) {
            CineScoutTraktClient(
                engine = MockTraktAccountEngine() + MockTraktAuthEngine() + MockTraktMovieEngine(),
                authProvider = get()
            )
        }
        factory { StartUpdateSuggestions {} }
    }

    @Test
    fun `get all rated movies`() = runTest {
        // given
        val expected = dualSourcesPagedDataOf(MovieWithPersonalRatingTestData.Inception).right()
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        getAllRatedMovies().test {

            // then
            assertEquals(expected.map { it.data }, awaitItem().map { it.data })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get all watchlist movies`() = runTest(dispatchTimeoutMs = TestTimeout) {
        // given
        val expected = dualSourcesPagedDataOf(MovieSample.Inception).right()
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        getAllWatchlistMovies().test {

            // then
            assertEquals(expected.map { it.data }, awaitItem().map { it.data })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get movie`() = runTest {
        // given
        val movie = MovieWithDetailsTestData.TheWolfOfWallStreet

        // when
        val result = getMovieDetails(TmdbMovieIdSample.TheWolfOfWallStreet).first()

        // then
        assertEquals(movie.right(), result)
    }

    @Test
    fun `generate suggested movies`() = runTest(dispatchTimeoutMs = TestTimeout) {
        // given
        val expected = nonEmptyListOf(MovieSample.TheWolfOfWallStreet).right()
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = generateSuggestedMovies(SuggestionsMode.Quick).first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `generate suggested movies completes with movie details with empty genres`() = runTest(
        dispatchTimeoutMs = TestTimeout
    ) {
        // given
        val expected = nonEmptyListOf(MovieSample.TheWolfOfWallStreet).right()
        tmdbMovieEngine.addMovieDetailsHandler(
            TmdbMovieIdSample.Inception,
            TmdbMovieDetailsJson.InceptionWithEmptyGenres
        )
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = generateSuggestedMovies(SuggestionsMode.Quick).first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `generate suggested movies completes with movie details without genres`() = runTest(
        dispatchTimeoutMs = TestTimeout
    ) {
        // given
        val expected = nonEmptyListOf(MovieSample.TheWolfOfWallStreet).right()
        tmdbMovieEngine.addMovieDetailsHandler(
            TmdbMovieIdSample.Inception,
            TmdbMovieDetailsJson.InceptionWithoutGenres
        )
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = generateSuggestedMovies(SuggestionsMode.Quick).first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `generate suggested movies completes with movie details without release date`() = runTest(
        dispatchTimeoutMs = TestTimeout
    ) {
        // given
        val expected = nonEmptyListOf(MovieSample.TheWolfOfWallStreet).right()
        tmdbMovieEngine.addMovieDetailsHandler(
            TmdbMovieIdSample.Inception,
            TmdbMovieDetailsJson.InceptionWithoutReleaseDate
        )
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = generateSuggestedMovies(SuggestionsMode.Quick).first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `rate movie`() = runTest {
        // given
        val expected = Unit.right()
        val movieId = MovieSample.TheWolfOfWallStreet.tmdbId
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()
        Rating.of(8).tap { rating ->

            // when
            val result = rateMovie(movieId, rating)

            // then
            assertEquals(expected, result)
        }
    }
}
