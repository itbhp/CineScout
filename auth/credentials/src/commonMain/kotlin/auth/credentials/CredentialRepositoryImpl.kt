package auth.credentials

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import database.credentials.TmdbCredentialQueries
import entities.TmdbStringId
import entities.auth.CredentialRepository
import kotlinx.coroutines.flow.Flow

internal class CredentialRepositoryImpl(
    private val tmdbCredentials: TmdbCredentialQueries
) : CredentialRepository {

    private var cachedAccountId: TmdbStringId? = null
    private var cachedSessionId: String? = null
    private var cachedTmdbAccessToken: String? = null

    override fun findTmdbAccessTokenBlocking(): String? =
        cachedTmdbAccessToken
            ?: tmdbCredentials.selectAccessToken().executeAsOneOrNull()

    override fun findTmdbAccountId(): Flow<TmdbStringId?> =
        tmdbCredentials.selectAccountId().asFlow().mapToOneOrNull()

    override fun findTmdbAccountIdBlocking(): TmdbStringId? =
        cachedAccountId
            ?: tmdbCredentials.selectAccountId().executeAsOneOrNull()

    override fun findTmdbSessionIdBlocking(): String? =
        cachedSessionId
            ?: tmdbCredentials.selectSessionId().executeAsOneOrNull()

    override suspend fun storeTmdbCredentials(accountId: TmdbStringId, token: String, sessionId: String) {
        cachedAccountId = accountId
        cachedSessionId = sessionId
        cachedTmdbAccessToken = token
        tmdbCredentials.insert(accountId, token, sessionId)
    }

    override suspend fun deleteTmdbAccessToken() {
        cachedTmdbAccessToken = null
        tmdbCredentials.delete()
    }
}
