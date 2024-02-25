package com.fduhole.danxi.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fduhole.danxi.ui.page.MainPage
import com.fduhole.danxi.ui.page.common.WebViewModel
import com.fduhole.danxi.ui.page.common.WebViewPage
import com.fduhole.danxi.ui.page.fdu.AAONoticesPage
import com.fduhole.danxi.ui.page.fdu.FudanECardPage

object DanXiDestinations {
    const val MAIN = "main"
    const val CARD_DETAIL = "card_detail"
    const val AAO_NOTICE = "aao_notice"
    const val WEB_VIEW = "web_view"
}

@Composable
fun DanXiNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    globalViewModel: GlobalViewModel = viewModel(),
    startDestination: String = DanXiDestinations.MAIN,
) {
    val canNavigateUpCheck = { navController.previousBackStackEntry != null }
    val navigateUp: () -> Unit = { navController.navigateUp() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(DanXiDestinations.MAIN) {
            MainPage(
                navController = navController,
                globalViewModel = globalViewModel,
            )
        }
        composable(DanXiDestinations.CARD_DETAIL) {
            FudanECardPage(
                fudanECardFeatureStateHolder = globalViewModel.fudanStateHolder.fudanECardFeature,
                canNavigateUp = canNavigateUpCheck(),
                navigateUp = navigateUp,
            )
        }
        composable(DanXiDestinations.AAO_NOTICE) {
            AAONoticesPage(
                navController = navController,
                canNavigateUp = canNavigateUpCheck(),
                navigateUp = navigateUp,
            )
        }
        composable(DanXiDestinations.WEB_VIEW) {
            val parentEntry = remember { navController.previousBackStackEntry }
            val viewModel: WebViewModel = hiltViewModel(parentEntry ?: it)
            if (viewModel.url != null) {
                WebViewPage(
                    url = viewModel.url!!,
                    javascript = viewModel.javascript,
                    feature = viewModel.feature,
                )
            } else {
                navController.navigateUp()
            }
        }
    }
}