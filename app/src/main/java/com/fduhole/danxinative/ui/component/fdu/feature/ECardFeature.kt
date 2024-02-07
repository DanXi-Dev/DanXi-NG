package com.fduhole.danxinative.ui.component.fdu.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.fdu.CardPersonInfo
import com.fduhole.danxinative.model.fdu.CardRecord
import com.fduhole.danxinative.repository.fdu.ECardRepository
import com.fduhole.danxinative.ui.DanXiDestinations
import com.fduhole.danxinative.ui.component.fdu.Feature
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class ECardFeature @Inject constructor(
    val repository: ECardRepository,
) : Feature<CardPersonInfo>(
    icon = Icons.Filled.CreditCard,
    title = R.string.fudan_ecard_balance,
    shouldNavigateOnClick = true,
) {
    override val mUIState = MutableStateFlow(
        State<CardPersonInfo>(clickable = true)
    )

    override fun onStart(navController: NavController) {
        scope.launch { loading() }
    }

    override fun onRefresh(navController: NavController) {
        scope.launch { loading() }
    }

    override suspend fun loadData(): Result<Status<CardPersonInfo>> = runCatching {
        val info = repository.getCardPersonInfo()
        Status.Success(
            message = info.recentRecord.firstOrNull()?.let { "￥${it.amount} ${it.location}" } ?: "无消费记录",
            data = info,
        )
    }

    override val trailingContent: @Composable () -> Unit = {
        val state = uiState.collectAsStateWithLifecycle().value
        when (state.state) {
            Status.Loading -> CircularProgressIndicator()
            is Status.Success -> {
                Text("￥${state.state.data.balance}")
            }

            else -> {}
        }
    }

    val lazyPagingCardRecords by lazy {
        Pager(PagingConfig(pageSize = 10)) { ECardPageSource(repository) }.flow
    }

    override fun navigate(navController: NavController) {
        navController.navigate(DanXiDestinations.CARD_DETAIL)
    }
}

class ECardPageSource(
    private val repository: ECardRepository,
) : PagingSource<Int, CardRecord>() {
    private val maxDays: Int = 365
    private var payload: Map<String, String>? = null
    private var totalPageNum: Int? = null
    override fun getRefreshKey(state: PagingState<Int, CardRecord>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CardRecord> = try {
        if (payload == null || totalPageNum == null) {
            val info = repository.getPagedCardRecordsPayloadAndPageNum(maxDays)
            payload = info.first
            totalPageNum = info.second
        }
        val pageNumber = params.key ?: 1
        val response = repository.getPagedCardRecords(payload!!, pageNumber)
        LoadResult.Page(
            response,
            nextKey = if (pageNumber + 1 > (totalPageNum ?: 0)) null else pageNumber + 1,
            prevKey = null,
        )
    } catch (e: Throwable) {
        LoadResult.Error(e)
    }

}