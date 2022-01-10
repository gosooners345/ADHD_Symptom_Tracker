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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout

class ADHDSettingsFragment : Fragment() {
    lateinit var prefs : SharedPreferences
    lateinit var passwordy: String
lateinit var passwordTextBox : TextInputLayout
lateinit var enterButton : Button
lateinit var enablePassword : SwitchMaterial
lateinit var layoutOption : String
var passwordEnabled = true
    lateinit var layoutChipOptions :ChipGroup
    lateinit var linearChip : Chip
    lateinit var gridChip : Chip
    lateinit var staggeredChip : Chip
    var userPassword = ""
    lateinit var resetButton : Button
    override fun onCreateView(inflater: LayoutInflater,container:ViewGroup?,savedInstanceState: Bundle?): View? {
val view = inflater.inflate(R.layout.settings_screen,container,false)

        prefs = MainActivity.passWordPreferences
        passwordy = MainActivity.passWordPreferences.getString("password","")!!
        passwordEnabled = MainActivity.passWordPreferences.getBoolean("enablePassword",true)
        layoutOption = MainActivity.passWordPreferences.getString("layoutOption","linear").toString()
layoutChipOptions = view.findViewById(R.id.layoutControlChipGroup)
linearChip = view.findViewById(R.id.linearChip)
        gridChip = view.findViewById(R.id.gridChip)
        staggeredChip = view.findViewById(R.id.staggeredChip)
layoutChipOptions.setOnCheckedChangeListener(layoutOptionChipGroupListener)
        enablePassword =view.findViewById(R.id.enablePasswordSwitch)
        enablePassword.isChecked=passwordEnabled
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
enablePassword.setOnCheckedChangeListener(enablePasswordChangeListener)
        layoutChipOptions.setOnClickListener {
            if(MainActivity.passWordPreferences.getString("layoutOption","linear")=="linear")
            linearChip.performClick()
        else
            gridChip.performClick()
        }
        layoutChipOptions.performClick()
        return  view
    }

var layoutOptionChipGroupListener= ChipGroup.OnCheckedChangeListener{
group, checkedId ->
    val passwordEditor : SharedPreferences.Editor = MainActivity.passWordPreferences.edit()
when(checkedId){
R.id.gridChip->
    layoutOption = "grid"
   R.id.linearChip -> layoutOption = "linear"
    R.id.staggeredChip -> layoutOption = "staggered"
}
    passwordEditor.putString("layoutOption",layoutOption)
    passwordEditor.apply()
}
    var enablePasswordChangeListener  = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        passwordEnabled = isChecked
        val passWordEditor :SharedPreferences.Editor = MainActivity.passWordPreferences.edit()
        passWordEditor.putBoolean("enablePassword",passwordEnabled)
        Log.i("Check", "Password is  enabled = $passwordEnabled")
        passWordEditor.apply()

    }


var resetButtonListener = View.OnClickListener {
    val passwordEditor : SharedPreferences.Editor = MainActivity.passWordPreferences.edit()
    passwordEditor.putString("password","")
    passwordEditor.putBoolean("firstUse",false)
    passwordEditor.putBoolean("enablePassword",false)
    passwordEditor.putString("layoutOption","linear")
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
