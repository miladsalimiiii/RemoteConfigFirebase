package com.milad.firebaseremoteconfig

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"
private const val TITLE_KEY = "title"
private const val DESCRIPTION_KEY = "description"
private const val MORE_INFO_KEY = "more_info"

class MainActivity : AppCompatActivity() {

    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        val configBuilder = FirebaseRemoteConfigSettings.Builder()
        /**
         * For developer mode I'm setting 0 (zero) second
         * The default mode is 12 Hours. So for production mode it will be 12 hours
         */
        if (BuildConfig.DEBUG) {
            val cacheInterval: Long = 0
            configBuilder.minimumFetchIntervalInSeconds = cacheInterval
        }

        /**
         * Set default Remote Config parameter values. An app uses the in-app default values, and
         * when you need to adjust those defaults, you set an updated value for only the values you
         * want to change in the Firebase console
         */
        firebaseRemoteConfig?.let {
            it.setConfigSettingsAsync(configBuilder.build())
            it.setDefaultsAsync(R.xml.remote_config_defaults)
            fetchRemoteTitle()
        }
    }

    private fun fetchRemoteTitle() {
        button_title!!.text = firebaseRemoteConfig!!.getString(TITLE_KEY)
        description!!.text = firebaseRemoteConfig!!.getString(DESCRIPTION_KEY)
        button_more!!.text = firebaseRemoteConfig!!.getString(MORE_INFO_KEY)
        firebaseRemoteConfig?.fetchAndActivate()
            ?.addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    val updated = task.result!!
                    Log.d(
                        TAG,
                        "Config params updated: $updated"
                    )
                    Toast.makeText(
                        this@MainActivity, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}