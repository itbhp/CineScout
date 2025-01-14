package cinescout.android

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter

@OptIn(ExperimentalKermitApi::class)
fun CineScoutLogger(): Logger {
    Logger.setLogWriters(
        LogcatWriter(),
        CrashlyticsLogWriter(
            minCrashSeverity = Severity.Error,
            minSeverity = Severity.Error,
            printTag = true
        )
    )
    return Logger
}
