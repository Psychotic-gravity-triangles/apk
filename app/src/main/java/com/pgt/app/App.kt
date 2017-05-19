package com.pgt.app

import android.app.Application
import com.squareup.picasso.Picasso
import trikita.anvil.Anvil
import trikita.jedux.Action
import trikita.jedux.Store

class App : Application() {

    private var store: Store<Action<*, *>, State>? = null
    private var navigationController: NavigationController? = null

    private var picasso: Picasso? = null

    override fun onCreate() {
        super.onCreate()
        println("$TAG: onCreate()")

        instance = this
//        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//            throwable.printStackTrace()
//            System.exit(2)
//        }

        val builder = Picasso.Builder(this)
        this@App.picasso = builder.build()

        this@App.navigationController = NavigationController()
        this@App.store = Store(Reducer(), State.getDefault(),
                navigationController)

        this@App.store!!.subscribe { Anvil.render() }
    }

    companion object {
        val TAG = "App"

        var instance: App? = null
            private set

        fun state(): State {
            return instance!!.store!!.state
        }

        fun dispatch(action: Action<*, *>): State {
            return instance!!.store!!.dispatch(action)
        }

        fun picasso(): Picasso? {
            return instance!!.picasso
        }

        fun getNavigationController(): NavigationController? {
            return instance!!.navigationController
        }
    }
}
