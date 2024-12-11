package com.example.firebase

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analytics = Firebase.analytics

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        LogEvents.logEvent(analytics, "MainActivity", "created")
    }
    override fun onStart(){
        super.onStart()
        LogEvents.logEvent(analytics, "MainActivity", "started")
    }

    override fun onResume(){
        super.onResume()
        LogEvents.logEvent(analytics, "MainActivity", "resumed")
    }
}

object LogEvents {
    fun logEvent(
        analytics: FirebaseAnalytics,
        id: String,
        name: String
    ){
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleOf(
            Pair(FirebaseAnalytics.Param.ITEM_ID, id),
            Pair(FirebaseAnalytics.Param.ITEM_NAME, name),
            Pair(FirebaseAnalytics.Param.CONTENT_TYPE, "image"))
        )
    }
}