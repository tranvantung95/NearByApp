package com.example.nearbyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }
    val btPublish: AppCompatButton by lazy {
        findViewById(R.id.btGoToPublisher)
    }
    val btSubscriber: AppCompatButton by lazy {
        findViewById(R.id.btGoToSubscriber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPer()
        btPublish.setOnClickListener {
            startActivity(
                Intent(
                    this, PublisherActivity::class.java
                )
            )
        }
        btSubscriber.setOnClickListener {
            startActivity(Intent(this, SubscribeActivity::class.java))
        }
    }

    fun requestPer() {
        val listPer = mutableListOf<String>().apply {
            this.add(android.Manifest.permission.BLUETOOTH)
            this.add(android.Manifest.permission.BLUETOOTH_ADMIN)
            this.add(android.Manifest.permission.ACCESS_WIFI_STATE)
            this.add(android.Manifest.permission.CHANGE_WIFI_STATE)
            this.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            this.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            this.add(android.Manifest.permission.ACCESS_WIFI_STATE)
            this.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listPer.add(android.Manifest.permission.BLUETOOTH_ADVERTISE)
            listPer.add(android.Manifest.permission.BLUETOOTH_CONNECT)
            listPer.add(android.Manifest.permission.BLUETOOTH_SCAN)
        }
        requestPermissionLauncher.launch(listPer.toTypedArray())
    }
}