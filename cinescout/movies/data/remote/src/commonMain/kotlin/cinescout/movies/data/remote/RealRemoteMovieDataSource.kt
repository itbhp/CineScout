package cinescout.movies.data.remote

import arrow.core.Either
import arrow.core.continuations.either
import cinescout.error.NetworkError
import cinescout.movies.data.RemoteMovieDataSource
import cinescout.movies.domain.model.DiscoverMoviesParams
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieWithRating
import cinescout.movies.domain.model.Rating
import cinescout.movies.domain.model.TmdbMovieId
import cinescout.store.PagedData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll

class RealRemoteMovieDataSource(
    private val tmdbSource: TmdbRemoteMovieDataSource,
    private val traktSource: TraktRemoteMovieDataSource
) : RemoteMovieDataSource {

    override suspend fun discoverMovies(params: DiscoverMoviesParams): Either<NetworkError, List<Movie>> =
        tmdbSource.discoverMovies(params)

    override suspend fun getMovie(id: TmdbMovieId): Either<NetworkError, Movie> =
        tmdbSource.getMovie(id)

    override suspend fun getRatedMovies(): Either<NetworkError, PagedData.Remote<MovieWithRating>> =
        either {
            val fromTmdb = tmdbSource.getRatedMovies().bind()
            val fromTrakt = run {
                val ratingWithIds = traktSource.getRatedMovies().bind()
                ratingWithIds.map { MovieWithRating(movie = getMovie(it.tmdbId).bind(), rating = it.rating) }
            }
            (fromTmdb + fromTrakt).distinct()
        }

    override suspend fun postRating(movie: Movie, rating: Rating): Either<NetworkError, Unit> = coroutineScope {
        val tmdbResult = async { tmdbSource.postRating(movie, rating) }
        val traktResult = async { traktSource.postRating(movie, rating) }
        tmdbResult.await().map { traktResult.await() }
    }

    override suspend fun postWatchlist(movie: Movie) = coroutineScope {
        val tmdbResult = async { tmdbSource.postWatchlist(movie) }
        val traktResult = async { traktSource.postWatchlist(movie) }
        joinAll(tmdbResult, traktResult)
    }
}
