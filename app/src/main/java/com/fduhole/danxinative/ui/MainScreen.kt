package com.fduhole.danxinative.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxinative.ui.page.CourseSubpage
import com.fduhole.danxinative.ui.page.FDUHoleSubpage
import com.fduhole.danxinative.ui.page.DashboardSubpage
import com.fduhole.danxinative.ui.page.MainPage
import com.fduhole.danxinative.ui.page.SettingsSubpage
import com.fduhole.danxinative.ui.page.TimetableSubpage
import com.fduhole.danxinative.util.LoginState

@Composable
fun MainScreen(
    navController: NavController,
    globalViewModel: GlobalViewModel,
) {
    val fduState = globalViewModel.fudanStateHolder.fduState.collectAsStateWithLifecycle()
    val fduHoleState = globalViewModel.fduHoleState.collectAsStateWithLifecycle()
    val subpages = buildList {
        if (fduState.value is LoginState.Success) {
            add(DashboardSubpage(
                navController = navController,
                globalViewModel = globalViewModel,
            ))
        }
        if (fduHoleState.value is LoginState.Success) {
            add(FDUHoleSubpage(globalViewModel))
            add(CourseSubpage(globalViewModel))
        }
        if (fduState.value is LoginState.Success) {
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
