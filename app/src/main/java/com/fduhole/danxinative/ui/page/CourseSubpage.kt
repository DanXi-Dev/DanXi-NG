package com.fduhole.danxinative.ui.page

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.runtime.Composable
import com.fduhole.danxinative.R
import com.fduhole.danxinative.ui.GlobalViewModel

class CourseSubpage(
    globalViewModel: GlobalViewModel,
) : Subpage {
    override val title = R.string.course
    override val icon = Icons.Filled.EggAlt
    override val scrollState = globalViewModel.danKeScrollState
    override val body: @Composable BoxScope.() -> Unit = {}
    override val leading: @Composable () -> Unit = {}
    override val trailing: @Composable RowScope.() -> Unit = {}
}