package cinescout.account.tmdb.data.remote

import arrow.core.Either
import cinescout.account.domain.model.Gravatar
import cinescout.account.tmdb.data.TmdbAccountRemoteDataSource
import cinescout.account.tmdb.domain.model.TmdbAccount
import cinescout.account.tmdb.domain.model.TmdbAccountUsername
import cinescout.auth.tmdb.domain.usecase.CallWithTmdbAccount
import cinescout.model.NetworkOperation
import org.koin.core.annotation.Factory

@Factory
class RealTmdbAccountRemoteDataSource(
    private val callWithTmdbAccount: CallWithTmdbAccount,
    private val service: TmdbAccountService
) : TmdbAccountRemoteDataSource {

    override suspend fun getAccount(): Either<NetworkOperation, TmdbAccount> =
        callWithTmdbAccount {
            service.getAccount().map { response ->
                TmdbAccount(
                    gravatar = response.avatar?.gravatar?.hash?.let(::Gravatar),
                    username = TmdbAccountUsername(response.username)
                )
            }
        }
}
