package com.fduhole.danxinative

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fduhole.danxinative.ui.DanXiNavGraph
import com.fduhole.danxinative.ui.GlobalViewModel
import com.fduhole.danxinative.ui.theme.DanXiNativeTheme
import dagger.hilt.android.AndroidEntryPoint

// @see https://github.com/android/user-interface-samples/blob/main/PerAppLanguages/compose_app/app/src/main/java/com/example/perapplanguages/MainActivity.kt
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val globalViewModel: GlobalViewModel = viewModel()
    val uiState by globalViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = uiState.isDarkTheme ?: isSystemInDarkTheme()
    DanXiNativeTheme(isDarkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DanXiNavGraph(
                globalViewModel = globalViewModel,
            )
        }
    }
}
