package com.fduhole.danxinative.ui.fdu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.fduhole.danxinative.model.CardRecord
import com.fduhole.danxinative.repository.fdu.ECardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ECardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ECardUiState())
    val uiState: StateFlow<ECardUiState> = _uiState.asStateFlow()

    fun initModel() {
        _uiState.update {
            it.copy(
                flow = Pager(PagingConfig(pageSize = 10)) { ECardPageSource() }
                    .flow.cachedIn(viewModelScope)
            )
        }
    }
}

class ECardPageSource(private val maxDays: Int = 365) : PagingSource<Int, CardRecord>(), KoinComponent {
    private val backend: ECardRepository by inject()
    private var payload: Map<String, String>? = null
    private var totalPageNum: Int? = null
    override fun getRefreshKey(state: PagingState<Int, CardRecord>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CardRecord> = try {
        if (payload == null || totalPageNum == null) {
            val info = backend.getPagedCardRecordsPayloadAndPageNum(maxDays)
            payload = info.first
            totalPageNum = info.second
        }
        val pageNumber = params.key ?: 1
        val response = backend.getPagedCardRecords(payload!!, pageNumber)
        LoadResult.Page(response, nextKey = if (pageNumber + 1 > (totalPageNum ?: 0)) null else pageNumber + 1, prevKey = null)
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

}