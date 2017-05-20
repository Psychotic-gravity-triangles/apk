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
            BleService.Companion.writeToDevice(1, 0, 0, 16, this@NotificationSpy)
        } else if (msg.contains("green")) {
            BleService.Companion.writeToDevice(0, 1, 0, 16, this@NotificationSpy)
        } else if (msg.contains("blue")) {
            BleService.Companion.writeToDevice(0, 0, 1, 16, this@NotificationSpy)
        } else if (msg.contains("cyan")) {
            BleService.Companion.writeToDevice(0, 1, 1, 16, this@NotificationSpy)
        } else if (msg.contains("magenta")) {
            BleService.Companion.writeToDevice(1, 0, 1, 16, this@NotificationSpy)
        } else if (msg.contains("yellow")) {
            BleService.Companion.writeToDevice(1, 1, 0, 16, this@NotificationSpy)
        } else if (msg.contains("white")) {
            BleService.Companion.writeToDevice(1, 1, 1, 16, this@NotificationSpy)
        } else if (msg.contains("black") || msg.contains("off")) {
            BleService.Companion.writeToDevice(0, 0, 0, 0, this@NotificationSpy)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationRemoved")
    }
}
