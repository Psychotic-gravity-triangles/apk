package com.pgt.app

import android.bluetooth.BluetoothDevice
import trikita.jedux.Action
import trikita.jedux.Store

class Reducer : Store.Reducer<Action<*, *>, State> {

    override fun reduce(action: Action<*, *>, old: State): State {
        when (action.type as Actions) {
            Actions.NAVIGATE -> return old.copy(nav = action.value as Navigation)
            Actions.SET_DEVICE -> return old.copy(btDevice = action.value as BluetoothDevice)
            Actions.SET_SEND_SMS -> return old.copy(sendSms = action.value as Boolean)
            else -> return old
        }
        return old
    }
}
