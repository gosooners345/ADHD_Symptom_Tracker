package com.activitylogger.release1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.activitylogger.release1.databinding.ActivityMainBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {


    var correctPassword = false

lateinit var enterButton : Button
lateinit var appPassword :String
lateinit var passwordTextBox : TextInputLayout
var userPassword = ""
    private lateinit var binding: ActivityMainBinding
    lateinit var mainActionButton: ExtendedFloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)
        passWordPreferences= getSharedPreferences("ADHDTracker", MODE_PRIVATE)
        appPassword = passWordPreferences.getString("password","").toString()
        enterButton = findViewById(R.id.enterButton)
        passwordTextBox = findViewById(R.id.passwordTextBox)
        passwordTextBox.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                userPassword = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {


            }
        })

        if(appPassword=="")
        {

            enterButton.text = "Save"
            enterButton.setOnClickListener(saveButtonClickListener)
        }
        else
        {
enterButton.text = "Log In"
            enterButton.setOnClickListener(loginButtonClickListener)
        }



    }


    fun loadApp(){

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard,R.id.navigation_settings
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            mainActionButton = findViewById(R.id.record_button)
            mainActionButton.setOnClickListener(mainButtonClick)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(this, ex.message!!, Toast.LENGTH_LONG).show()

        }
    }

    var mainButtonClick = View.OnClickListener {
        HomeFragment.newRecord(this, 75)


    }


    var saveButtonClickListener = View.OnClickListener {
        userPassword = passwordTextBox.editText!!.text.toString()
        storePassword(userPassword)
        loadApp()
    }

    var loginButtonClickListener = View.OnClickListener {
        userPassword = passwordTextBox.editText!!.text.toString()
       correctPassword =  LogIn(userPassword)
   if(correctPassword)
       loadApp()
    }

    fun LogIn(password: String) : Boolean {


if(password != appPassword)
{

    MaterialAlertDialogBuilder(this)
            .setTitle("Save Record?")
            .setMessage(String.format("Invalid Password, Try Again?"))
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setPositiveButton("Yes") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    Toast.makeText(this,"Invalid Password, try again",Toast.LENGTH_LONG).show()
return false
}
        else{
    Toast.makeText(this,"Password correct",Toast.LENGTH_LONG).show()
return true

        }
    }



    fun storePassword(password: String)
    {
        val passWordEditor :SharedPreferences.Editor = passWordPreferences.edit()

        passWordEditor.putString("password",password)
        passWordEditor.apply()

correctPassword = true
    }


    companion object{
        const val versionName = BuildConfig.VERSION_NAME
        const val appName = BuildConfig.APPLICATION_ID
        lateinit var  passWordPreferences :SharedPreferences
        const val buildType = BuildConfig.BUILD_TYPE
    }
}