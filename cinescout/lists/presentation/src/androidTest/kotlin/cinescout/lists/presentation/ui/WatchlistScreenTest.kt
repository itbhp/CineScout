package cinescout.lists.presentation.ui

import cinescout.design.TextRes
import cinescout.lists.presentation.model.WatchlistState
import cinescout.test.compose.robot.ListRobot
import cinescout.test.compose.robot.ListRobot.Companion.verify
import cinescout.test.compose.runComposeTest
import studio.forface.cinescout.design.R.string
import kotlin.test.Test

class WatchlistScreenTest {

    @Test
    fun whenLoading_progressIsDisplayed() = runComposeTest {
        val state = WatchlistState.Loading
        ListRobot { WatchlistScreen(state = state) }
            .verify { progressIsDisplayed() }
    }

    @Test
    fun whenError_correctMessageIsDisplayed() = runComposeTest {
        val errorMessage = string.network_error_no_network
        val state = WatchlistState.Error(TextRes(errorMessage))
        ListRobot { WatchlistScreen(state = state) }
            .verify { errorMessageIsDisplayed(errorMessage) }
    }

    @Test
    fun whenEmptyWatchList_emptyWatchlistIsDisplayed() = runComposeTest {
        val state = WatchlistState.Data.Empty
        ListRobot { WatchlistScreen(state = state) }
            .verify { emptyWatchlistIsDisplayed() }
    }
}
