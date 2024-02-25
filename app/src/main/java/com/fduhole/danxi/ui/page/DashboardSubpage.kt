package com.fduhole.danxi.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxi.R
import com.fduhole.danxi.ui.GlobalViewModel
import com.fduhole.danxi.ui.component.fdu.Feature
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class DashboardSubpage(
    navController: NavController,
    globalViewModel: GlobalViewModel,
) : Subpage {
    override val title = R.string.dashboard
    override val icon = Icons.Filled.Home
    override val scrollState = globalViewModel.dashboardScrollState

    @OptIn(ExperimentalMaterial3Api::class)
    override val body: @Composable BoxScope.() -> Unit = {
        val featureStateHolders = globalViewModel.fudanStateHolder.features
        val pullToRefreshState = rememberPullToRefreshState()
        val haptics = LocalHapticFeedback.current
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(Unit) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(0.25.seconds)
                pullToRefreshState.endRefresh()
            }
        }

        Box(
            Modifier
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                ElevatedCard {
                    featureStateHolders.forEach {
                        val state by it.uiState.collectAsStateWithLifecycle()

                        // load data on start
                        if (state.state is Feature.Status.Idle && it.shouldLoadData) {
                            it.onStart(navController)
                        }

                        if (pullToRefreshState.isRefreshing) {
                            LaunchedEffect(Unit) {
                                it.onRefresh(navController)
                            }
                        }

                        val title = stringResource(id = it.title)
                        val subTitle: String = it.subTitleDefault ?: when (state.state) {
                            Feature.Status.Idle -> stringResource(id = R.string.tap_to_view)
                            Feature.Status.Loading -> stringResource(id = R.string.loading)
                            is Feature.Status.Error -> stringResource(id = R.string.failed_to_load)
                            is Feature.Status.Success -> (state.state as Feature.Status.Success<Any?>).message
                        }
                        ListItem(
                            headlineContent = { Text(title) },
                            supportingContent = { Text(subTitle) },
                            leadingContent = { Icon(it.icon, contentDescription = title) },
                            trailingContent = it.trailingContent,
                            modifier = Modifier.clickable(enabled = state.clickable) {
                                it.onClick(navController)
                            }
                        )

                        if (state.showMoreContent) {
                            it.moreContent(navController)
                        }
                    }
                }
            }
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    override val leading: @Composable () -> Unit = {}
    override val trailing: @Composable RowScope.() -> Unit = {}
}