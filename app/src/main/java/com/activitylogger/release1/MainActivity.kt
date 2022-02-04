@file:Suppress("unused", "EXPERIMENTAL_API_USAGE")

package com.activitylogger.release1

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.activitylogger.release1.databinding.ActivityMainBinding
import com.activitylogger.release1.security.Secure
import com.activitylogger.release1.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import kotlinx.coroutines.DelicateCoroutinesApi
import net.sqlcipher.database.SQLiteDatabase


@Suppress("SimplifyBooleanWithConstants", "CascadeIf")
@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {


    private var correctPassword = false
    private lateinit var fragmentManager: FragmentManager
    private lateinit var passWordPreferences: SharedPreferences
    private lateinit var enterButton: Button
    lateinit var appPassword: String
    private lateinit var skipButton: Button
    private lateinit var oldPrefs: SharedPreferences
    private var passwordEnabled = true
    lateinit var title: TextView
    private lateinit var firstUse: Any
    private lateinit var passwordTextBox: TextInputLayout
var dbPassword = ""
    var userPassword = ""
    private lateinit var enablePasswordSwitch: SwitchMaterial
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActionButton: ExtendedFloatingActionButton
    var trusted = false
     var timesRan=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SQLiteDatabase.loadLibs(this)
        oldPrefs = getSharedPreferences("ADHDTracker", MODE_PRIVATE)
        oldPrefs.edit().clear().apply()

        oldPrefs = getSharedPreferences(PREFNAME, MODE_PRIVATE)
        passWordPreferences = getSecretSharedPref(this)

        var transferred = passWordPreferences.getBoolean("transferred", false)


        appPreferences = passWordPreferences

        appPassword = passWordPreferences.getString("password", "").toString()

        if (transferred == false) {
            transferred = true
           //For after ConfessionSearchAppTest is successfully completed

            passWordPreferences.edit()
                .putBoolean("firstUse", oldPrefs.getBoolean("firstUse", false))
                .putString("password", oldPrefs.getString("password", ""))
                .putString("layoutOption", oldPrefs.getString("layoutOption", "linear"))
                .putBoolean("enablePassword", oldPrefs.getBoolean("enablePassword", true))

                .putBoolean("transferred", transferred).apply()

        }
        if (transferred) {
            oldPrefs.edit().clear().apply()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trusted = passWordPreferences.getBoolean("trusted", false)
            if (trusted == false) {
                dbPassword = appPassword
                trusted = true
                passWordPreferences.edit().putBoolean("trusted", true)
                    .putString("dbPassword", dbPassword).apply()
            } else {

                dbPassword = passWordPreferences.getString("dbPassword", "").toString()
            }
        }
        //Intro guide for new users
        setContentView(R.layout.app_intro_layout)
        firstUse = try {
            passWordPreferences.getBoolean("firstUse", false)
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
        passwordEnabled = passWordPreferences.getBoolean("enablePassword", true)
        if (firstUse == false)
        {
            timesRan = passWordPreferences.getInt("dbTimes",0)
            timesRan++
            passWordPreferences.edit().putInt("dbTimes",timesRan).apply()
            firstUser()

        }
        else if (passwordEnabled)
         loginScreen()
        else
            loadApp()
    }

    companion object {
        const val versionName = BuildConfig.VERSION_NAME
        private const val appName = BuildConfig.APPLICATION_ID
        const val PREFNAME = appName + "_preferences"
        const val buildType = BuildConfig.BUILD_TYPE
        lateinit var appPreferences: SharedPreferences

    }

    private fun firstUser() {
        fragmentManager = supportFragmentManager
        val paperOnboardFragment = PaperOnboardingFragment.newInstance(onBoarding())
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, paperOnboardFragment)
        fragmentTransaction.commit()
        skipButton = findViewById(R.id.skipButton)
        skipButton.setOnClickListener(skipButtonClickListener)


    }

    @SuppressLint("SetTextI18n")
    fun loginScreen() {


        setContentView(R.layout.login_screen)
        enablePasswordSwitch = findViewById(R.id.enablePasswordSwitch)
        title = findViewById(R.id.Login_TitleHdr)
        val passWordEditor: SharedPreferences.Editor = passWordPreferences.edit()
        if (firstUse == false) {
            firstUse = true
            passWordEditor.putBoolean("firstUse", firstUse as Boolean)
            passWordEditor.apply()
            enablePasswordSwitch.isChecked = true
        } else
            enablePasswordSwitch.visibility = View.GONE

        appPassword = passWordPreferences.getString("password", "").toString()

        enterButton = findViewById(R.id.enterButton)
        passwordTextBox = findViewById(R.id.passwordTextBox)
        passwordTextBox.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                userPassword = s.toString()
               if (userPassword.length == appPassword.length)
                    if (logIn(userPassword)) {
                        loadApp()
                    }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        if (appPassword == "") {
            title.text = "Type in a password to secure your journal!"
            enterButton.text = "Save"
            enterButton.setOnClickListener(saveButtonClickListener)
        } else {
            timesRan = passWordPreferences.getInt("dbTimes", 0)
            if (timesRan <= 1)
            {
                timesRan++
                passWordPreferences.edit().putInt("dbTimes",timesRan).apply()
            }
            title.text = "Welcome Back! Enter your password below to log into your journal!"

            enterButton.text = "Log In"
            enterButton.setOnClickListener(loginButtonClickListener)
        }
    }

    private var skipButtonClickListener = View.OnClickListener {
        loginScreen()
    }

    fun loadApp() {

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val navView: BottomNavigationView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard
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

    @DelicateCoroutinesApi
    private var mainButtonClick = View.OnClickListener {
        HomeFragment.newRecord(this, 75)
    }

    private fun onBoarding(): ArrayList<PaperOnboardingPage> {
        val introList = ArrayList<PaperOnboardingPage>()
        val firstPage = PaperOnboardingPage(
            "Welcome!",
            String.format(
                "Welcome to the ADHD Journal! \nThis is a personal diary for recording anything and everything that impacts your life. " +
                        "\nThis can help you in therapy and so much more!\n  Swipe left to continue."
            ),
            resources.getColor(R.color.white),
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_next_arrow
        )
        val secondPage = PaperOnboardingPage(
            "Security", String.format(
                "This is your personal journal. \n" +
                        "    It would be wise to protect it with a passcode or password so other people do not go prying into your private matters without your permission. \n" +
                        "    Make sure you remember it or else you may have trouble accessing your info."
            ), resources.getColor(R.color.white), R.drawable.ic_security_lock,
            R.drawable.ic_next_arrow
        )
        val thirdPage = PaperOnboardingPage(
            "Record Entries",
            String.format(
                "You can record events by simply hitting record on the home screen. \n" +
                        "        You can log event details, emotions surrounding event, any lessons learned, sources of pain, etc.\n" +
                        "        You can rate the entry  from your perspective on a scale from 0(bad) to 100(good).\n" +
                        " You can include any ADHD symptoms that impacted the event or entry by clicking the symptoms area on screen.        \n" +
                        "        Hit save and its logged."
            ),
            resources.getColor(R.color.white),
            R.drawable.ic_baseline_edit_24,
            R.drawable.ic_next_arrow
        )
        val fourthPage = PaperOnboardingPage(
            "Statistics",
            String.format("Track your statistics here. You can see how you are doing on rating, success/fail percentage, symptoms, and emotional statistics."),
            resources.getColor(R.color.white),
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_next_arrow
        )
        val fifthPage = PaperOnboardingPage(
            "Finally",
            String.format(
                "You can customize how you want the layout formatting to selecting relevant symptoms and viewing records to look in the settings. While you're there you can find links to rate this app and contact me directly. " +
                        "\r\nAlso, you can change your password there and enable/disable it as you please. "
            ),
            resources.getColor(R.color.white),
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp
        )
        introList.add(firstPage)
        introList.add(secondPage)
        introList.add(thirdPage)
        introList.add(fourthPage)
        introList.add(fifthPage)
        return introList
    }

    //Store Password
    private var saveButtonClickListener = View.OnClickListener {
        userPassword = passwordTextBox.editText!!.text.toString()
        val passWordEditor: SharedPreferences.Editor = passWordPreferences.edit()
        passWordEditor.putBoolean("enablePassword", passwordEnabled)
        passWordEditor.apply()
        storePassword(userPassword)
        loadApp()
    }

    private var loginButtonClickListener = View.OnClickListener {
        userPassword = passwordTextBox.editText!!.text.toString()
        correctPassword = logIn(userPassword)
        if (correctPassword || !passwordEnabled)
            loadApp()
    }

    // Log in Methods
    private fun logIn(password: String): Boolean {
        if (password != appPassword) {

            MaterialAlertDialogBuilder(this)
                .setTitle("Incorrect Password")
                .setMessage(String.format("Invalid Password, Try Again?"))
                .setNegativeButton("No") { _, _ ->
                    finish()
                }
                .setPositiveButton("Yes") { dialog, _ -> dialog.dismiss() }
                .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
            Toast.makeText(this, "Invalid Password, try again", Toast.LENGTH_LONG).show()
            return false
        }
        else {
            Toast.makeText(this, "Password correct", Toast.LENGTH_LONG).show()
            return true

        }
    }

    private fun storePassword(password: String) {
        passwordEnabled = enablePasswordSwitch.isChecked
        val passWordEditor: SharedPreferences.Editor = passWordPreferences.edit()
        passWordEditor.putString("password", password).putString("dbPassword", password)
            .putBoolean("enablePassword", passwordEnabled).apply()
        correctPassword = true
    }

    private fun getSecretSharedPref(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFNAME + "_secured",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}