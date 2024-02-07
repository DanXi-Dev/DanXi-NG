package com.fduhole.danxinative.ui.page.fdu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.fduhole.danxinative.model.fdu.AAONotice
import com.fduhole.danxinative.repository.fdu.AAORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AAONoticesViewModel @Inject constructor(
    private val repository: AAORepository,
) : ViewModel() {
    val pagingData by lazy {
        Pager(PagingConfig(pageSize = 10)) { AAONoticePageSource(repository) }
            .flow.cachedIn(viewModelScope)
    }
}

class AAONoticePageSource(
    private val repository: AAORepository,
) : PagingSource<Int, AAONotice>() {
    override fun getRefreshKey(state: PagingState<Int, AAONotice>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AAONotice> = try {
        val pageNumber = params.key ?: 1
        val response = repository.getNoticeList(pageNumber)
        LoadResult.Page(response, nextKey = if (response.isEmpty()) null else pageNumber + 1, prevKey = null)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }
}