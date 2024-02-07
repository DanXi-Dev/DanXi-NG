package com.fduhole.danxinative.ui.page.fdu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.fdu.AAONotice
import com.fduhole.danxinative.ui.DanXiDestinations
import com.fduhole.danxinative.ui.page.common.LazyPagingColumn
import com.fduhole.danxinative.ui.page.common.NavigationScaffold
import com.fduhole.danxinative.ui.page.common.WebViewModel
import com.fduhole.danxinative.util.uisLoginJavaScript

@Composable
fun AAONoticesPage(
    navController: NavController,
    viewModel: AAONoticesViewModel = hiltViewModel(),
    canNavigateUp: Boolean = false,
    navigateUp: () -> Unit = {},
) {
    NavigationScaffold(
        title = stringResource(R.string.fudan_aao_notices),
        canNavigateUp = canNavigateUp,
        navigateUp = navigateUp,
    ) {
        LazyPagingColumn(
            lazyPagingFlow = viewModel.pagingData,
            key = { it.url },
            modifier = Modifier.padding(8.dp)
        ) {
            AAONoticeItem(it, navController = navController)
        }
    }
}

@Composable
fun AAONoticeItem(
    notice: AAONotice,
    navController: NavController,
) {
    val webViewModel: WebViewModel = hiltViewModel()
    ListItem(
        headlineContent = { Text(notice.title) },
        supportingContent = { Text(notice.time) },
        modifier = Modifier.clickable {
            webViewModel.apply {
                url = notice.url
                javascript = uisLoginJavaScript(globalState.fduUISInfo.value)
                feature = "https://uis.fudan.edu.cn/authserver/login"
            }
            navController.navigate(DanXiDestinations.WEB_VIEW)
        }
    )
}