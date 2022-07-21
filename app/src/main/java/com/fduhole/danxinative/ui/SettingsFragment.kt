package com.fduhole.danxinative.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.fduhole.danxinative.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}