package com.activitylogger.release1.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class ADHDSettingsFragment : Fragment() {
    lateinit var prefs : SharedPreferences
    lateinit var passwordy: String
lateinit var passwordTextBox : TextInputLayout
lateinit var enterButton : Button
    var userPassword = ""
    lateinit var resetButton : Button
    override fun onCreateView(inflater: LayoutInflater,container:ViewGroup?,savedInstanceState: Bundle?): View? {
val view = inflater.inflate(R.layout.settings_screen,container,false)

        prefs = MainActivity.passWordPreferences
        passwordy = MainActivity.passWordPreferences.getString("password","")!!
        enterButton = view.findViewById(R.id.enterButton)
        enterButton.setOnClickListener(saveButtonListener)
        resetButton = view.findViewById(R.id.resetButton)
        resetButton.setOnClickListener(resetButtonListener)
        passwordTextBox=view.findViewById(R.id.passwordTextBox)
        passwordTextBox.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                userPassword = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {


            }
        })

return  view
    }

var resetButtonListener = View.OnClickListener {
    val passwordEditor : SharedPreferences.Editor = MainActivity.passWordPreferences.edit()
    passwordEditor.putString("password","")
    passwordEditor.putBoolean("firstUse",false)
    passwordEditor.apply()
}

var saveButtonListener = View.OnClickListener {
    userPassword = passwordTextBox.editText!!.text.toString()
    changePassword(userPassword)

}


    fun changePassword(newpassword : String)
    {
        val passWordEditor :SharedPreferences.Editor = MainActivity.passWordPreferences.edit()
        passWordEditor.putString("password",newpassword)
        passWordEditor.apply()
Log.i("newPassword","Password $newpassword saved")
    }

}