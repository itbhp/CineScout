package cinescout.movies.domain

import arrow.core.Either
import cinescout.error.DataError
import cinescout.movies.domain.model.DiscoverMoviesParams
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieCredits
import cinescout.movies.domain.model.MovieWithDetails
import cinescout.movies.domain.model.MovieWithPersonalRating
import cinescout.movies.domain.model.Rating
import cinescout.movies.domain.model.TmdbMovieId
import cinescout.store.PagedStore
import cinescout.store.Paging
import cinescout.store.Refresh
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun addToDisliked(id: TmdbMovieId)

    suspend fun addToLiked(id: TmdbMovieId)

    suspend fun addToWatchlist(id: TmdbMovieId): Either<DataError.Remote, Unit>

    fun discoverMovies(params: DiscoverMoviesParams): Flow<Either<DataError.Remote, List<Movie>>>

    fun getAllDislikedMovies(): Flow<Either<DataError.Local, List<Movie>>>

    fun getAllLikedMovies(): Flow<Either<DataError.Local, List<Movie>>>

    fun getAllRatedMovies(): PagedStore<MovieWithPersonalRating, Paging.Page.DualSources>

    fun getMovieDetails(id: TmdbMovieId, refresh: Refresh = Refresh.Once): Flow<Either<DataError, MovieWithDetails>>

    fun getMovieCredits(movieId: TmdbMovieId, refresh: Refresh = Refresh.Once): Flow<Either<DataError.Remote, MovieCredits>>

    suspend fun rate(movieId: TmdbMovieId, rating: Rating): Either<DataError, Unit>

    suspend fun syncRatedMovies()
}
