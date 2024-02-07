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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.ui.component.RequestSinglePermissionDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WebViewModel @Inject constructor(
    val globalState: GlobalState
): ViewModel() {
    var url: String? = null
    var javascript: String? = null
    var feature: String? = null
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
            rationale = "我们向您请求大致位置权限，是因为当前页面正在调用 Geolocation API 来获知您的大致位置。",
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