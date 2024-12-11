package com.example.firebase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

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

        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            task ->
            if (!task.isSuccessful){
                Log.e("NotificationToken", task.exception.toString())
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("NotificationToken", "Firebase token: $token")
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            Log.i("Permission: ", "Granted")
        }else{
            Log.i("Permission: ", "Denied")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
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