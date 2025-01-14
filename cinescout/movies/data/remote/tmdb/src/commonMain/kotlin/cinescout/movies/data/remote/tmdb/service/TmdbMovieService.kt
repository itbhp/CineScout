package cinescout.movies.data.remote.tmdb.service

import arrow.core.Either
import arrow.core.left
import cinescout.error.NetworkError
import cinescout.movies.data.remote.tmdb.model.*
import cinescout.movies.domain.model.DiscoverMoviesParams
import cinescout.movies.domain.model.TmdbMovieId
import cinescout.network.Try
import cinescout.network.tmdb.TmdbAuthProvider
import cinescout.network.tmdb.TmdbNetworkQualifier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

@Factory
internal class TmdbMovieService(
    private val authProvider: TmdbAuthProvider,
    @Named(TmdbNetworkQualifier.V3.Client) private val v3client: HttpClient
) {

    suspend fun discoverMovies(params: DiscoverMoviesParams): Either<NetworkError, DiscoverMovies.Response> =
        Either.Try {
            v3client.get {
                url {
                    path("discover", "movie")
                    params.castMember.tap { member -> parameter("with_cast", member.person.tmdbId.value) }
                    params.crewMember.tap { member -> parameter("with_crew", member.person.tmdbId.value) }
                    params.genre.tap { genre -> parameter("with_genres", genre.id.value) }
                    params.keyword.tap { keyword -> parameter("with_keywords", keyword.id.value) }
                    params.releaseYear.tap { releaseYear -> parameter("primary_release_year", releaseYear.value) }
                }
            }.body()
        }

    suspend fun getMovieDetails(id: TmdbMovieId): Either<NetworkError, GetMovieDetails.Response> =
        Either.Try {
            v3client.get { url.path("movie", id.value.toString()) }.body()
        }

    suspend fun getMovieCredits(movieId: TmdbMovieId): Either<NetworkError, GetMovieCredits.Response> =
        Either.Try { v3client.get { url.path("movie", movieId.value.toString(), "credits") }.body() }

    suspend fun getMovieImages(movieId: TmdbMovieId): Either<NetworkError, GetMovieImages.Response> =
        Either.Try { v3client.get { url.path("movie", movieId.value.toString(), "images") }.body() }

    suspend fun getMovieKeywords(movieId: TmdbMovieId): Either<NetworkError, GetMovieKeywords.Response> =
        Either.Try { v3client.get { url.path("movie", movieId.value.toString(), "keywords") }.body() }

    suspend fun getMovieVideos(movieId: TmdbMovieId): Either<NetworkError, GetMovieVideos.Response> =
        Either.Try { v3client.get { url.path("movie", movieId.value.toString(), "videos") }.body() }

    suspend fun getRatedMovies(page: Int): Either<NetworkError, GetRatedMovies.Response> {
        val accountId = authProvider.accountId()
            ?: return NetworkError.Unauthorized.left()
        return Either.Try {
            v3client.get {
                url.path("account", accountId, "rated", "movies")
                parameter("page", page)
            }.body()
        }
    }

    suspend fun getRecommendationsFor(
        movieId: TmdbMovieId,
        page: Int
    ): Either<NetworkError, GetMovieRecommendations.Response> =
        Either.Try {
            v3client.get {
                url.path("movie", movieId.value.toString(), "recommendations")
                parameter("page", page)
            }.body()
        }

    suspend fun getMovieWatchlist(page: Int): Either<NetworkError, GetMovieWatchlist.Response> {
        val accountId = authProvider.accountId()
            ?: return NetworkError.Unauthorized.left()
        return Either.Try {
            v3client.get {
                url { path("account", accountId, "watchlist", "movies") }
                parameter("page", page)
            }.body()
        }
    }

    suspend fun postRating(id: TmdbMovieId, rating: PostRating.Request): Either<NetworkError, Unit> =
        Either.Try {
            v3client.post {
                url.path("movie", id.value.toString(), "rating")
                setBody(rating)
            }.body()
        }

    suspend fun postToWatchlist(id: TmdbMovieId, shouldBeInWatchlist: Boolean): Either<NetworkError, Unit> {
        val accountId = authProvider.accountId()
            ?: return NetworkError.Unauthorized.left()
        val request = PostWatchlist.Request(
            mediaId = "${id.value}",
            mediaType = "movie",
            shouldBeInWatchlist = shouldBeInWatchlist
        )
        return Either.Try {
            v3client.post {
                url.path("account", accountId, "watchlist")
                setBody(request)
            }.body()
        }
    }
}
