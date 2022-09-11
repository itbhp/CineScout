package cinescout.movies.data.local.mapper

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.valueOr
import cinescout.database.model.DatabaseMovie
import cinescout.database.model.DatabaseMovieWithPersonalRating
import cinescout.movies.domain.model.Movie
import cinescout.movies.domain.model.MovieRating
import cinescout.movies.domain.model.MovieWithPersonalRating
import cinescout.movies.domain.model.Rating
import cinescout.movies.domain.model.TmdbBackdropImage
import cinescout.movies.domain.model.TmdbPosterImage
import cinescout.movies.domain.model.getOrThrow

internal class DatabaseMovieMapper {

    fun toMovie(databaseMovie: DatabaseMovie) = Movie(
        backdropImage = Option.fromNullable(databaseMovie.backdropPath).map(::TmdbBackdropImage),
        posterImage = Option.fromNullable(databaseMovie.posterPath).map(::TmdbPosterImage),
        rating = MovieRating(
            voteCount = databaseMovie.ratingCount.toInt(),
            average = Rating.of(databaseMovie.ratingAverage).getOrThrow()
        ),
        releaseDate = Option.fromNullable(databaseMovie.releaseDate),
        title = databaseMovie.title,
        tmdbId = databaseMovie.tmdbId.toId()
    )

    fun toMoviesWithRating(
        list: List<DatabaseMovieWithPersonalRating>
    ): List<MovieWithPersonalRating> = list.map { entry ->
        val rating = Rating.of(entry.personalRating).getOrThrow()

        MovieWithPersonalRating(
            movie = Movie(
                backdropImage = Option.fromNullable(entry.backdropPath).map(::TmdbBackdropImage),
                posterImage = Option.fromNullable(entry.posterPath).map(::TmdbPosterImage),
                rating = MovieRating(
                    voteCount = entry.ratingCount.toInt(),
                    average = Rating.of(entry.ratingAverage).getOrThrow()
                ),
                releaseDate = Option.fromNullable(entry.releaseDate),
                tmdbId = entry.tmdbId.toId(),
                title = entry.title
            ),
            personalRating = rating
        )
    }

    fun toMoviesWithRating(list: NonEmptyList<DatabaseMovieWithPersonalRating>): NonEmptyList<MovieWithPersonalRating> =
        list.map { entry ->
            val rating = Rating.of(entry.personalRating)
                .valueOr { throw IllegalStateException("Invalid rating: $it") }

            MovieWithPersonalRating(
                movie = Movie(
                    backdropImage = Option.fromNullable(entry.backdropPath).map(::TmdbBackdropImage),
                    posterImage = Option.fromNullable(entry.posterPath).map(::TmdbPosterImage),
                    rating = MovieRating(
                        voteCount = entry.ratingCount.toInt(),
                        average = Rating.of(entry.ratingAverage).getOrThrow()
                    ),
                    releaseDate = Option.fromNullable(entry.releaseDate),
                    tmdbId = entry.tmdbId.toId(),
                    title = entry.title
                ),
                personalRating = rating
            )
        }
}
