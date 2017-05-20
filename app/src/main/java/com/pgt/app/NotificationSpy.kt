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
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationRemoved")
    }
}
