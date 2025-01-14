package cinescout.suggestions.presentation.worker

import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import cinescout.common.model.SuggestionError
import cinescout.suggestions.domain.model.SuggestionsMode
import cinescout.suggestions.domain.usecase.UpdateSuggestedMovies
import cinescout.suggestions.domain.usecase.UpdateSuggestedTvShows
import cinescout.suggestions.presentation.usecase.BuildUpdateSuggestionsErrorNotification
import cinescout.suggestions.presentation.usecase.BuildUpdateSuggestionsForegroundNotification
import cinescout.suggestions.presentation.usecase.BuildUpdateSuggestionsSuccessNotification
import cinescout.utils.android.createOutput
import cinescout.utils.android.requireInput
import cinescout.utils.android.setInput
import co.touchlab.kermit.Logger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

class UpdateSuggestionsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val analytics: FirebaseAnalytics,
    private val buildUpdateSuggestionsErrorNotification: BuildUpdateSuggestionsErrorNotification,
    private val buildUpdateSuggestionsForegroundNotification: BuildUpdateSuggestionsForegroundNotification,
    private val buildUpdateSuggestionsSuccessNotification: BuildUpdateSuggestionsSuccessNotification,
    private val ioDispatcher: CoroutineDispatcher,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val updateSuggestedMovies: UpdateSuggestedMovies,
    private val updateSuggestedTvShows: UpdateSuggestedTvShows
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val input = requireInput<SuggestionsMode>()
        setForeground()
        val (result, time) = measureTimedValue {
            withTimeoutOrNull(10.minutes) {
                updateSuggestedMovies(input).flatMap {
                    updateSuggestedTvShows(input)
                }
            } ?: SuggestionError.NoSuggestions.left()
        }
        handleResult(input, time, result)
        return@withContext toWorkerResult(result)
    }

    private suspend fun setForeground() {
        val (notification, notificationId) = buildUpdateSuggestionsForegroundNotification()
        val foregroundInfo = ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        setForeground(foregroundInfo)
    }

    private fun handleResult(input: SuggestionsMode, time: Duration, result: Either<SuggestionError, Unit>) {
        logAnalytics(input, time, result)
        result
            .tap {
                Logger.i("Successfully updated suggestions for ${input.name}")
                val (notification, notificationId) = buildUpdateSuggestionsSuccessNotification()
                notificationManagerCompat.notify(notificationId, notification)
            }
            .tapLeft { error ->
                Logger.e("Error updating suggestions for ${input.name}: $error")
                val (notification, notificationId) = buildUpdateSuggestionsErrorNotification()
                notificationManagerCompat.notify(notificationId, notification)
            }
    }

    private fun logAnalytics(
        input: SuggestionsMode,
        time: Duration,
        result: Either<SuggestionError, Unit>
    ) {
        analytics.logEvent(Analytics.EventName) {
            param(Analytics.TypeParameter, input.name)
            param(Analytics.TimeParameter, time.inWholeSeconds)
            param(
                Analytics.ResultParameter,
                result.fold(ifLeft = { "${Analytics.ErrorWithReason}$it" }, ifRight = { Analytics.Success })
            )
        }
    }

    private fun toWorkerResult(result: Either<SuggestionError, Unit>): Result = result.fold(
        ifRight = { Result.success() },
        ifLeft = { error ->
            when {
                runAttemptCount < MaxAttempts -> Result.retry()
                else -> Result.failure(createOutput(error.toString()))
            }
        }
    )

    class Scheduler(private val workManager: WorkManager) {

        operator fun invoke(suggestionsMode: SuggestionsMode) {
            when (suggestionsMode) {
                SuggestionsMode.Deep -> schedulePeriodic()
                SuggestionsMode.Quick -> scheduleExpedited()
            }
        }

        private fun scheduleExpedited() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val requestBuilder = OneTimeWorkRequestBuilder<UpdateSuggestionsWorker>()

            val request = requestBuilder
                .setConstraints(constraints)
                .setInput(SuggestionsMode.Quick)
                .setBackoffCriteria(BackoffPolicy.LINEAR, Backoff.toJavaDuration())
                .setInitialRunAttemptCount(MaxAttempts)
                .build()

            workManager.enqueueUniqueWork(
                ExpeditedName,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        private fun schedulePeriodic() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(true)
                .build()

            val requestBuilder = PeriodicWorkRequestBuilder<UpdateSuggestionsWorker>(
                repeatInterval = Interval.toJavaDuration(),
                flexTimeInterval = FlexInterval.toJavaDuration()
            )

            val request = requestBuilder
                .setConstraints(constraints)
                .setInput(SuggestionsMode.Deep)
                .setInitialRunAttemptCount(MaxAttempts)
                .build()

            workManager.enqueueUniquePeriodicWork(
                PeriodicName,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    companion object {

        const val MaxAttempts = 5
        const val ExpeditedName = "UpdateSuggestionsWorker.expedited"
        const val PeriodicName = "UpdateSuggestionsWorker.periodic"
        val Backoff = 10.seconds
        val Interval = 1.hours
        val FlexInterval = 3.hours
    }

    private object Analytics {

        const val ErrorWithReason = "Error: "
        const val EventName = "update_suggestions"
        const val ResultParameter = "result"
        const val Success = "Success"
        const val TimeParameter = "time_seconds"
        const val TypeParameter = "type"
    }
}
