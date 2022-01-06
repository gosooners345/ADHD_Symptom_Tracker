package com.activitylogger.release1.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.activitylogger.release1.R

class ADHDSettingsFragment : PreferenceFragmentCompat() {
    lateinit var prefs : SharedPreferences
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
setPreferencesFromResource(R.xml.settings_page,rootKey)

    }
    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,key: String)
    {

    }
}