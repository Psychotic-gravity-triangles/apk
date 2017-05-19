package com.pgt.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import trikita.jedux.Action
import trikita.jedux.Store

class NavigationController : Store.Middleware<Action<*, *>, State> {

    private val TAG = "NavigationController"

    private val mHandler = Handler()

    private var mActivity: Activity? = null
    private var mCurrentView: View? = null
    private var mLastPage: Navigation? = null

    fun setInitView(a: Activity, v: View) {
        mActivity = a
        mCurrentView = v
    }

    override fun dispatch(store: Store<Action<*, *>, State>, action: Action<*, *>, next: Store.NextDispatcher<Action<*, *>>) {
        mHandler.removeCallbacksAndMessages(null)
        next.dispatch(action)
        if (action.type == Actions.NAVIGATE) {
            fadeAnimation(action.value as Navigation)
        }
    }

    private fun fadeAnimation(nav: Navigation) {
        Log.d(TAG, "fadeAnimation()")
        mLastPage = nav
        val v = getPageView(mLastPage!!)
        (mActivity!!.findViewById(android.R.id.content) as ViewGroup).addView(v, 0)

        mCurrentView!!.animate().alpha(0.0f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mCurrentView = v
                (mActivity!!.findViewById(android.R.id.content) as ViewGroup).removeViewAt(1)
            }
        })
        v.alpha = 0.0f
        v.animate().alpha(1.0f).duration = 500
    }

    private fun getPageView(page: Navigation): View {
        val v: View
        when (page) {
            Navigation.MAIN -> v = MainView(mActivity!!)
            else -> v = MainView(mActivity!!)
        }
        return v
    }
}
