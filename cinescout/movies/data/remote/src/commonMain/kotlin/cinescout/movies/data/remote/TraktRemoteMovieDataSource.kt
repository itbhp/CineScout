package cinescout.movies.data.remote

import arrow.core.Either
import cinescout.error.NetworkError
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieWithRating
import cinescout.movies.domain.model.Rating
import cinescout.store.PagedData

interface TraktRemoteMovieDataSource {

    suspend fun getRatedMovies(): Either<NetworkError, PagedData.Remote<MovieWithRating>>

    suspend fun postRating(movie: Movie, rating: Rating)

    suspend fun postWatchlist(movie: Movie)
}
