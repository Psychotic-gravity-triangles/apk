package com.pgt.app

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationSpy: NotificationListenerService() {

    private val TAG = "NotificationSpy"

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: $intent")
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationPosted")
        Log.d(TAG, sbn.packageName)
        Log.d(TAG, sbn.notification.category)
        Log.d(TAG, sbn.notification.tickerText.toString())
        Log.d(TAG, sbn.toString())
        Log.d(TAG, sbn.notification.toString())

        val msg = sbn.notification.tickerText.toString().toLowerCase()
        if (msg.contains("red")) {
            writeToDevice(1, 0, 0, 16)
        } else if (msg.contains("green")) {
            writeToDevice(0, 1, 0, 16)
        } else if (msg.contains("blue")) {
            writeToDevice(0, 0, 1, 16)
        } else if (msg.contains("cyan")) {
            writeToDevice(0, 1, 1, 16)
        } else if (msg.contains("magenta")) {
            writeToDevice(1, 0, 1, 16)
        } else if (msg.contains("yellow")) {
            writeToDevice(1, 1, 0, 16)
        } else if (msg.contains("white")) {
            writeToDevice(1, 1, 1, 16)
        } else if (msg.contains("black") || msg.contains("off")) {
            writeToDevice(0, 0, 0, 0)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationRemoved")
    }

    private fun writeToDevice(b0: Int, b1: Int, b2: Int, b3: Int) {
        val bytes = ByteArray(4)
        bytes[0] = b0.toByte()
        bytes[1] = b1.toByte()
        bytes[2] = b2.toByte()
        bytes[3] = b3.toByte()
        val intent = Intent(this@NotificationSpy, BleService::class.java)
        intent.action = MainActivity.SEND_COMMAND_ACTION
        intent.putExtra("command", bytes)
        startService(intent)
    }

}
