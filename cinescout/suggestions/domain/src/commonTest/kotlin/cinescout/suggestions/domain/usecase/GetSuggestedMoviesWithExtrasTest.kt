package cinescout.suggestions.domain.usecase

import app.cash.turbine.test
import arrow.core.*
import cinescout.common.model.SuggestionError
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.sample.MovieSample
import cinescout.movies.domain.testdata.MovieWithExtrasTestData
import cinescout.movies.domain.usecase.GetMovieExtras
import cinescout.test.kotlin.TestTimeout
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSuggestedMoviesWithExtrasTest {

    private val getSuggestedMovies: GetSuggestedMovies = mockk {
        val movies = nonEmptyListOf(
            MovieSample.Inception,
            MovieSample.TheWolfOfWallStreet,
            MovieSample.War
        )
        every { this@mockk() } returns flowOf(movies.right())
    }
    private val getMovieExtras: GetMovieExtras = mockk {
        every { this@mockk(movie = MovieSample.Inception, refresh = any()) } returns
            flowOf(MovieWithExtrasTestData.Inception.right())

        every { this@mockk(movie = MovieSample.TheWolfOfWallStreet, refresh = any()) } returns
            flowOf(MovieWithExtrasTestData.TheWolfOfWallStreet.right())

        every { this@mockk(movie = MovieSample.War, refresh = any()) } returns
            flowOf(MovieWithExtrasTestData.War.right())
    }
    private val getSuggestedMoviesWithExtras = GetSuggestedMoviesWithExtras(
        getSuggestedMovies = getSuggestedMovies,
        getMovieExtras = getMovieExtras
    )

    @Test
    fun `progressively load extras`() = runTest {
        // given
        val expected = listOf(
            nonEmptyListOf(
                MovieWithExtrasTestData.Inception
            ),
            nonEmptyListOf(
                MovieWithExtrasTestData.Inception,
                MovieWithExtrasTestData.TheWolfOfWallStreet
            ),
            nonEmptyListOf(
                MovieWithExtrasTestData.Inception,
                MovieWithExtrasTestData.TheWolfOfWallStreet,
                MovieWithExtrasTestData.War
            )
        )
        every { getMovieExtras(movie = MovieSample.Inception, refresh = any()) } returns flow {
            delay(100)
            emit(MovieWithExtrasTestData.Inception.right())
        }
        every { getMovieExtras(movie = MovieSample.TheWolfOfWallStreet, refresh = any()) } returns flow {
            delay(200)
            emit(MovieWithExtrasTestData.TheWolfOfWallStreet.right())
        }
        every { getMovieExtras(movie = MovieSample.War, refresh = any()) } returns flow {
            delay(300)
            emit(MovieWithExtrasTestData.War.right())
        }

        // when
        getSuggestedMoviesWithExtras().test {

            // then
            assertEquals(expected[0].right(), awaitItem())
            assertEquals(expected[1].right(), awaitItem())
            assertEquals(expected[2].right(), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given suggestions are consumed, when new suggestions, emits new suggestions`() = runTest(
        dispatchTimeoutMs = TestTimeout
    ) {
        // given
        val expected1 = nonEmptyListOf(MovieWithExtrasTestData.Inception).right()
        val expected2 = SuggestionError.NoSuggestions.left()
        val expected3 = nonEmptyListOf(MovieWithExtrasTestData.TheWolfOfWallStreet).right()

        val suggestionsFlow: MutableStateFlow<Either<SuggestionError, NonEmptyList<Movie>>> =
            MutableStateFlow(nonEmptyListOf(MovieSample.Inception).right())
        every { getSuggestedMovies() } returns suggestionsFlow

        // when
        getSuggestedMoviesWithExtras().test {

            assertEquals(expected1, awaitItem())
            suggestionsFlow.emit(SuggestionError.NoSuggestions.left())
            assertEquals(expected2, awaitItem())

            // then
            suggestionsFlow.emit(nonEmptyListOf(MovieSample.TheWolfOfWallStreet).right())
            assertEquals(expected3, awaitItem())
        }
    }

    @Test
    fun `get details only for movies to take`() = runTest {
        // given
        val expected = nonEmptyListOf(
            MovieWithExtrasTestData.Inception,
            MovieWithExtrasTestData.TheWolfOfWallStreet
        ).right()

        // when
        getSuggestedMoviesWithExtras(take = 2).test {

            // then
            assertEquals(expected, awaitItem())
            coVerify(exactly = 1) { getMovieExtras(movie = MovieSample.Inception, refresh = any()) }
            coVerify(exactly = 1) { getMovieExtras(movie = MovieSample.TheWolfOfWallStreet, refresh = any()) }
            coVerify(exactly = 0) { getMovieExtras(movie = MovieSample.War, refresh = any()) }
            awaitComplete()
        }
    }
}
