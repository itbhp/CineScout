package cinescout.movies.data.remote.tmdb.testdata

import cinescout.movies.data.remote.testdata.TmdbMovieTestData
import cinescout.movies.data.remote.tmdb.model.GetRatedMovies

object GetRatedMoviesPageResultTestData {

    val Inception = GetRatedMovies.Response.PageResult(
        id = TmdbMovieTestData.Inception.id,
        releaseDate = TmdbMovieTestData.Inception.releaseDate,
        rating = 9.0,
        title = TmdbMovieTestData.Inception.title
    )
}