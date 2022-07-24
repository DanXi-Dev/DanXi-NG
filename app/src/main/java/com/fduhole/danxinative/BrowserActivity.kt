package com.fduhole.danxinative

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.fduhole.danxinative.databinding.ActivityBrowserBinding
import kotlinx.coroutines.delay

class BrowserActivity : AppCompatActivity() {
    companion object {
        const val KEY_URL = "url"
        const val KEY_JAVASCRIPT = "js"
        const val KEY_EXECUTE_IF_START_WITH = "executeIfStartWith"
    }

    private val callbacks: MutableList<() -> Unit> = mutableListOf()
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

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    for (callback in callbacks) {
                        callback.invoke()
                    }
                    callbacks.clear()
                }
            }

        binding.actBrowser.settings.javaScriptEnabled = true
        binding.actBrowser.settings.domStorageEnabled = true
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
                val permissionGrant = ContextCompat.checkSelfPermission(this@BrowserActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                println(permissionGrant)
                if (permissionGrant == PackageManager.PERMISSION_GRANTED) {
                    callback?.invoke(origin, true, true)
                } else {
                    callbacks.add { callback?.invoke(origin, true, true) }
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }
        binding.actBrowser.loadUrl(url)
    }
}