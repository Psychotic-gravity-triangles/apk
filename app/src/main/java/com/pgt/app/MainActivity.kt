package com.pgt.app

import android.app.Activity
import android.bluetooth.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.content.Context
import android.content.Intent
import android.bluetooth.BluetoothAdapter

class MainActivity : Activity() {

    private val TAG = "BlinkActivity"
    private val REQUEST_ENABLE_BT = 100

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

    override fun onResume() {
        super.onResume()
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bm.adapter == null || !bm.adapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            serviceCall(START_SCANNING_ACTION)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        serviceCall(DISCONNECT_ACTION)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult(): $requestCode $resultCode $data")
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult() - bluetooth is now enabled")
            serviceCall(START_SCANNING_ACTION)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun serviceCall(action: String) {
        val intent = Intent(this@MainActivity, BleService::class.java)
        intent.action = action
        startService(intent)
    }

    fun startScanning() {
        serviceCall(START_SCANNING_ACTION)
    }

    fun writeToDevice(b0: Int, b1: Int, b2: Int, b3: Int) {
        val bytes = ByteArray(4)
        bytes[0] = b0.toByte()
        bytes[1] = b1.toByte()
        bytes[2] = b2.toByte()
        bytes[3] = b3.toByte()
        val intent = Intent(this@MainActivity, BleService::class.java)
        intent.action = SEND_COMMAND_ACTION
        intent.putExtra("command", bytes)
        startService(intent)
    }
}
