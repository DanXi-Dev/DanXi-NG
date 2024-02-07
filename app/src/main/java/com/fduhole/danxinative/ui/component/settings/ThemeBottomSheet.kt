package com.fduhole.danxinative.ui.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    onDismissRequest: () -> Unit,
    themeSheetState: SheetState,
    scope: CoroutineScope,
    setDarkTheme: (Boolean?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = themeSheetState,
    ) {
        val hide = {
            scope.launch {
                themeSheetState.hide()
            }.invokeOnCompletion {
                if (!themeSheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
        ListItem(
            headlineContent = {
                Text("Light")
            },
            modifier = Modifier.clickable {
                setDarkTheme(false)
                hide()
            },
        )
        ListItem(
            headlineContent = {
                Text("Dark")
            },
            modifier = Modifier.clickable {
                setDarkTheme(true)
                hide()
            },
        )
        ListItem(
            headlineContent = {
                Text("System")
            },
            modifier = Modifier.clickable {
                setDarkTheme(null)
                hide()
            },
        )
    }
}