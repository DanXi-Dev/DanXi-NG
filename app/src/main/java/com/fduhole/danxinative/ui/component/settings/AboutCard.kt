package com.fduhole.danxinative.ui.component.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fduhole.danxinative.BuildConfig
import com.fduhole.danxinative.R

data class Developer(
    @DrawableRes val avatar: Int,
    val name: String,
    val role: String,
    val url: String,
)

val developers = listOf(
    Developer(R.drawable.w568w, "w568w", "Android 主要开发者", "https://github.com/w568w"),
    Developer(R.drawable.skyleaworld, "skyleaworlder", "Android 主要开发者", "https://github.com/skyleaworlder"),
    Developer(R.drawable.fsy2001, "fsy2001", "iOS 主要开发者", "https://github.com/fsy2001"),
    Developer(R.drawable.kavinzhao, "singularity", "iOS 主要开发者", "https://github.com/singularity-s0"),
    Developer(R.drawable.ivanfei, "Ivan Fei", "App 图标 & UI 设计", "https://github.com/ivanfei-1"),
)

data class License(
    val name: String,
    val author: String,
    val license: String,
    val url: String,
) {
    companion object {
        const val MIT = "MIT License"
        const val APACHE_2 = "Apache Software License 2.0"
        const val GPL_V3 = "GNU general public license Version 3"
    }
}

val licenses = listOf(
    License("kotlinx.serialization", "Kotlin", License.APACHE_2, "https://github.com/Kotlin/kotlinx.serialization"),
    License("kotlinx-datetime", "Kotlin", License.APACHE_2, "https://github.com/Kotlin/kotlinx-datetime"),
    License("jsoup", "jhy", License.MIT, "https://github.com/jhy/jsoup"),
    License("Android Jetpack", "google", License.MIT, "https://maven.google.com/"),
    License("junit4", "junit-team", "Eclipse Public License 1.0", "https://github.com/junit-team/junit4"),
)

fun openUrl(context: Context, url: String) {
    val urlIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    context.startActivity(urlIntent)
}

@Composable
fun AboutCard(
    expanded: Boolean,
    onChangeExpanded: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val text = stringResource(id = R.string.about)
        ListItem(
            headlineContent = {
                Text(text)
            },
            leadingContent = {
                Icon(Icons.Filled.Info, contentDescription = text)
            },
            trailingContent = {
                val icon = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
                Icon(icon, contentDescription = text)
            },
            modifier = Modifier.clickable {
                onChangeExpanded()
            },
        )

        if (expanded) {
            AboutContent()
        }
    }
}

@Composable
private fun AboutContent() {
    Column {
        ListItem(
            headlineContent = { Text(stringResource(R.string.app_description_title)) },
            supportingContent = { Text(stringResource(R.string.app_description)) }
        )
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.version)) },
            supportingContent = { Text("${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})") }
        )
        HorizontalDivider()
        ListItem(headlineContent = { Text(text = stringResource(id = R.string.developers)) })
        Column {
            developers.forEach {
                val context = LocalContext.current
                ListItem(
                    leadingContent = {
                        Image(
                            painterResource(it.avatar),
                            contentDescription = it.name,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                        )
                    },
                    headlineContent = { Text(it.name) },
                    supportingContent = { Text(it.role) },
                    modifier = Modifier.clickable {
                        openUrl(context, it.url)
                    }
                )
            }
        }
        HorizontalDivider()
        ListItem(headlineContent = { Text(text = stringResource(id = R.string.license)) })
        Column {
            licenses.forEach {
                val context = LocalContext.current
                ListItem(
                    headlineContent = { Text(it.name) },
                    supportingContent = { Text("${it.author} - ${it.license}") },
                    modifier = Modifier.clickable {
                        openUrl(context, it.url)
                    }
                )
            }
        }
    }
}