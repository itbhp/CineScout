package cinescout.tvshows.domain.usecase

import cinescout.tvshows.domain.TvShowRepository
import cinescout.tvshows.domain.model.TvShow
import org.koin.core.annotation.Factory
import store.PagedStore
import store.Paging
import store.Refresh
import kotlin.time.Duration.Companion.days

@Factory
class GetAllWatchlistTvShows(
    private val tvShowRepository: TvShowRepository
) {

    operator fun invoke(refresh: Refresh = Refresh.IfExpired(1.days)): PagedStore<TvShow, Paging> =
        tvShowRepository.getAllWatchlistTvShows(refresh)
}
