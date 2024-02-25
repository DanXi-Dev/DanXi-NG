package com.fduhole.danxi.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainPage(subpages: List<Subpage>) {

    val pagerState = rememberPagerState(pageCount = { subpages.size })
    val selectedSubpage = subpages[pagerState.currentPage]
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = selectedSubpage.title)) },
                navigationIcon = selectedSubpage.leading,
                actions = selectedSubpage.trailing,
            )
        },
        bottomBar = {
            if (subpages.size <= 1) return@Scaffold
            NavigationBar {
                subpages.forEachIndexed { index, subpage ->
                    val titleText = stringResource(id = subpage.title)
                    NavigationBarItem(
                        icon = { Icon(subpage.icon, contentDescription = titleText) },
                        label = { Text(titleText) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                content = selectedSubpage.body,
            )
        }
    }
}