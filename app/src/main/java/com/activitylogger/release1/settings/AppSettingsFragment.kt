package com.activitylogger.release1.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.preference.*
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R

@Suppress("SpellCheckingInspection", "SameParameterValue")
class AppSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    //Email Developer Item
    private lateinit var emailPreference : Preference
    //Version Info Preference
    private lateinit var versionInfoPreference: Preference

    //Personalized Greeting settings
    private lateinit var greetingPreference : EditTextPreference

    //Symptom Custom Layout Options
    private lateinit var symptomLayoutOptionPrefs: ListPreference
    private lateinit var symptomVerticalOptions: ListPreference
    private lateinit var gridSizePreference: SeekBarPreference

    //Record Layout Settings
    private lateinit var recordLayoutOptions: ListPreference
    private lateinit var recordVerticalOptions: ListPreference

    //Password Change and Enable
    private lateinit var passwordChangeTextEditor: EditTextPreference
    private lateinit var enablePasswordSwitch: SwitchPreferenceCompat

    //Reset everything
    private lateinit var resetPreference: Preference
    @SuppressLint("RestrictedApi")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_page, rootKey)

        versionInfoPreference = findPreference("versionInfo")!!

        greetingPreference = findPreference(resources.getString(R.string.greeting))!!

        emailPreference = findPreference("feedback")!!
        emailPreference.setOnPreferenceClickListener {
    clickAway()
        }

        versionInfoPreference.title = "Version #: ${MainActivity.versionName}"

        recordVerticalOptions = findPreference("linear_horizontal_records")!!

        symptomLayoutOptionPrefs = findPreference("layoutOption")!!
        symptomLayoutOptionPrefs.setOnPreferenceChangeListener { preference, newValue ->
            preference.sharedPreferences!!.edit().putString(preference.key, newValue as String)
                .apply()
            MainActivity.appPreferences.edit().putString(preference.key, newValue).commit()
        }
        symptomVerticalOptions = findPreference("linear_horizontal_symptoms")!!
        symptomVerticalOptions.setOnPreferenceChangeListener { preference, newValue ->
            preference.sharedPreferences!!.edit().putString(preference.key, newValue as String)
                .apply()
            MainActivity.appPreferences.edit().putString(preference.key, newValue).commit()

        }

        gridSizePreference = findPreference("gridSize")!!
        gridSizePreference.setOnPreferenceChangeListener { preference, newValue ->
            preference.sharedPreferences!!.edit().putInt(preference.key, newValue as Int).apply()
            MainActivity.appPreferences.edit().putInt(preference.key, newValue).commit()
        }
//Record Layout Settings
        recordLayoutOptions = findPreference("layoutOption_record")!!
        recordLayoutOptions.setOnPreferenceChangeListener { preference, newValue ->

            preference.sharedPreferences!!.edit().putString(preference.key, newValue as String)
                .apply()
            if(newValue=="linear")
            {
                recordVerticalOptions.isVisible=true
                recordVerticalOptions.isEnabled=true
            }
            else{
                recordVerticalOptions.isVisible=false
                recordVerticalOptions.isEnabled=false
            }
            MainActivity.appPreferences.edit().putString(preference.key, newValue).commit()
        }
       if(recordLayoutOptions.value=="linear")
       {
           recordVerticalOptions.isVisible=true
           recordVerticalOptions.isEnabled=true
           recordVerticalOptions.setOnPreferenceChangeListener { preference, newValue ->
            preference.sharedPreferences!!.edit().putString(preference.key, newValue as String)
                .apply()
            MainActivity.appPreferences.edit().putString(preference.key, newValue).commit()

        }
       }
        else
       {
           recordVerticalOptions.isVisible=false
       recordVerticalOptions.isEnabled=false
       }
//Reset Everything
        resetPreference = findPreference("firstUse")!!
        resetPreference.setOnPreferenceClickListener {
            it.sharedPreferences!!.edit().putBoolean(it.key, false).putString("password", "")
                ?.putString("layoutOption", "linear")?.putInt("gridSize", 2)
                ?.putString("linear_horizontal_records", "vertical")
                ?.putString("linear_horizontal_symptoms", "vertical")?.putString("greeting","")
                ?.putString("layoutOption_record", "linear")?.putBoolean("enablePassword", false)!!.apply()
            MainActivity.appPreferences.edit().putBoolean(it.key, false)
                .putString("password", "")
                ?.putString("layoutOption", "linear")?.putInt("gridSize", 2)
                ?.putString("layoutOption_record", "linear")
                ?.putString("linear_horizontal_records", "vertical")
                ?.putString("linear_horizontal_symptoms", "vertical")?.putString("greeting","")
                ?.putBoolean("enablePassword", false)!!.commit()
        }


//Password Settings
        enablePasswordSwitch = findPreference("enablePassword")!!
        enablePasswordSwitch.isChecked =
            MainActivity.appPreferences.getBoolean("enablePassword", false)
        enablePasswordSwitch.setOnPreferenceChangeListener { preference, newValue ->
            when (newValue as Boolean) {
                true -> {
                    passwordChangeTextEditor.isEnabled = true
                }
                false -> {
                    passwordChangeTextEditor.isEnabled = false

                }

            }
            changePasswordTextBoxVisibility(newValue)
            MainActivity.appPreferences.edit().putBoolean(preference.key, newValue)
                .commit()
        }

        enablePasswordSwitch.summaryOff = String.format("Password Protection Disabled")
        enablePasswordSwitch.summaryOn = String.format("Password Protection Enabled")

        passwordChangeTextEditor = findPreference("password")!!

        passwordChangeTextEditor.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordChangeTextEditor.setOnPreferenceChangeListener { preference, newValue ->
         if(MainActivity.buildType=="debug")
            Log.i(preference.key, "Password is $newValue")
            if(newValue =="")
            {
                enablePasswordSwitch.performClick()
            }
            MainActivity.appPreferences.edit().putString(preference.key, newValue as String)
                .commit()
        }
        greetingPreference.setOnPreferenceChangeListener { preference, newValue ->
            if (MainActivity.buildType=="debug")
                Log.i(preference.key,"Greeting is: Hello $newValue, What " +
                                     "would you like to record today?")
            MainActivity.appPreferences.edit().putString(preference.key,newValue as String).commit()
        }


        MainActivity.appPreferences.registerOnSharedPreferenceChangeListener(this)

    }





    private fun changePasswordTextBoxVisibility(visible: Boolean) {
        passwordChangeTextEditor.isVisible = visible

    }
    //Restore Methods upon loading
    private fun restoreseekbars(int1: Int) {
        gridSizePreference.value = int1
    }

    private fun restoreGridOptions(string1: String, string2: String) {
        recordVerticalOptions.value = string1
        symptomVerticalOptions.value = string2
    }

    private fun restorePasswordonResume(password: String?) {
        passwordChangeTextEditor.text = password!!
    }

    override fun onResume() {
        super.onResume()
        MainActivity.appPreferences.registerOnSharedPreferenceChangeListener(this)
        restorePrefs()
    }

    private fun restorePrefs() {
        restoreGridOptions(
            MainActivity.appPreferences.getString(
                "linear_horizontal_records",
                "vertical"
            )!!, MainActivity.appPreferences.getString("linear_horizontal_symptoms", "vertical")!!
        )
        restoreseekbars(
            MainActivity.appPreferences.getInt("gridSize", 2)
        )
        changePasswordTextBoxVisibility(
            MainActivity.appPreferences.getBoolean(
                "enablePassword",
                false
            )
        )
        restorePasswordonResume(MainActivity.appPreferences.getString("password", ""))

    }

    override fun onPause() {
        super.onPause()
        MainActivity.appPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {

            "password" -> Log.i(key, "Password is " + sharedPreferences?.getString(key, ""))
            "enablePassword" -> {
                Log.i(
                    key, "Password protection is" +
                            " ${
                                if (sharedPreferences?.getBoolean(
                                        key,
                                        false
                                    ) == false
                                ) "Disabled" else "Enabled"
                            }"
                )
  
            }
          "firstUse"         ->
          {
            Log.i(key, "Everything has been reset except records")
            Toast.makeText(
              requireContext(),
              "Everything has been reset except your records.",
              Toast.LENGTH_LONG
            ).show()
    
          }
          "linearOption"     -> Log.i(
            key, "value is " + sharedPreferences?.getString(
              key, "linear"
            )
          )
          "gridSize"         -> Log.i(
            key, "value is" + sharedPreferences?.getInt(
              key, 3
            )
          )
        }
  
  
    }
  
  private fun composeEmail(addresses: Array<String>, subject: String)
  {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("mailto:")
      putExtra(Intent.EXTRA_EMAIL, addresses)
      putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    startActivity(intent)
  }
  
  private fun clickAway(): Boolean
  {
    composeEmail(
      resources.getStringArray(R.array.addresses),
      "Feedback about ADHD Journal Version #${MainActivity.versionName}"
    )
    return true
  }
}

