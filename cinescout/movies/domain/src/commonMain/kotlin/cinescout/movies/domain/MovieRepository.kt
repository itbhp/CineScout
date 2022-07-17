package cinescout.movies.domain

import arrow.core.Either
import cinescout.error.DataError
import cinescout.movies.domain.model.DiscoverMoviesParams
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieWithRating
import cinescout.movies.domain.model.Rating
import cinescout.movies.domain.model.TmdbMovieId
import cinescout.store.PagedStore
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun addToWatchlist(movie: Movie)

    fun discoverMovies(params: DiscoverMoviesParams): Flow<Either<DataError, List<Movie>>>

    fun getAllRatedMovies(): PagedStore<MovieWithRating>

    fun getMovie(id: TmdbMovieId): Flow<Either<DataError, Movie>>

    suspend fun rate(movie: Movie, rating: Rating): Either<DataError, Unit>
}
