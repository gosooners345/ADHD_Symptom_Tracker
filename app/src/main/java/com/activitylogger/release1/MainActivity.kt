package com.activitylogger.release1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.activitylogger.release1.databinding.ActivityMainBinding
import com.activitylogger.release1.ui.home.HomeFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mainActionButton : ExtendedFloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,R.id.navigation_dashboard
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            mainActionButton = findViewById(R.id.record_button)
            mainActionButton.setOnClickListener(mainButtonClick)
        } catch (ex: Exception)
        {
            ex.printStackTrace()
            Toast.makeText(this,ex.message!!,Toast.LENGTH_LONG).show()

        }

    }

    var mainButtonClick =   View.OnClickListener {
        HomeFragment.newRecord(this,75)


    }
    companion object{
        const val versionName = BuildConfig.VERSION_NAME
        const val appName = BuildConfig.APPLICATION_ID
        const val buildType = BuildConfig.BUILD_TYPE
    }
}