package com.fduhole.danxi.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxi.ui.GlobalViewModel
import com.fduhole.danxi.util.LoginStatus

@Composable
fun MainPage(
    navController: NavController,
    globalViewModel: GlobalViewModel,
) {
    val fduState by globalViewModel.fudanStateHolder.fduState.collectAsStateWithLifecycle()
    val fduHoleState by globalViewModel.fduHoleState.collectAsStateWithLifecycle()
    val subpages = buildList {
        if (fduState is LoginStatus.Success) {
            add(
                DashboardSubpage(
                    navController = navController,
                    globalViewModel = globalViewModel,
                )
            )
        }
        if (fduHoleState is LoginStatus.Success) {
            add(FDUHoleSubpage(globalViewModel))
            add(CourseSubpage(globalViewModel))
        }
        if (fduState is LoginStatus.Success) {
            add(TimetableSubpage(globalViewModel))
        }
        add(
            SettingsSubpage(
                navController = navController,
                globalViewModel = globalViewModel,
            )
        )
    }

    MainPageContent(subpages)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageContent(subpages: List<Subpage>) {
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    if (currentPage >= subpages.size) {
        currentPage = 0
    }
    val selectedSubpage = subpages[currentPage]
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
                        selected = currentPage == index,
                        onClick = {
                            currentPage = index
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            content = selectedSubpage.body,
        )
    }
}