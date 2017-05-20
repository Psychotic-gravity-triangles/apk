package com.pgt.app

import android.bluetooth.BluetoothDevice

data class State(
        val nav: Navigation,
        val btDevice: BluetoothDevice?,
        val sendSms: Boolean) {

    companion object {
        fun getDefault(): State {
            return State(Navigation.MAIN, null, false)
        }
    }
}

enum class Navigation {
    MAIN,
}
