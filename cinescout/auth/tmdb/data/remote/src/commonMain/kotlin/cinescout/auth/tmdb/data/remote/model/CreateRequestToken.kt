package cinescout.auth.tmdb.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface CreateRequestToken {

    @Serializable
    data class Response(

        @SerialName(RequestToken)
        val requestToken: String
    ) {

        companion object {

            const val RequestToken = "request_token"
        }
    }
}
