package com.pgt.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {

    private val TAG = "BlinkActivity"

    companion object {
        val START_SCANNING_ACTION = "com.pgt.app.SCAN"
        val SEND_COMMAND_ACTION = "com.pgt.app.LED"
        val DISCONNECT_ACTION = "com.pgt.app.DISCONNECT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "BLE is not supported on this device!")
            finish()
        } else {
            Log.d(TAG, "BLE is supported")
        }

        val mainView = MainView(this)
        App.Companion.getNavigationController()!!.setInitView(this@MainActivity, mainView)
        setContentView(mainView)
    }

    private val PERMISSION_REQUEST_COARSE_LOCATION = 1

    override fun onResume() {
        super.onResume()
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this@MainActivity).
                        setTitle("This app needs location access").
                        setMessage("Please grant location access so this app can detect Blink device").
                        setPositiveButton(android.R.string.ok, null).
                        setOnDismissListener { dialog ->
                            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_COARSE_LOCATION)
                        }
                builder.show()
            } else {
                println("BLE permissions are already granted, start scanning")
                serviceCall(START_SCANNING_ACTION)
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        serviceCall(DISCONNECT_ACTION)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults!![0] == PackageManager.PERMISSION_GRANTED) {
                    println("BLE permissions are granted by the user, start scanning")
                    serviceCall(START_SCANNING_ACTION)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult(): $requestCode $resultCode $data")
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult() - bluetooth is now enabled")
            serviceCall(START_SCANNING_ACTION)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun serviceCall(action: String) {
        val intent = Intent(this@MainActivity, BleService::class.java)
        intent.action = action
        startService(intent)
    }

    fun startScanning() {
        serviceCall(START_SCANNING_ACTION)
    }
}
