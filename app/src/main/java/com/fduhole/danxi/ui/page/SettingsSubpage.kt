package com.fduhole.danxi.ui.page

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxi.R
import com.fduhole.danxi.ui.GlobalViewModel
import com.fduhole.danxi.ui.component.settings.AboutCard
import com.fduhole.danxi.ui.component.settings.FDUHoleLoginItem
import com.fduhole.danxi.ui.component.settings.FDUUISLoginItem
import com.fduhole.danxi.ui.component.settings.SettingsCard

class SettingsSubpage(
    private val navController: NavController,
    private val globalViewModel: GlobalViewModel,
) : Subpage {
    override val title = R.string.settings
    override val icon = Icons.Filled.Settings

    override val scrollState = globalViewModel.settingsScrollState

    override val body: @Composable BoxScope.() -> Unit = {
        val fduState by globalViewModel.fudanStateHolder.fduState.collectAsStateWithLifecycle()
        val fduHoleState by globalViewModel.fduHoleState.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxSize(),
        ) {
            ElevatedCard {
                FDUUISLoginItem(fduState, globalViewModel.fudanStateHolder)
                FDUHoleLoginItem(fduHoleState)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            SettingsCard(
                globalViewModel = globalViewModel,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            AboutCard(
                expanded = globalViewModel.aboutExpanded,
                onChangeExpanded = { globalViewModel.aboutExpanded = !globalViewModel.aboutExpanded },
            )
        }
    }


    override val leading: @Composable () -> Unit = {}
    override val trailing: @Composable RowScope.() -> Unit = {}
}