package com.fduhole.danxi.ui.component.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fduhole.danxi.R

@Composable
fun UISLoginDialog(
    onDismissRequest: () -> Unit,
    onLogin: (String, String) -> Unit,
    enabled: Boolean,
    errorMessage: String = "",
) {
    var id by rememberSaveable { mutableStateOf("") }
    var isIdError by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordError by rememberSaveable { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { if (!isIdError && !isPasswordError) onLogin(id, password) },
                enabled = enabled && id.isNotEmpty() && password.isNotEmpty(),
            ) {
                Text(stringResource(id = R.string.login))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        title = { Text(stringResource(id = R.string.login_uis)) },
        text = {
            Column {
                OutlinedTextField(
                    value = id,
                    onValueChange = {
                        isIdError = it.isEmpty()
                        id = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.id)) },
                    enabled = enabled,
                    isError = isIdError,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        isPasswordError = it.isEmpty()
                        password = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = enabled,
                    isError = isPasswordError,
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}