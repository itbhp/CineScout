package cinescout.android

import cinescout.android.testutil.homeRobot
import cinescout.android.testutil.runComposeAppTest
import kotlin.test.Test

class DrawerFlowTest {

    @Test
    fun givenDrawerIsOpen_whenAccountsIsClicked_loginIsShown() = runComposeAppTest {
        homeRobot
            .openDrawer()
            .openAccounts()
            .verify {
                loginToTmdbIsDisplayed()
                loginToTraktIsDisplayed()
            }
    }

    @Test
    fun givenDrawerIsOpen_whenAccountsIsClicked_drawerIsClosed() = runComposeAppTest {
        homeRobot
            .openDrawer()
            .openAccounts()
            .closeLogin()
            .verify { drawerIsClosed() }
    }
}
