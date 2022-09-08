package cinescout.test.compose.robot

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import cinescout.test.compose.util.onNodeWithText
import studio.forface.cinescout.design.R.string

class ListRobot<T : ComponentActivity> internal constructor(
    composeTest: AndroidComposeUiTest<T>
) : HomeRobot<T>(composeTest) {

    class Verify<T : ComponentActivity>(composeTest: AndroidComposeUiTest<T>) : HomeRobot.Verify<T>(composeTest) {

        fun emptyWatchlistIsDisplayed() {
            composeTest.onNodeWithText(string.lists_watchlist_empty)
                .assertIsDisplayed()
        }

        fun noRatedMoviesIsDisplayed() {
            composeTest.onNodeWithText(string.lists_rated_empty)
                .assertIsDisplayed()
        }
    }

    companion object {

        fun <T : ComponentActivity> ListRobot<T>.verify(
            block: ListRobot.Verify<T>.() -> Unit
        ): ListRobot<T> =
            also { ListRobot.Verify(composeTest).block() }
    }
}

fun <T : ComponentActivity> AndroidComposeUiTest<T>.ListRobot(content: @Composable () -> Unit) =
    ListRobot(this).also { setContent(content) }