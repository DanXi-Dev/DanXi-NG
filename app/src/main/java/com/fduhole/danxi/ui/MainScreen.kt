package com.fduhole.danxi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxi.ui.page.CourseSubpage
import com.fduhole.danxi.ui.page.FDUHoleSubpage
import com.fduhole.danxi.ui.page.DashboardSubpage
import com.fduhole.danxi.ui.page.MainPage
import com.fduhole.danxi.ui.page.SettingsSubpage
import com.fduhole.danxi.ui.page.TimetableSubpage
import com.fduhole.danxi.util.LoginStatus

@Composable
fun MainScreen(
    navController: NavController,
    globalViewModel: GlobalViewModel,
) {
    val fduState by globalViewModel.fudanStateHolder.fduState.collectAsStateWithLifecycle()
    val fduHoleState by globalViewModel.fduHoleState.collectAsStateWithLifecycle()
    val subpages = buildList {
        if (fduState is LoginStatus.Success) {
            add(DashboardSubpage(
                navController = navController,
                globalViewModel = globalViewModel,
            ))
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
    MainPage(subpages)
}

