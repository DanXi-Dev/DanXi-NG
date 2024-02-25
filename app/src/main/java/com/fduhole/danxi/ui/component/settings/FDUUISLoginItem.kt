package com.fduhole.danxi.ui.component.settings

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fduhole.danxi.model.fdu.UISInfo
import com.fduhole.danxi.repository.fdu.BaseFDURepository
import com.fduhole.danxi.ui.component.fdu.FudanStateHolder
import com.fduhole.danxi.util.LoginStatus

@Composable
fun FDUUISLoginItem(
    fduState: LoginStatus<out UISInfo>,
    fudanStateHolder: FudanStateHolder,
) {
    var showUISLoginDialog by rememberSaveable { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text("复旦 UIS 账号")
        },
        supportingContent = {
            when (fduState) {
                is LoginStatus.Error -> {
                    Text("登录失败")
                }

                LoginStatus.Loading -> {
                    Text("登录中")
                }

                LoginStatus.NotLogin -> {
                    Text("未登录")
                }

                is LoginStatus.Success -> {
                    val name = fduState.data.name
                    val id = fduState.data.id
                    Text("已登录 - $name($id)")
                }
            }
        },
        leadingContent = {
            Icon(Icons.Filled.AccountCircle, contentDescription = "复旦 UIS 账号")
        },
        trailingContent = {
            when (fduState) {
                is LoginStatus.Error -> {
                    Icon(Icons.Filled.Error, contentDescription = "复旦 UIS 账号 - 登录失败")
                }

                LoginStatus.Loading -> {
                    CircularProgressIndicator()
                }

                LoginStatus.NotLogin -> {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "复旦 UIS 账号 - 登录")
                }

                is LoginStatus.Success -> {
                    Icon(Icons.Filled.Check, contentDescription = "复旦 UIS 账号 - 登录成功")
                }
            }
        },
        modifier = Modifier.clickable {
            showUISLoginDialog = true
        },
    )

    if (showUISLoginDialog) {
        val errorMessage = if (fduState is LoginStatus.Error) {
            val error = fduState.error
            if (error is BaseFDURepository.Companion.UISLoginException) {
                stringResource(id = error.id)
            } else {
                error.message ?: ""
            }
        } else ""
        UISLoginDialog(
            onDismissRequest = { showUISLoginDialog = false },
            onLogin = { id, password ->
                fudanStateHolder.loginFDUUIS(id, password)
            },
            enabled = fduState !is LoginStatus.Loading,
            errorMessage = errorMessage,
        )
    }
}