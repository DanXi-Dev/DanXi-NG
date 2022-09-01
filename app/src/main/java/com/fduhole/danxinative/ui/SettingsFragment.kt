package com.fduhole.danxinative.ui

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fduhole.danxinative.AboutActivity
import com.fduhole.danxinative.BuildConfig
import com.fduhole.danxinative.R
import com.fduhole.danxinative.state.GlobalState
import com.fduhole.danxinative.util.SharedPrefDataStore
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {
    private val globalState: GlobalState by inject()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = SharedPrefDataStore(globalState.preferences)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("campus_account")?.setSummaryProvider { globalState.person?.toString() }
        findPreference<Preference>("about")?.apply {
            setSummaryProvider { "${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})" }
            setOnPreferenceClickListener {
                startActivity(Intent(activity, AboutActivity::class.java))
                true
            }
        }
    }
}