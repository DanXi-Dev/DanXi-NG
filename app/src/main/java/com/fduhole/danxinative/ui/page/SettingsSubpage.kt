package com.fduhole.danxinative.ui.page

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxinative.R
import com.fduhole.danxinative.repository.fdu.BaseFDURepository
import com.fduhole.danxinative.ui.GlobalViewModel
import com.fduhole.danxinative.ui.component.settings.AboutCard
import com.fduhole.danxinative.ui.component.settings.FDUHoleLoginItem
import com.fduhole.danxinative.ui.component.settings.UISLoginDialog
import com.fduhole.danxinative.ui.component.settings.FDUUISLoginItem
import com.fduhole.danxinative.ui.component.settings.SettingsCard
import com.fduhole.danxinative.ui.component.settings.ThemeBottomSheet
import com.fduhole.danxinative.util.LoginState

class SettingsSubpage(
    private val navController: NavController,
    private val globalViewModel: GlobalViewModel,
) : Subpage {
    override val title = R.string.settings
    override val icon = Icons.Filled.Settings

    override val scrollState = globalViewModel.settingsScrollState

    @OptIn(ExperimentalMaterial3Api::class)
    override val body: @Composable BoxScope.() -> Unit = {
        val fduState = globalViewModel.fudanStateHolder.fduState.collectAsStateWithLifecycle()
        val fduHoleState = globalViewModel.fduHoleState.collectAsStateWithLifecycle()

        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        var showThemeBottomSheet by rememberSaveable { mutableStateOf(false) }

        var showFDUUISLoginDialog by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp)
                .fillMaxSize(),
        ) {
            ElevatedCard {
                FDUUISLoginItem(fduState.value) { showFDUUISLoginDialog = true }
                FDUHoleLoginItem(fduHoleState.value)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            SettingsCard(
                expanded = globalViewModel.settingsExpanded,
                onChangeExpanded = { globalViewModel.settingsExpanded = !globalViewModel.settingsExpanded },
                onShowBottomSheet = { showThemeBottomSheet = true }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            AboutCard(
                expanded = globalViewModel.aboutExpanded,
                onChangeExpanded = { globalViewModel.aboutExpanded = !globalViewModel.aboutExpanded },
            )
        }

        if (showThemeBottomSheet) {
            ThemeBottomSheet(
                onDismissRequest = { showThemeBottomSheet = false },
                themeSheetState = sheetState,
                scope = scope,
                setDarkTheme = { darkTheme ->
                    globalViewModel.setDarkTheme(darkTheme)
                },
            )
        }

        if (showFDUUISLoginDialog) {
            val errorMessage = if (fduState.value is LoginState.Error) {
                val error = (fduState.value as LoginState.Error).error
                if (error is BaseFDURepository.Companion.UISLoginException) {
                    stringResource(id = error.id)
                } else {
                    error.message ?: ""
                }
            } else ""
            UISLoginDialog(
                onDismissRequest = { showFDUUISLoginDialog = false },
                onLogin = { id, password ->
                    globalViewModel.fudanStateHolder.loginFDUUIS(id, password)
                },
                enabled = fduState.value !is LoginState.Loading,
                errorMessage = errorMessage,
            )
        }
    }


    override val leading: @Composable () -> Unit = {}
    override val trailing: @Composable RowScope.() -> Unit = {}
}