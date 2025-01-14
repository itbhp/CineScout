package cinescout.movies.data.remote.model

import cinescout.movies.domain.model.TmdbMovieId
import com.soywiz.klock.Date
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovie(

    @SerialName(BackdropPath)
    val backdropPath: String? = null,

    @SerialName(Id)
    val id: TmdbMovieId,

    @SerialName(Overview)
    val overview: String,

    @SerialName(PosterPath)
    val posterPath: String? = null,

    @SerialName(ReleaseDate)
    @Contextual
    val releaseDate: Date? = null,

    @SerialName(Title)
    val title: String,

    @SerialName(VoteAverage)
    val voteAverage: Double,

    @SerialName(VoteCount)
    val voteCount: Int
) {

    companion object {

        const val BackdropPath = "backdrop_path"
        const val Id = "id"
        const val Overview = "overview"
        const val PosterPath = "poster_path"
        const val ReleaseDate = "release_date"
        const val Title = "title"
        const val VoteAverage = "vote_average"
        const val VoteCount = "vote_count"
    }
}
