package cinescout.tvshows.domain.usecase

import cinescout.tvshows.domain.TvShowRepository
import cinescout.tvshows.domain.model.TvShow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAllLikedTvShows(
    private val tvShowRepository: TvShowRepository
) {

    operator fun invoke(): Flow<List<TvShow>> =
        tvShowRepository.getAllLikedTvShows()
}
