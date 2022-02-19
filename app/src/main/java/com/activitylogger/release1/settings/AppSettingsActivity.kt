package com.activitylogger.release1.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.activitylogger.release1.R

class AppSettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment.toString())
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, fragment)
            .addToBackStack(null)
            .commit()
        return true

    }

    override fun onBackPressed() {
        //
setResult(RESULT_OK)
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blank_settings)
val fragmentManager = supportFragmentManager
        val settingsFragment = AppSettingsFragment()
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.settings_fragment,settingsFragment)
        fragmentTransaction.commit()

    }
}