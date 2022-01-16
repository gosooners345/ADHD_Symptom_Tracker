package com.activitylogger.release1.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.preference.*
import androidx.security.crypto.EncryptedSharedPreferences
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R

class AppSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
lateinit var enablePasswordSwitch: SwitchPreferenceCompat
lateinit var  customLayoutOptionPrefs : ListPreference
    lateinit var passwordChangeTextEditor: EditTextPreference
    lateinit var resetPreference : Preference
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
// Look into data store


            setPreferencesFromResource(R.xml.settings_page, rootKey)

resetPreference=findPreference("firstUse")!!


            resetPreference.setOnPreferenceClickListener {
             it.sharedPreferences!!.edit().putBoolean(it.key,false).putString("password","")?.putString("layoutOption","linear")
                    ?.putBoolean("enablePassword",false)!!.apply()
                MainActivity.appPreferences!!.edit().putBoolean(it.key,false).putString("password","")?.putString("layoutOption","linear")
                ?.putBoolean("enablePassword",false)!!.commit()
            }
         customLayoutOptionPrefs= findPreference("layoutOption")!!
customLayoutOptionPrefs.setOnPreferenceChangeListener { preference, newValue ->
    preference.sharedPreferences.edit().putString(preference.key,newValue as String).apply()
    MainActivity.appPreferences!!.edit().putString(preference.key,newValue as String).commit()
}



    enablePasswordSwitch= findPreference("enablePassword")!!
            enablePasswordSwitch.isChecked = MainActivity.appPreferences.getBoolean("enablePassword",false)
enablePasswordSwitch.setOnPreferenceChangeListener { preference, newValue ->
when(newValue as Boolean)
{
    true ->{ passwordChangeTextEditor.isEnabled=true
    }
    false ->{
        passwordChangeTextEditor.isEnabled=false

    }

}
    changePasswordTextBoxVisibility(newValue)
        MainActivity.appPreferences.edit().putBoolean(preference.key, newValue as Boolean).commit()
}

        enablePasswordSwitch.summaryOff = String.format("Password Protection Disabled")
        enablePasswordSwitch.summaryOn = String.format("Password Protection Enabled")

       passwordChangeTextEditor=findPreference("password")!!

            passwordChangeTextEditor.setOnBindEditTextListener { editText ->
                editText.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordChangeTextEditor.setOnPreferenceChangeListener { preference, newValue ->
                Log.i(preference.key,"Password is $newValue")
                MainActivity.appPreferences.edit().putString(preference.key, newValue as String).commit()
            }
            MainActivity.appPreferences.registerOnSharedPreferenceChangeListener(this)

        }

fun changePasswordTextBoxVisibility(visible: Boolean){
    passwordChangeTextEditor.isVisible = visible

}
    fun restorePasswordonResume(password : String?)
    {
        passwordChangeTextEditor.text=password!!
    }


    @RequiresApi(Build.VERSION_CODES.M)
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
//requireContext().getSharedPreferences(MainActivity.PREFNAME,MODE_PRIVATE).edit().clear().commit()

    }

    override fun onResume() {
        super.onResume()
        MainActivity.appPreferences.registerOnSharedPreferenceChangeListener(this)
        changePasswordTextBoxVisibility(MainActivity.appPreferences.getBoolean("enablePassword",false))
        restorePasswordonResume(MainActivity.appPreferences.getString("password",""))
    }

    override fun onPause() {
        super.onPause()
        MainActivity.appPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}

