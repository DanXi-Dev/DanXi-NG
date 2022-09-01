package com.fduhole.danxinative

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.fduhole.danxinative.databinding.ActivityBrowserBinding
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay

class BrowserActivity : AppCompatActivity() {
    companion object {
        const val KEY_URL = "url"
        const val KEY_JAVASCRIPT = "js"
        const val KEY_EXECUTE_IF_START_WITH = "executeIfStartWith"
    }

    private val binding: ActivityBrowserBinding by lazy { ActivityBrowserBinding.inflate(LayoutInflater.from(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        val url = intent.getStringExtra(KEY_URL)
        val javascript = intent.getStringExtra(KEY_JAVASCRIPT)
        val feature = intent.getStringExtra(KEY_EXECUTE_IF_START_WITH)
        if (url.isNullOrBlank()) {
            finish()
            return
        }

        binding.actBrowser.settings.javaScriptEnabled = true
        binding.actBrowser.settings.domStorageEnabled = true
        binding.actBrowser.settings.builtInZoomControls = true
        binding.actBrowser.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null && feature != null && url.startsWith(feature)) {
                    lifecycleScope.launchWhenStarted {
                        delay(1000)
                        binding.actBrowser.evaluateJavascript(javascript.orEmpty()) {}
                    }
                }
            }
        }
        binding.actBrowser.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                PermissionX.init(this@BrowserActivity)
                    .permissions(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .explainReasonBeforeRequest()
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(deniedList, "我们向您请求大致位置权限，是因为当前页面正在调用 Geolocation API 来获知您的大致位置。", "允许", "拒绝")
                    }
                    .request { allGranted, grantedList, deniedList ->
                        callback?.invoke(origin, allGranted, allGranted)
                    }
            }
        }
        binding.actBrowser.loadUrl(url)
    }
}