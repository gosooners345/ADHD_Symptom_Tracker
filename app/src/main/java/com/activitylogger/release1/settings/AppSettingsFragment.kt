package com.activitylogger.release1.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.preference.*
import com.activitylogger.release1.R

class AppSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
lateinit var enablePasswordSwitch: SwitchPreferenceCompat
lateinit var  customLayoutOptionPrefs : ListPreference
    lateinit var passwordChangeTextEditor: EditTextPreference
    lateinit var resetPreference : Preference
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_page, rootKey)

resetPreference=findPreference("firstUse")!!

            resetPreference.setOnPreferenceClickListener {
                it.sharedPreferences!!.edit().putBoolean(it.key,false).putString("password","")?.putString("layoutOption","linear")
                    ?.putBoolean("enablePassword",false)!!.commit()
            }
         customLayoutOptionPrefs= findPreference("layoutOption")!!
customLayoutOptionPrefs.setOnPreferenceChangeListener { preference, newValue ->
    preference.sharedPreferences.edit().putString(preference.key,newValue as String).commit()
}



    enablePasswordSwitch= findPreference("enablePassword")!!
enablePasswordSwitch.setOnPreferenceChangeListener { preference, newValue ->
when(newValue as Boolean)
{
    true ->{ passwordChangeTextEditor.isEnabled=true
    }
    false ->{
        passwordChangeTextEditor.isEnabled=false
    }
}
        preference.sharedPreferences.edit().putBoolean(preference.key, newValue as Boolean).commit()
}
        enablePasswordSwitch.summaryOff = String.format("Password Protection Disabled")
        enablePasswordSwitch.summaryOn = String.format("Password Protection Enabled")

       passwordChangeTextEditor=findPreference("password")!!

            passwordChangeTextEditor.setOnBindEditTextListener { editText ->
                editText.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordChangeTextEditor.setOnPreferenceChangeListener { preference, newValue ->
                Log.i(preference.key,"Password is $newValue")
                preference.sharedPreferences.edit()
                            .putString(preference.key, newValue as String).commit()
            }
        }




    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key)
        {
"linearOption" -> Log.i(key,"value is " + sharedPreferences?.getString(key,"linear"))
            "password" ->Log.i(key, "Password is "+sharedPreferences?.getString(key,""))
            "enablePassword"->{Log.i(key,"Password protection is" +
                    " ${if(sharedPreferences?.getBoolean(key,false)==false) "Disabled" else "Enabled"}" )

            }
        "firstUse" ->{
        Log.i(key,"Everything has been reset except records")
        Toast.makeText(requireContext(),"Everything has been reset except your logs.",Toast.LENGTH_LONG).show()

        }
        }


    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}

