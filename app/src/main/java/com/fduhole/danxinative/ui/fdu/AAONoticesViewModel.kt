package com.fduhole.danxinative.ui.fdu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.fduhole.danxinative.model.AAONotice
import com.fduhole.danxinative.repository.fdu.AAORepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AAONoticesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AAONoticesUiState())
    val uiState: StateFlow<AAONoticesUiState> = _uiState.asStateFlow()
    fun initModel() {
        _uiState.update {
            it.copy(
                flow = Pager(PagingConfig(pageSize = 10)) { AAONoticePageSource() }
                    .flow.cachedIn(viewModelScope)
            )
        }
    }

}

class AAONoticePageSource : PagingSource<Int, AAONotice>(), KoinComponent {
    private val backend: AAORepository by inject()
    override fun getRefreshKey(state: PagingState<Int, AAONotice>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AAONotice> = try {
        val pageNumber = params.key ?: 1
        val response = backend.getNoticeList(pageNumber)
        LoadResult.Page(response, nextKey = if (response.isEmpty()) null else pageNumber + 1, prevKey = null)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

}