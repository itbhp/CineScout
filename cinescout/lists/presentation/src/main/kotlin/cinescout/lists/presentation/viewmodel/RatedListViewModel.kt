package cinescout.lists.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import cinescout.design.NetworkErrorToMessageMapper
import cinescout.error.DataError
import cinescout.lists.presentation.mapper.ListItemUiModelMapper
import cinescout.lists.presentation.model.RatedListAction
import cinescout.lists.presentation.model.RatedListState
import cinescout.movies.domain.usecase.GetAllRatedMovies
import cinescout.unsupported
import cinescout.utils.android.CineScoutViewModel
import cinescout.utils.kotlin.nonEmptyUnsafe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import store.Refresh

internal class RatedListViewModel(
    private val errorToMessageMapper: NetworkErrorToMessageMapper,
    private val getAllRatedMovies: GetAllRatedMovies,
    private val listItemUiModelMapper: ListItemUiModelMapper
) : CineScoutViewModel<RatedListAction, RatedListState>(RatedListState.Loading) {

    init {
        viewModelScope.launch {
            getAllRatedMovies(refresh = Refresh.WithInterval()).loadAll().map { moviesEither ->
                moviesEither.fold(
                    ifLeft = { error -> error.toErrorState() },
                    ifRight = { movies ->
                        val items = movies.data.map(listItemUiModelMapper::toUiModel)
                        if (items.isEmpty()) RatedListState.Data.Empty
                        else RatedListState.Data.NotEmpty(items.nonEmptyUnsafe())
                    }
                )
            }.collect { newState ->
                updateState { newState }
            }
        }
    }

    private fun DataError.toErrorState(): RatedListState.Error = when (this) {
        DataError.Local.NoCache -> unsupported
        is DataError.Remote -> RatedListState.Error(errorToMessageMapper.toMessage(networkError))
    }

    override fun submit(action: RatedListAction) {
        // No actions
    }
}
