package com.fduhole.danxinative.ui.page.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> LazyPagingColumn(
    lazyPagingFlow: Flow<PagingData<T>>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    val lazyPagingData = lazyPagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        lazyPagingData.refresh()
    }

    LazyColumn(
        modifier = modifier
    ) {
        items(
            count = lazyPagingData.itemCount,
            key = lazyPagingData.itemKey { key(it) }
        ) { index ->
            lazyPagingData[index]?.let {
                content(it)
            }
        }

        when (val refreshLoadState = lazyPagingData.loadState.refresh) {
            LoadState.Loading -> item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> item {
                val error = refreshLoadState.error
                ListItem(headlineContent = {
                    Text(error.toString(), color = MaterialTheme.colorScheme.error)
                })
            }

            else -> {}
        }
        when (val appendLoadState = lazyPagingData.loadState.append) {
            LoadState.Loading -> item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> item {
                val error = appendLoadState.error
                ListItem(headlineContent = {
                    Text(error.toString(), color = MaterialTheme.colorScheme.error)
                })
            }

            else -> {}
        }
    }
}