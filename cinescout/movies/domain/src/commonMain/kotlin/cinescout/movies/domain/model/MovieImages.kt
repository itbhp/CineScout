package cinescout.movies.domain.model

import cinescout.common.model.TmdbBackdropImage

data class MovieImages(
    val backdrops: List<TmdbBackdropImage>,
    val movieId: TmdbMovieId,
    val posters: List<TmdbPosterImage>
)
