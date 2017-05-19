package com.pgt.app

import android.bluetooth.BluetoothDevice

data class State(
        val nav: Navigation,
        val btDevice: BluetoothDevice?) {

    companion object {
        fun getDefault(): State {
            return State(Navigation.MAIN, null)
        }
    }
}

enum class Navigation {
    MAIN,
}
