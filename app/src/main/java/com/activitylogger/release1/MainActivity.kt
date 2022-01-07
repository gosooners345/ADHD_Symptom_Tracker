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
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import androidx.navigation.ui.setupWithNavController
import com.activitylogger.release1.databinding.ActivityMainBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.content.SharedPreferences
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.activitylogger.release1.settings.AppSettingsFragment
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {


    var correctPassword = false
lateinit var fragmentManager : FragmentManager
lateinit var enterButton : Button
lateinit var appPassword :String
lateinit var skipButton :Button
lateinit var firstUse :Any
    lateinit var passwordTextBox : TextInputLayout
var userPassword = ""
    private lateinit var binding: ActivityMainBinding
    lateinit var mainActionButton: ExtendedFloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passWordPreferences = getSharedPreferences("ADHDTracker", MODE_PRIVATE)

        //Intro guide for new users
        setContentView(R.layout.app_intro_layout)
         firstUse = passWordPreferences.getBoolean("firstUse",false)
        if(firstUse==false)
        firstUser()
else
    firstUse()
        //Log in to the app before accessing the records.
        //For security purposes the password is stored locally



    }


    fun firstUser()
    {
fragmentManager = supportFragmentManager
        val paperOnboardingFragment = PaperOnboardingFragment.newInstance(onBoarding())
var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout,paperOnboardingFragment)
        fragmentTransaction.commit()
 skipButton = findViewById<Button>(R.id.skipButton)
        skipButton.setOnClickListener(skipButtonClickListener)


    }

    fun firstUse()
    {
        if(firstUse==false)
        firstUse = true
        val passWordEditor :SharedPreferences.Editor = passWordPreferences.edit()
        passWordEditor.putBoolean("firstUse", firstUse as Boolean)
        passWordEditor.apply()
        setContentView(R.layout.login_screen)



        appPassword = passWordPreferences.getString("password", "").toString()

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
        if (appPassword == "") {
            enterButton.text = "Save"
            enterButton.setOnClickListener(saveButtonClickListener)
        } else {
            enterButton.text = "Log In"
            enterButton.setOnClickListener(loginButtonClickListener)
        }
    }

    var skipButtonClickListener = View.OnClickListener {
        firstUse()


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
            /*supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, AppSettingsFragment())
                .commit()*/
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


    fun onBoarding() : ArrayList<PaperOnboardingPage> {
        val introList = ArrayList<PaperOnboardingPage>()
        val firstPage = PaperOnboardingPage(
            "Welcome!",
           String.format("Welcome to the ADHD Journal! \nThis is a personal diary for recording anything and everything that impacts your life. " +
                   "\nThis can help you in therapy and so much more!\n  Swipe right to continue."),
            resources.getColor(R.color.red),
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_next_arrow
        )
        val secondPage = PaperOnboardingPage("Security", String.format("This is your personal journal. \n" +
                "    It would be wise to protect it with a passcode or password so other people do not go prying into your private matters without your permission. \n" +
                "    Make sure you remember it or else you may have trouble accessing your info."),resources.getColor(R.color.red), R.drawable.ic_security_lock,
        R.drawable.ic_next_arrow)
        val thirdPage = PaperOnboardingPage("Logging",String.format("You can record events by simply hitting record on the home screen. \n" +
                "        You can log event details, emotions surrounding event, any lessons learned, sources of pain, etc.\n" +
                "        You are able to rate how the event was from your perspective from 0(bad) to 100(good).\n" +
                "        You are also able to record ADHD Symptoms that could've influenced the event by selecting from the dropdown on the form.\n" +
                "        Hit save and its logged."),resources.getColor(R.color.red),R.drawable.ic_baseline_edit_24,R.drawable.ic_next_arrow)
        val fourthPage = PaperOnboardingPage("Statistics",String.format("    Track your statistics here. You can see how you are doing on rating, success/fail percentage, and emotional statistics."),resources.getColor(R.color.red),R.drawable.ic_dashboard_black_24dp,R.drawable.ic_next_arrow)
val fifthPage= PaperOnboardingPage("Finally",String.format("I hope you were able to follow me through this tutorial long enough to get to this point.  Next up, you will need to create a password to save for your diary. (Do not worry, your secrets are safe in here)\n" +
        "         Hit the skip button below to create your password.  If you want to change it, head to the settings page and you can type a new password in without a problem!\n" +
        "        Let us make mental health discussions a more pleasant experience for everyone!"),R.color.red,R.drawable.ic_home_black_24dp,R.drawable.ic_home_black_24dp)
introList.add(firstPage)
        introList.add(secondPage)
        introList.add(thirdPage)
        introList.add(fourthPage)
        introList.add(fifthPage)
        return  introList
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
            .setTitle("Incorrect Password")
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