package cinescout.account.tmdb.data.remote

import arrow.core.right
import cinescout.account.tmdb.data.remote.testdata.GetAccountResponseTestData
import cinescout.account.tmdb.domain.testdata.TmdbAccountTestData
import cinescout.auth.tmdb.domain.usecase.CallWithTmdbAccount
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RealTmdbAccountRemoteDataSourceTest {

    private val callWithTmdbAccount = CallWithTmdbAccount(
        appScope = TestScope(context = UnconfinedTestDispatcher()),
        isTmdbLinked = mockk {
            every { this@mockk.invoke() } returns flowOf(true)
        }
    )
    private val service: TmdbAccountService = mockk {
        coEvery { getAccount() } returns GetAccountResponseTestData.Account.right()
    }
    private val dataSource = RealTmdbAccountRemoteDataSource(
        callWithTmdbAccount = callWithTmdbAccount,
        service = service
    )

    @Test
    fun `get account from service`() = runTest {
        // given
        val expected = TmdbAccountTestData.Account.right()

        // when
        val result = dataSource.getAccount()

        // then
        assertEquals(expected, result)
    }
}
