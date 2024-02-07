package com.fduhole.danxinative.ui.component.settings

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
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fduhole.danxinative.R

@Composable
fun SettingsCard(
    expanded: Boolean,
    onChangeExpanded: () -> Unit,
    onShowBottomSheet: () -> Unit,
) {
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
                onChangeExpanded()
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
                    onShowBottomSheet()
                },
            )
        }
    }
}