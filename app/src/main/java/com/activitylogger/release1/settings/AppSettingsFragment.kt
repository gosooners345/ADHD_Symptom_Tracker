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
//Symptom Custom Layout Options
lateinit var  customLayoutOptionPrefs : ListPreference
lateinit var symptomVerticalOptions : ListPreference
    lateinit var gridSizePreference : SeekBarPreference
    //Record Layout Settings
    lateinit var recordLayoutOptions : ListPreference
    lateinit var recordVerticalOptions : ListPreference
    lateinit var recordGridSizePreference: SeekBarPreference
    //Password Change and Enable
    lateinit var passwordChangeTextEditor: EditTextPreference
    lateinit var enablePasswordSwitch: SwitchPreferenceCompat
    //Reset everything
    lateinit var resetPreference : Preference
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_page, rootKey)

//Reset Everything
resetPreference=findPreference("firstUse")!!
            resetPreference.setOnPreferenceClickListener {
             it.sharedPreferences!!.edit().putBoolean(it.key,false).putString("password","")?.putString("layoutOption","linear")?.putInt("gridSize",3)?.putString("linear_horizontal_records","vertical")?.putString("linear_horizontal_symptoms","vertical")?. putString("layoutOption_record","linear")?.putInt("gridSize_record",3)
                    ?.putBoolean("enablePassword",false)!!.apply()
                MainActivity.appPreferences.edit().putBoolean(it.key,false).putString("password","")?.putString("layoutOption","linear")?.putInt("gridSize",3)
                    ?.putString("layoutOption_record","linear")?.putInt("gridSize_record",3)?.putString("linear_horizontal_records","vertical")?.putString("linear_horizontal_symptoms","vertical")
                ?.putBoolean("enablePassword",false)!!.commit()
            }
            //Symptom List Custom Prefs
         customLayoutOptionPrefs= findPreference("layoutOption")!!
customLayoutOptionPrefs.setOnPreferenceChangeListener { preference, newValue ->
    preference.sharedPreferences.edit().putString(preference.key,newValue as String).apply()
    MainActivity.appPreferences!!.edit().putString(preference.key,newValue).commit()
}
            symptomVerticalOptions = findPreference("linear_horizontal_symptoms")!!
            symptomVerticalOptions.setOnPreferenceChangeListener { preference, newValue ->
                preference.sharedPreferences.edit().putString(preference.key,newValue as String).apply()
                MainActivity.appPreferences!!.edit().putString(preference.key,newValue).commit()

            }

gridSizePreference = findPreference<SeekBarPreference>("gridSize")!!
            gridSizePreference.setOnPreferenceChangeListener { preference, newValue ->
                preference.sharedPreferences.edit().putInt(preference.key,newValue as Int).apply()
                MainActivity.appPreferences!!.edit().putInt(preference.key, newValue ).commit()
            }
//Record Layout Settings
            recordLayoutOptions=findPreference<ListPreference>("layoutOption_record")!!
            recordLayoutOptions.setOnPreferenceChangeListener { preference, newValue ->
                preference.sharedPreferences.edit().putString(preference.key,newValue as String).apply()
                MainActivity.appPreferences!!.edit().putString(preference.key,newValue).commit()
            }
            recordGridSizePreference= findPreference<SeekBarPreference>("gridSize_record")!!
            recordGridSizePreference.setOnPreferenceChangeListener { preference, newValue ->
                preference.sharedPreferences.edit().putInt(preference.key,newValue as Int).apply()
                MainActivity.appPreferences!!.edit().putInt(preference.key, newValue ).commit()
            }
            recordVerticalOptions = findPreference("linear_horizontal_records")!!
            recordVerticalOptions.setOnPreferenceChangeListener { preference, newValue ->
                preference.sharedPreferences.edit().putString(preference.key,newValue as String).apply()
                MainActivity.appPreferences!!.edit().putString(preference.key,newValue).commit()

            }

//Password Settings
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
    fun restoreseekbars(int1 : Int, int2:Int)
    {
        recordGridSizePreference.value =int1
        gridSizePreference.value = int2

    }
    fun restoreGridOptions(string1:String,string2:String)
    {
        recordVerticalOptions.value = string1
        symptomVerticalOptions.value = string2


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
        restoreGridOptions(MainActivity.appPreferences.getString("linear_horizontal_records","vertical")!!,MainActivity.appPreferences.getString("linear_horizontal_symptoms","vertical")!!)
        restoreseekbars(MainActivity.appPreferences.getInt("gridSize_record",3),MainActivity.appPreferences.getInt("gridSize",3))
    }

    override fun onPause() {
        super.onPause()
        MainActivity.appPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}

