package com.pgt.app

import android.app.Activity
import android.bluetooth.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.bluetooth.le.*
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGatt
import trikita.jedux.Action

class MainActivity : Activity() {

    private val TAG = "BlinkActivity"
    private val  REQUEST_ENABLE_BT = 100

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    private var mScanning: Boolean = false
    private var mHandler: Handler = Handler()

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mLEScanner: BluetoothLeScanner? = null

    private var mScanSettings: ScanSettings? = null
    private var mScanFilters: List<ScanFilter>? = null
    private var mGatt: BluetoothGatt? = null

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
        // Initializes Bluetooth adapter
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bm.adapter

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            if (!mScanning) {
                Log.d(TAG, "onResume() - bluetooth is enabled")
                setupLeScanner()
                scanLeDevice(true)
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        if (mGatt == null) {
            return
        }
        mGatt!!.close()
        mGatt = null
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult(): $requestCode $resultCode $data")
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult() - bluetooth is now enabled")
            setupLeScanner()
            scanLeDevice(true)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupLeScanner() {
        mLEScanner = mBluetoothAdapter!!.bluetoothLeScanner
        mScanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
        mScanFilters = ArrayList<ScanFilter>()
    }

    fun scanLeDevice(enable: Boolean) {
        Log.d(TAG, "scanLeDevice(): $enable")
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed({
                Log.d(TAG, "stop scanning by timer..")
                mLEScanner!!.stopScan(mScanCallback)
            }, SCAN_PERIOD)

            mScanning = true
            Log.d(TAG, "start scanning..")
            mLEScanner!!.startScan(mScanFilters, mScanSettings, mScanCallback)
        } else {
            mScanning = false
            Log.d(TAG, "stop scanning..")
            mLEScanner!!.stopScan(mScanCallback)
        }
    }

    // Device scan callback.
    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, callbackType.toString())
            Log.d(TAG, result.toString())
            val btDevice = result.device
            Log.d(TAG, "onScanResult(): device discovered - $btDevice ${btDevice.name}")
            if (btDevice.name != null && btDevice.name.equals("Blink")) {
                Log.d(TAG, "BLINK device is detected!!!")
                connectToDevice(btDevice)
                App.Companion.dispatch(Action(Actions.SET_DEVICE, btDevice))
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            for (sr in results) {
                Log.d(TAG, "onBatchScanResults(): ${sr.device.name}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Error Code: $errorCode")
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        Log.d(TAG, "connectToDevice(): $device")
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, mGattCallback)
            scanLeDevice(false)     // will stop after first device detection
        }
    }

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange(): status $status newState $newState")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "STATE_CONNECTED")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> Log.d(TAG, "STATE_DISCONNECTED")
                else -> Log.d(TAG, "STATE_OTHER")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val services = gatt.services
            Log.d(TAG, "onServiceDiscovered(): $services")
            gatt.readCharacteristic(services[1].characteristics[0])
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d(TAG, "onCharacteristicRead(): $characteristic")
            gatt.disconnect()
        }
    }
}
