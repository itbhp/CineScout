package cinescout.store

import cinescout.utils.kotlin.DataRefreshInterval
import kotlin.time.Duration

sealed interface Refresh {

    /**
     * Always fetch from the remote source with the defined [interval]
     * @param interval the interval to fetch from the remote source
     *  Default is [DataRefreshInterval]
     */
    data class WithInterval(val interval: Duration = DataRefreshInterval) : Refresh

    /**
     * Always fetch once from the remove source
     */
    object Once : Refresh

    /**
     * Fetch from the remote source if the local source is empty
     */
    object IfNeeded : Refresh

    /**
     * Never fetch from the remote source, only emit from local source
     */
    object Never : Refresh
}