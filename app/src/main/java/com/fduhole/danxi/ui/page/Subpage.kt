package com.fduhole.danxi.ui.page

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface Subpage {
    @get:StringRes
    val title: Int
    val icon: ImageVector
    val scrollState: ScrollState
    val leading: @Composable () -> Unit
    val trailing: @Composable RowScope.() -> Unit
    val body: @Composable BoxScope.() -> Unit
}
