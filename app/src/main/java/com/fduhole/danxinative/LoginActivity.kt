package com.fduhole.danxinative

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.fduhole.danxinative.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(LayoutInflater.from(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.actLoginToolbar)
    }

    /*
    We do not permit user to go back to the last page.
    We will exit the app if user refuses to log in.
    */
    override fun onBackPressed() = finishAffinity()
}