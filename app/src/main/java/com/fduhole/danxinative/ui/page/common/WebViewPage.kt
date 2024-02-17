package com.fduhole.danxinative.ui.page.common

import android.Manifest
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fduhole.danxinative.R
import com.fduhole.danxinative.model.fdu.UISInfo
import com.fduhole.danxinative.repository.settings.SettingsRepository
import com.fduhole.danxinative.ui.component.RequestSinglePermissionDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WebViewModel @Inject constructor(
    val settingsRepository: SettingsRepository
) : ViewModel() {
    var url: String? = null
    var javascript: String? = null
    var feature: String? = null

    var uisInfo: StateFlow<UISInfo?> =
        settingsRepository.uisInfo.flow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

@Composable
fun WebViewPage(
    url: String,
    javascript: String?,
    feature: String?,
) {
    val scope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionCallback: ((Boolean) -> Unit)? by remember { mutableStateOf(null) }

    if (showPermissionDialog) {
        RequestSinglePermissionDialog(
            permission = Manifest.permission.ACCESS_COARSE_LOCATION,
            rationale = stringResource(R.string.location_permission_rationale),
            callback = permissionCallback!!,
            onDismissRequest = { showPermissionDialog = false }
        )
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (url != null && feature != null && url.startsWith(feature)) {
                            scope.launch {
                                delay(1.seconds)
                                view?.evaluateJavascript(javascript.orEmpty()) {}
                            }
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                        super.onGeolocationPermissionsShowPrompt(origin, callback)
                        permissionCallback = {
                            callback?.invoke(origin, it, it)
                            showPermissionDialog = false
                        }
                    }
                }
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}