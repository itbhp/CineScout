package cinescout.movies.data.remote.trakt

import arrow.core.Either
import cinescout.error.NetworkError
import cinescout.movies.data.remote.TraktRemoteMovieDataSource
import cinescout.movies.data.remote.trakt.mapper.TraktMovieMapper
import cinescout.movies.data.remote.trakt.service.TraktMovieService
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieRating
import cinescout.movies.domain.model.Rating
import cinescout.store.PagedData

internal class RealTraktMovieDataSource(
    private val movieMapper: TraktMovieMapper,
    private val service: TraktMovieService
) : TraktRemoteMovieDataSource {

    override suspend fun getRatedMovies(): Either<NetworkError, PagedData.Remote<MovieRating>> =
        service.getRatedMovies().map { pagedData ->
            pagedData.map { movie ->
                movieMapper.toMovieRating(movie)
            }
        }

    override suspend fun postRating(movie: Movie, rating: Rating) {
    }

    override suspend fun postWatchlist(movie: Movie) {
    }
}
