package com.fduhole.danxi.ui.component.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.fduhole.danxi.R
import com.fduhole.danxi.ui.GlobalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCard(
    globalViewModel: GlobalViewModel
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showThemeBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showLanguageBottomSheet by rememberSaveable { mutableStateOf(false) }
    val expanded = globalViewModel.settingsExpanded
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val settingsText = stringResource(id = R.string.settings)
        ListItem(
            headlineContent = {
                Text(settingsText)
            },
            leadingContent = {
                Icon(Icons.Filled.Settings, contentDescription = settingsText)
            },
            trailingContent = {
                val icon = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
                Icon(icon, contentDescription = settingsText)
            },
            modifier = Modifier.clickable {
                globalViewModel.settingsExpanded = !expanded
            },
        )

        if (expanded) {
            val languageText = stringResource(id = R.string.language)
            ListItem(
                headlineContent = {
                    Text(languageText)
                },
                leadingContent = {
                    Icon(Icons.Filled.Language, contentDescription = languageText)
                },
                modifier = Modifier.clickable {
                    showLanguageBottomSheet = true
                },
            )

            val themeText = stringResource(id = R.string.theme)
            ListItem(
                headlineContent = {
                    Text(themeText)
                },
                leadingContent = {
                    Icon(Icons.Filled.Brightness4, contentDescription = themeText)
                },
                modifier = Modifier.clickable {
                    showThemeBottomSheet = true
                },
            )
        }
    }

    if (showThemeBottomSheet) {
        ThemeBottomSheet(
            onDismissRequest = { showThemeBottomSheet = false },
            themeSheetState = sheetState,
            scope = scope,
            setDarkTheme = globalViewModel::setDarkTheme,
        )
    }

    if (showLanguageBottomSheet) {
        LanguageBottomSheet(
            scope = scope,
            onDismissRequest = { showLanguageBottomSheet = false },
            themeSheetState = sheetState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    onDismissRequest: () -> Unit,
    themeSheetState: SheetState,
    scope: CoroutineScope,
    setDarkTheme: (Boolean?) -> Unit,
) {
    val themeOptions = mapOf(
        R.string.light to false,
        R.string.dark to true,
        R.string.follow_system to null,
    )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = themeSheetState,
    ) {
        val hide = {
            scope.launch {
                themeSheetState.hide()
                onDismissRequest()
            }.invokeOnCompletion {
                if (!themeSheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
        themeOptions.forEach { (resId, darkTheme) ->
            ListItem(
                headlineContent = { Text(stringResource(resId)) },
                modifier = Modifier.clickable {
                    hide()
                    setDarkTheme(darkTheme)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    scope: CoroutineScope,
    onDismissRequest: () -> Unit,
    themeSheetState: SheetState,
) {
    val localeOptions = mapOf(
        R.string.en to "en",
        R.string.zh to "zh",
    )
    val hide = {
        scope.launch {
            themeSheetState.hide()
            onDismissRequest()
        }.invokeOnCompletion {
            if (!themeSheetState.isVisible) {
                onDismissRequest()
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = themeSheetState,
    ) {
        localeOptions.forEach { (resId, locale) ->
            ListItem(
                headlineContent = { Text(stringResource(resId)) },
                modifier = Modifier.clickable {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(locale)
                    )
                    hide()
                },
            )
        }
    }
}