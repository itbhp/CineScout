package cinescout.network.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.annotation.Single

@Single
actual class ObserveNetworkStatusChanges {

    actual operator fun invoke(): Flow<Unit> =
        flowOf(Unit)
}
