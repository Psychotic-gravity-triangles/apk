package com.pgt.app

import android.content.Context
import android.widget.LinearLayout
import trikita.anvil.RenderableView
import trikita.anvil.DSL.*

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
                onClick { (context as MainActivity).scanLeDevice(true) }
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
                onClick {

                }
            }

            button {
                size(0, dip(60))
                weight(1f)
                text("GREEN")
                onClick {

                }
            }

            button {
                size(0, dip(60))
                weight(1f)
                text("BLUE")
                onClick {

                }
            }
        }
    }
}