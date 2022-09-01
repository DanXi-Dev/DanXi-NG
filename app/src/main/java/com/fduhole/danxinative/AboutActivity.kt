package com.fduhole.danxinative

import android.widget.ImageView
import android.widget.TextView
import com.drakeet.about.*

class AboutActivity : AbsAboutActivity() {
    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.mipmap.ic_launcher)
        slogan.text = getString(R.string.app_name)
        version.text = "${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.addAll(
            listOf(
                Category(getString(R.string.app_description_title)),
                Card(getString(R.string.app_description)),
                Category(getString(R.string.developers)),
                Contributor(R.drawable.w568w, "w568w", "Android 主要开发者", "https://github.com/w568w"),
                Contributor(R.drawable.skyleaworld, "skyleaworlder", "Android 主要开发者", "https://github.com/skyleaworlder"),
                Contributor(R.drawable.fsy2001, "fsy2001", "iOS 主要开发者", "https://github.com/fsy2001"),
                Contributor(R.drawable.kavinzhao, "singularity", "iOS 主要开发者", "https://github.com/singularity-s0"),
                Contributor(R.drawable.ivanfei, "Ivan Fei", "App 图标 & UI 设计", "https://github.com/ivanfei-1"),
                Category(getString(R.string.license)),
                License("Koin", "InsertKoinIO", License.APACHE_2, "https://github.com/InsertKoinIO/koin"),
                License("okhttp", "Square, Inc.", License.APACHE_2, "https://github.com/square/okhttp"),
                License("retrofit", "Square, Inc.", License.APACHE_2, "https://github.com/square/retrofit"),
                License("kotlinx.serialization", "Kotlin", License.APACHE_2, "https://github.com/Kotlin/kotlinx.serialization"),
                License("retrofit2-kotlinx-serialization-converter", "Jake Wharton", License.APACHE_2, "https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter"),
                License("kotlinx-datetime", "Kotlin", License.APACHE_2, "https://github.com/Kotlin/kotlinx-datetime"),
                License("jsoup", "jhy", License.MIT, "https://github.com/jhy/jsoup"),
                License("Android Jetpack", "google", License.MIT, "https://maven.google.com/"),
                License("about-page", "drakeet", License.APACHE_2, "https://github.com/PureWriter/about-page"),
                License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"),
                License("junit4", "junit-team", "Eclipse Public License 1.0", "https://github.com/junit-team/junit4"),
                License("shared-preferences-mock", "Ivan Shafran", License.MIT, "https://github.com/IvanShafran/shared-preferences-mock"),
            )
        )
    }
}