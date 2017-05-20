package com.pgt.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AutomateReceiver: BroadcastReceiver() {

    private val TAG = "AutomateReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val color = intent?.getStringExtra("color")
        val blink = if (intent?.getBooleanExtra("blink", false) == true) {
            16
        } else {
            0
        }
        Log.d(TAG, "onReceive(): $intent $color $blink")
        if (color != null) {
            when (color) {
                "red" -> BleService.Companion.writeToDevice(1, 0, 0, blink, context!!)
                "green" -> BleService.Companion.writeToDevice(0, 1, 0, blink, context!!)
                "blue" -> BleService.Companion.writeToDevice(0, 0, 1, blink, context!!)
                "cyan" -> BleService.Companion.writeToDevice(0, 1, 1, blink, context!!)
                "magenta" -> BleService.Companion.writeToDevice(1, 0, 1, blink, context!!)
                "yellow" -> BleService.Companion.writeToDevice(1, 1, 0, blink, context!!)
                "white" -> BleService.Companion.writeToDevice(1, 1, 1, blink, context!!)
                "black", "off" -> BleService.Companion.writeToDevice(0, 0, 0, 0, context!!)
                else -> Log.d(TAG, "Unknown Automate intent data")
            }
        }
    }
}