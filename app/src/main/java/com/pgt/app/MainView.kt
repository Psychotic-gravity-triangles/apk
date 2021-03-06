package com.pgt.app

import android.content.Context
import android.widget.CompoundButton
import android.widget.LinearLayout
import trikita.anvil.RenderableView
import trikita.anvil.DSL.*
import trikita.jedux.Action

class MainView(context: Context): RenderableView(context) {
    override fun view() {
        val dev = App.Companion.state().btDevice
        linearLayout {
            size(FILL, FILL)
            orientation(LinearLayout.VERTICAL)
            padding(dip(10))

            button {
                size(FILL, dip(60))
                margin(0, 0, 0, dip(10))
                text("Scan BLE devices")
                onClick { (context as MainActivity).startScanning() }
            }

            checkBox {
                size(WRAP, WRAP)
                text("Send SMS once connection established")
                textSize(sip(18f))
                checked(App.Companion.state().sendSms)
                onCheckedChange { _: CompoundButton?, b: Boolean ->
                    App.Companion.dispatch(Action(Actions.SET_SEND_SMS, b))
                }
            }

            if (dev != null) {
                textView {
                    size(FILL, WRAP)
                    padding(0, dip(10))
                    text("Device: ${dev.name}")
                    textSize(sip(18f))
                }
                textView {
                    size(FILL, WRAP)
                    padding(0, dip(10))
                    text("Bond state: ${dev.bondState}")
                    textSize(sip(18f))
                }
                textView {
                    size(FILL, WRAP)
                    padding(0, dip(10))
                    text("Address: ${dev.address}")
                    textSize(sip(18f))
                }
                textView {
                    size(FILL, WRAP)
                    padding(0, dip(10))
                    text("Type: ${dev.type}")
                    textSize(sip(18f))
                }

                blinkButtons()
            }
        }
    }

    private fun blinkButtons() {
        linearLayout {
            size(FILL, WRAP)

            button {
                size(0, dip(60))
                weight(1f)
                text("RED")
                onClick { BleService.Companion.writeToDevice(1, 0, 0, 0, context) }
            }

            button {
                size(0, dip(60))
                weight(1f)
                text("GREEN")
                onClick { BleService.Companion.writeToDevice(0, 1, 0, 0, context) }
            }

            button {
                size(0, dip(60))
                weight(1f)
                text("BLUE")
                onClick { BleService.Companion.writeToDevice(0, 0, 1, 16, context) }
            }
        }
        button {
            size(FILL, dip(60))
            margin(0, dip(10))
            text("OFF")
            onClick { BleService.Companion.writeToDevice(0, 0, 0, 0, context) }
        }
    }
}
