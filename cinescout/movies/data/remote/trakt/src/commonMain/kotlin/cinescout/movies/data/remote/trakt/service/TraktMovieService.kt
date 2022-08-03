package cinescout.movies.data.remote.trakt.service

import arrow.core.Either
import cinescout.error.NetworkError
import cinescout.movies.data.remote.trakt.model.GetRatings
import cinescout.network.Try
import cinescout.network.trakt.getPaging
import cinescout.store.PagedData
import cinescout.store.Paging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path

internal class TraktMovieService(
    private val client: HttpClient
) {

    suspend fun getRatedMovies(
        page: Int
    ): Either<NetworkError, PagedData.Remote<GetRatings.Result.Movie, Paging.Page.SingleSource>> =
        Either.Try {
            val response = client.get {
                url { path("sync", "ratings", "movies") }
                parameter("page", page)
            }
            PagedData.Remote(data = response.body(), paging = response.headers.getPaging())
        }
}
