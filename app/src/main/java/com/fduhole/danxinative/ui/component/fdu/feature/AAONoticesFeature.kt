package com.fduhole.danxinative.ui.component.fdu.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.navigation.NavController
import com.fduhole.danxinative.R
import com.fduhole.danxinative.ui.DanXiDestinations
import com.fduhole.danxinative.ui.component.fdu.Feature
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@ViewModelScoped
class AAONoticesFeature @Inject constructor() : Feature<Nothing>(
    icon = Icons.Filled.Newspaper,
    title = R.string.fudan_aao_notices,
    shouldLoadData = false,
    shouldNavigateOnClick = true,
) {
    override val mUIState = MutableStateFlow(
        State<Nothing>(clickable = true)
    )

    override fun navigate(navController: NavController) {
        navController.navigate(DanXiDestinations.AAO_NOTICE)
    }
}
