package cinescout.movies.domain.usecase

import arrow.core.Either
import cinescout.error.DataError
import cinescout.movies.domain.MovieRepository
import cinescout.movies.domain.model.TmdbMovieId
import org.koin.core.annotation.Factory

@Factory
class AddMovieToWatchlist(
    private val movieRepository: MovieRepository
) {

    suspend operator fun invoke(movieId: TmdbMovieId): Either<DataError.Remote, Unit> =
        movieRepository.addToWatchlist(movieId)
}
