package com.fduhole.danxinative.ui.component.fdu.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.fdu.LibraryInfo
import com.fduhole.danxinative.repository.fdu.LibraryRepository
import com.fduhole.danxinative.ui.component.fdu.Feature
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@ViewModelScoped
class LibraryAttendanceFeature @Inject constructor(
    val repository: LibraryRepository,
) : Feature<List<LibraryInfo>>(
    icon = Icons.Filled.LocalLibrary,
    title = R.string.fudan_library_attendance,
    hasMoreContent = true,
) {
    override val mUIState = MutableStateFlow(
        State<List<LibraryInfo>>(clickable = true)
    )

    override suspend fun loadData() = runCatching {
        val attendanceList = repository.getAttendance()
        Status.Success(
            message = attendanceList.fold(StringBuilder()) { builder, info ->
                builder.append("${info.campusName}: ${info.inNum} ")
            }.toString(),
            data = attendanceList,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override val moreContent: @Composable (navController: NavController) -> Unit = {
        ModalBottomSheet(
            onDismissRequest = { mUIState.update { it.copy(showMoreContent = false) } },
        ) {
            val state by uiState.collectAsStateWithLifecycle()
            val supportingText = if (state.state is Status.Success) {
                (state.state as Status.Success<List<LibraryInfo>>)
                    .data.fold(StringBuilder()) { builder, info ->
                        builder.append("${info.campusName}: ${info.inNum}\n")
                    }.toString()
            } else {
                ""
            }
            ListItem(
                headlineContent = { Text(stringResource(id = title)) },
                supportingContent = { Text(supportingText) }
            )
        }
    }
}