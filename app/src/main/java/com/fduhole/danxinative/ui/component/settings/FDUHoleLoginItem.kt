package com.fduhole.danxinative.ui.component.settings

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
import androidx.compose.ui.Modifier
import com.fduhole.danxinative.ui.FDUHoleViewState
import com.fduhole.danxinative.util.LoginStatus

@Composable
fun FDUHoleLoginItem(fduHoleState: LoginStatus<out FDUHoleViewState>) {
    ListItem(
        headlineContent = {
            Text("FDUHole 账号")
        },
        supportingContent = {
            when (fduHoleState) {
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
                    val id = fduHoleState.data.id
                    Text("登录成功, id: $id")
                }
            }
        },
        leadingContent = {
            Icon(Icons.Filled.AccountCircle, contentDescription = "FDUHole 账号")
        },
        trailingContent = {
            when (fduHoleState) {
                is LoginStatus.Error -> {
                    Icon(Icons.Filled.Error, contentDescription = "FDUHole 账号 登录失败")
                }

                LoginStatus.Loading -> {
                    CircularProgressIndicator()
                }

                LoginStatus.NotLogin -> {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "FDUHole 账号 登录")
                }

                is LoginStatus.Success -> {
                    Icon(Icons.Filled.Check, contentDescription = "FDUHole 账号 登录成功")
                }
            }
        },
        modifier = Modifier.clickable {

        },
    )
}