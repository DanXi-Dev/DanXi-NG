package com.fduhole.danxinative.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.fduhole.danxinative.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestSinglePermissionDialog(
    permission: String,
    rationale: String,
    callback: (Boolean) -> Unit = {},
    onDismissRequest: () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission) {
        callback(it)
    }
    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) {
            AlertDialog(
                onDismissRequest = {
                    onDismissRequest()
                    callback(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            permissionState.launchPermissionRequest()
                            onDismissRequest()
                        },
                    ) {
                        Text(stringResource(R.string.accept))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            callback(false)
                            onDismissRequest()
                        },
                    ) {
                        Text(stringResource(R.string.deny))
                    }
                },
                text = {
                    Text(rationale)
                },
            )
        } else {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
        }
    } else {
        callback(true)
        onDismissRequest()
    }
}