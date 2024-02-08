package com.fduhole.danxinative.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxinative.ui.page.CourseSubpage
import com.fduhole.danxinative.ui.page.FDUHoleSubpage
import com.fduhole.danxinative.ui.page.DashboardSubpage
import com.fduhole.danxinative.ui.page.MainPage
import com.fduhole.danxinative.ui.page.SettingsSubpage
import com.fduhole.danxinative.ui.page.TimetableSubpage
import com.fduhole.danxinative.util.LoginStatus

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

