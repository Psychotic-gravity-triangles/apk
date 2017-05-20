package com.pgt.app

import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import trikita.jedux.Action

class BleService : Service() {

    val TAG = "BleService"

    private val  mBinder: IBinder = BleBinder()

    private val mHandler = Handler(Looper.getMainLooper())

    inner class BleBinder : Binder() {
        fun getService() : BleService { return this@BleService }
    }

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    private var mScanning: Boolean = false

    private var mLEScanner: BluetoothLeScanner? = null
    private var mScanSettings: ScanSettings? = null
    private var mScanFilters: List<ScanFilter>? = null
    private var mGatt: BluetoothGatt? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand(): $intent")

        if (intent == null) return START_STICKY

        val action = intent.action
        when (action) {
            MainActivity.Companion.START_SCANNING_ACTION -> scan()
            MainActivity.Companion.SEND_COMMAND_ACTION -> sendCommand(intent.getByteArrayExtra("command"))
            MainActivity.Companion.DISCONNECT_ACTION -> disconnect()
            else -> Log.d(TAG, "Unknown intent action")
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        disconnect()
        super.onDestroy()
    }

    // ------------------------- EXTERNAL CALLS ----------------------------
    private fun scan() {
        if (!mScanning) {
            scanLeDevice(true)
        }
    }

    private fun sendCommand(bytes: ByteArray) {
        if (mGatt != null) {
            val characteristic = mGatt!!.services[2].characteristics[0]
            characteristic.value = bytes
            val status = mGatt!!.writeCharacteristic(characteristic)
            Log.d(TAG, "writeToDevice(): writing status $status")
        }
    }

    private fun disconnect() {
        if (mGatt == null) {
            return
        }
        mGatt!!.disconnect()
        mGatt!!.close()
        mGatt = null
    }
    // -----------------------------------------------------------------------

    fun scanLeDevice(enable: Boolean) {
        Log.d(TAG, "scanLeDevice(): $enable")
        val bm = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mLEScanner = bm.adapter!!.bluetoothLeScanner
        mScanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
        mScanFilters = ArrayList<ScanFilter>()

        if (enable) {
            // Stops scanning after a pre-defined scan period.
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
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "STATE_DISCONNECTED")
                    mGatt?.close()
                    mGatt = null
                    scanLeDevice(true)
                }
                else -> Log.d(TAG, "STATE_OTHER")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val services = gatt.services
            Log.d(TAG, "onServiceDiscovered(): $services")
            for (s in services) {
                Log.d(TAG, "--> ${s.uuid}")
                for (c in s.characteristics) {
                    Log.d(TAG, " ${c.uuid}")
                }
                Log.d(TAG, "---")
            }
//            gatt.readCharacteristic(services[2].characteristics[0])
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt,
                                          characteristic: BluetoothGattCharacteristic, status: Int) {
            Log.d(TAG, "onCharacteristicRead(): $characteristic")
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?,
                                           characteristic: BluetoothGattCharacteristic?,
                                           status: Int) {
            Log.d(TAG, "onCharacteristicWrite(): $characteristic $status")
        }
    }

    companion object {
        fun writeToDevice(b0: Int, b1: Int, b2: Int, b3: Int, c: Context) {
            val bytes = ByteArray(4)
            bytes[0] = b0.toByte()
            bytes[1] = b1.toByte()
            bytes[2] = b2.toByte()
            bytes[3] = b3.toByte()
            val intent = Intent(c, BleService::class.java)
            intent.action = MainActivity.SEND_COMMAND_ACTION
            intent.putExtra("command", bytes)
            c.startService(intent)
        }
    }
}
