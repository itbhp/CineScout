package cinescout.details.presentation.mapper

import cinescout.common.model.CastMember
import cinescout.common.model.CrewMember
import cinescout.common.model.TmdbBackdropImage
import cinescout.common.model.TmdbPosterImage
import cinescout.common.model.TmdbProfileImage
import cinescout.details.presentation.model.MovieDetailsUiModel
import cinescout.movies.domain.model.MovieCredits
import cinescout.movies.domain.model.MovieMedia
import cinescout.movies.domain.model.MovieWithExtras
import org.koin.core.annotation.Factory

@Factory
internal class MovieDetailsUiModelMapper {

    fun toUiModel(movieWithExtras: MovieWithExtras, media: MovieMedia): MovieDetailsUiModel {
        val movie = movieWithExtras.movieWithDetails.movie
        return MovieDetailsUiModel(
            creditsMember = movieWithExtras.credits.members(),
            genres = movieWithExtras.movieWithDetails.genres.map { it.name },
            backdrops = (listOfNotNull(movie.backdropImage.orNull()) + media.backdrops).distinct()
                .map { it.getUrl(TmdbBackdropImage.Size.ORIGINAL) },
            isInWatchlist = movieWithExtras.isInWatchlist,
            overview = movieWithExtras.movieWithDetails.movie.overview,
            posterUrl = movie.posterImage.orNull()?.getUrl(TmdbPosterImage.Size.LARGE),
            ratings = MovieDetailsUiModel.Ratings(
                publicAverage = movie.rating.average.value.toString(),
                publicCount = movie.rating.voteCount.toString(),
                personal = movieWithExtras.personalRating.fold(
                    ifEmpty = { MovieDetailsUiModel.Ratings.Personal.NotRated },
                    ifSome = { rating ->
                        MovieDetailsUiModel.Ratings.Personal.Rated(
                            rating = rating,
                            stringValue = rating.value.toInt().toString()
                        )
                    }
                )
            ),
            releaseDate = movie.releaseDate.fold(ifEmpty = { "" }, ifSome = { it.format("MMM YYYY") }),
            title = movie.title,
            tmdbId = movie.tmdbId,
            videos = media.videos.map { video ->
                MovieDetailsUiModel.Video(
                    previewUrl = video.getPreviewUrl(),
                    title = video.title,
                    url = video.getVideoUrl()
                )
            }
        )
    }

    private fun MovieCredits.members(): List<MovieDetailsUiModel.CreditsMember> =
        (cast + crew).map { member ->
            MovieDetailsUiModel.CreditsMember(
                name = member.person.name,
                profileImageUrl = member.person.profileImage.orNull()?.getUrl(TmdbProfileImage.Size.SMALL),
                role = when (member) {
                    is CastMember -> member.character.orNull()
                    is CrewMember -> member.job.orNull()
                }
            )
        }
}
