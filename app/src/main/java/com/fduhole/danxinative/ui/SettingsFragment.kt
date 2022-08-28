package com.fduhole.danxinative.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fduhole.danxinative.R
import com.fduhole.danxinative.state.GlobalState
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {
    private val globalState: GlobalState by inject()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>("campus_account")?.setSummaryProvider { globalState.person?.toString() }
    }
}