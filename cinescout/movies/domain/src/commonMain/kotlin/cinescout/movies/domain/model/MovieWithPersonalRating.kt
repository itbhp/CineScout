package cinescout.movies.domain.model

data class MovieWithPersonalRating(
    val movie: Movie,
    val personalRating: Rating
)
