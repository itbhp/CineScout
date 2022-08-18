package store.builder

import arrow.core.right
import kotlinx.coroutines.flow.flowOf
import store.PagedData
import store.PagedStore
import store.PagedStoreImpl
import store.Paging

fun <T> emptyPagedStore(): PagedStore<T, Paging.Page.SingleSource> =
    pagedStoreOf(emptyList())

fun <T> dualSourcesEmptyPagedStore(): PagedStore<T, Paging.Page.DualSources> =
    dualSourcesPagedStoreOf(emptyList())

fun <T> pagedStoreOf(data: List<T>): PagedStore<T, Paging.Page.SingleSource> =
    pagedStoreOf(data.toPagedData(Paging.Page.SingleSource.Initial))

fun <T> dualSourcesPagedStoreOf(data: List<T>): PagedStore<T, Paging.Page.DualSources> =
    pagedStoreOf(data.toPagedData(Paging.Page.DualSources.Initial))

fun <T, P : Paging> pagedStoreOf(data: PagedData<T, P>): PagedStore<T, P> =
    PagedStoreImpl(flow = flowOf(data.right()), onLoadMore = {}, onLoadAll = {})
