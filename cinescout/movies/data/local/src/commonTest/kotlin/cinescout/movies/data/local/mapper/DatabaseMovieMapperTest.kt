package cinescout.movies.data.local.mapper

import arrow.core.nonEmptyListOf
import cinescout.database.testdata.DatabaseMovieTestData
import cinescout.database.testdata.DatabaseMovieWithRatingTestData
import cinescout.movies.domain.sample.MovieSample
import cinescout.movies.domain.testdata.MovieWithPersonalRatingTestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class DatabaseMovieMapperTest {

    private val mapper = DatabaseMovieMapper()

    @Test
    fun `maps single movie`() {
        // given
        val movie = MovieSample.Inception
        val databaseMovie = DatabaseMovieTestData.Inception

        // when
        val result = mapper.toMovie(databaseMovie)

        // then
        assertEquals(movie, result)
    }

    @Test
    fun `maps movies with rating`() = runTest {
        // given
        val expected = nonEmptyListOf(
            MovieWithPersonalRatingTestData.Inception,
            MovieWithPersonalRatingTestData.TheWolfOfWallStreet
        )
        val databaseMoviesWithRating = nonEmptyListOf(
            DatabaseMovieWithRatingTestData.Inception,
            DatabaseMovieWithRatingTestData.TheWolfOfWallStreet
        )

        // when
        val result = mapper.toMoviesWithRating(databaseMoviesWithRating)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `throws with invalid rating`() = runTest {
        // given
        val databaseMoviesWithRating = nonEmptyListOf(
            DatabaseMovieWithRatingTestData.Inception,
            DatabaseMovieWithRatingTestData.War.copy(personalRating = 12.0)
        )

        // then
        assertFailsWith<IllegalStateException> {

            // when
            mapper.toMoviesWithRating(databaseMoviesWithRating)
        }
    }
}
