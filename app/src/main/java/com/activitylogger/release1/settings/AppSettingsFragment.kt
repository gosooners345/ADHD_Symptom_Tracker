package com.activitylogger.release1.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R

class AppSettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var passwordValue : String
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_page,rootKey)
        val passwordPreference : EditTextPreference? = findPreference("password")
        passwordPreference?.summaryProvider= Preference.SummaryProvider<EditTextPreference>{
                preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text))
                "Set Password"
            else
                "Change your password"
        }
        passwordPreference?.setOnBindEditTextListener {  editText ->
            editText.inputType= InputType.TYPE_TEXT_VARIATION_PASSWORD
            editText.setText(MainActivity.passWordPreferences.getString("password",""))
            passwordValue=editText.text.toString()
        }
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        if(preferenceManager.sharedPreferences.equals(MainActivity.passWordPreferences)==true)
            Log.i("SAME","They're The same")
        else
            Log.i("UHOH","WE have trouble here")

    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        Log.i("Preference Change","$newValue")

        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key=="password") {
            val passwordSaver = sharedPreferences?.edit()
            passwordSaver?.putString(key!!, passwordValue)
            passwordSaver?.commit()
            Toast.makeText(
                requireContext(),
                "New Password is ${sharedPreferences?.getString(key, "")}",
                Toast.LENGTH_LONG
            ).show()
            Log.i("Saved", "Password saved is ${sharedPreferences?.getString(key, "")}")
        }
    }

}