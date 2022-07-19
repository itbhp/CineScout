package tests

import arrow.core.nonEmptyListOf
import arrow.core.right
import cinescout.auth.tmdb.data.remote.testutil.MockTmdbAuthEngine
import cinescout.auth.trakt.data.remote.testutil.MockTraktAuthEngine
import cinescout.movies.data.remote.tmdb.testutil.MockTmdbMovieEngine
import cinescout.movies.data.remote.trakt.testutil.MockTraktMovieEngine
import cinescout.movies.domain.model.Rating
import cinescout.movies.domain.testdata.MovieTestData
import cinescout.movies.domain.testdata.MovieWithRatingTestData
import cinescout.movies.domain.testdata.TmdbMovieIdTestData
import cinescout.movies.domain.usecase.GetAllRatedMovies
import cinescout.movies.domain.usecase.GetMovie
import cinescout.movies.domain.usecase.RateMovie
import cinescout.network.testutil.plus
import cinescout.network.tmdb.CineScoutTmdbV3Client
import cinescout.network.tmdb.CineScoutTmdbV4Client
import cinescout.network.tmdb.TmdbNetworkQualifier
import cinescout.network.trakt.CineScoutTraktClient
import cinescout.network.trakt.TraktNetworkQualifier
import cinescout.store.multipleSourcesPagedDataOf
import cinescout.suggestions.domain.usecase.GetSuggestedMovies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.dsl.module
import org.koin.test.inject
import util.BaseAppTest
import util.BaseTmdbTest
import util.BaseTraktTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoviesTest : BaseAppTest(), BaseTmdbTest, BaseTraktTest {

    private val getAllRatedMovies: GetAllRatedMovies by inject()
    private val getMovie: GetMovie by inject()
    private val getSuggestedMovies: GetSuggestedMovies by inject()
    private val rateMovie: RateMovie by inject()

    override val extraModule = module {
        factory(TmdbNetworkQualifier.V3.Client) {
            CineScoutTmdbV3Client(
                engine = MockTmdbAuthEngine() + MockTmdbMovieEngine(),
                authProvider = get()
            )
        }
        factory(TmdbNetworkQualifier.V4.Client) {
            CineScoutTmdbV4Client(
                engine = MockTmdbAuthEngine(),
                authProvider = get()
            )
        }
        factory(TraktNetworkQualifier.Client) {
            CineScoutTraktClient(
                engine = MockTraktAuthEngine() + MockTraktMovieEngine(),
                authProvider = get()
            )
        }
    }

    @Test
    fun `get all rated movies`() = runTest {
        // given
        val expected = multipleSourcesPagedDataOf(MovieWithRatingTestData.Inception).right()
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = getAllRatedMovies().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `get movie`() = runTest {
        // given
        val movie = MovieTestData.TheWolfOfWallStreet

        // when
        val result = getMovie(TmdbMovieIdTestData.TheWolfOfWallStreet).first()

        // then
        assertEquals(movie.right(), result)
    }

    @Test
    fun `get suggested movies`() = runTest {
        // given
        val expected = nonEmptyListOf(MovieTestData.TheWolfOfWallStreet).right()
        givenSuccessfullyLinkedToTmdb()
        givenSuccessfullyLinkedToTrakt()

        // when
        val result = getSuggestedMovies().first()

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `rate movie`() = runTest {
        // given
        val movie = MovieTestData.TheWolfOfWallStreet
        Rating.of(8).tap { rating ->

            // when
            val result = rateMovie(movie, rating)

            // then
            assertEquals(Unit.right(), result)
        }
    }
}