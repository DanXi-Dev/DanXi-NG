package com.fduhole.danxinative

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fduhole.danxinative.databinding.ActivitySingleFragmentBinding

class SingleFragmentActivity : AppCompatActivity() {
    private val binding: ActivitySingleFragmentBinding by lazy { ActivitySingleFragmentBinding.inflate(LayoutInflater.from(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.actSingleFragmentToolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.act_single_fragment_fragment, intent.getSerializableExtra(KEY_FRAGMENT_CLASS)!! as Class<out Fragment>, intent.getBundleExtra(KEY_FRAGMENT_ARGUMENTS))
            .commitNow()
    }

    companion object {
        const val KEY_FRAGMENT_CLASS = "class"
        const val KEY_FRAGMENT_ARGUMENTS = "arguments"
        fun <T : Fragment> showFragment(context: Context, clazz: Class<T>) {
            showFragment(context, clazz, null)
        }

        fun <T : Fragment> showFragment(context: Context, clazz: Class<T>, arguments: Bundle?) {
            val intent = Intent(context, SingleFragmentActivity::class.java)
                .putExtra(KEY_FRAGMENT_CLASS, clazz)

            arguments?.let { intent.putExtra(KEY_FRAGMENT_ARGUMENTS, it) }

            if (context is Application) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}