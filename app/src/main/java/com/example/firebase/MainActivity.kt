package com.example.firebase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebase.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var analytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analytics = Firebase.analytics

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.navView

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        checkAuthenticationStatus()

        askNotificationPermission()
        setupFirebaseMessaging()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_register) {
                navView.visibility = View.GONE // Hide Nav Bar
            }else if (destination.id == R.id.navigation_login) {
                navView.visibility = View.GONE
            }
            else {
                navView.visibility = View.VISIBLE // Show Nav Bar
            }
        }
    }

    private fun checkAuthenticationStatus() {
        if (auth.currentUser == null) {
            navController.navigate(R.id.navigation_login)
            binding.navView.visibility = View.GONE // Hide Bottom Navigation while on Login
        } else {
            binding.navView.visibility = View.VISIBLE // Show Bottom Navigation for authenticated users
            navController.navigate(R.id.navigation_home)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {

            } else {

                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("Permission: ", "Granted")
        } else {
            Log.i("Permission: ", "Denied")
        }
    }

    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("NotificationToken", task.exception.toString())
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("NotificationToken", "Firebase token: $token")
        }
    }

    override fun onStart() {
        super.onStart()
        LogEvents.logEvent(analytics, "MainActivity", "started")
    }

    override fun onResume() {
        super.onResume()
        LogEvents.logEvent(analytics, "MainActivity", "resumed")
    }
}

object LogEvents {
    fun logEvent(
        analytics: FirebaseAnalytics,
        id: String,
        name: String
    ) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleOf(
            Pair(FirebaseAnalytics.Param.ITEM_ID, id),
            Pair(FirebaseAnalytics.Param.ITEM_NAME, name),
            Pair(FirebaseAnalytics.Param.CONTENT_TYPE, "image"))
        )
    }
}