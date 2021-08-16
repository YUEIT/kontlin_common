package cn.yue.base.kotlin.test.utils

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference
import java.util.*

object TestGC {
    private const val TAG = "TestGc"
    private var activityWeakReference: WeakReference<Activity>? = null
    private var fragmentWeakReference: WeakReference<Fragment>? = null
    fun setActivityWeakReference(activity: Activity) {
        activityWeakReference = WeakReference(activity)
    }

    fun setFragmentWeakReference(fragment: Fragment) {
        fragmentWeakReference = WeakReference(fragment)
    }

    fun show() {
        if (activityWeakReference != null) {
            Log.d(TAG, "cache activity: " + activityWeakReference!!.get())
        } else {
            Log.d(TAG, "cache activity: " + null)
        }
        if (fragmentWeakReference != null) {
            Log.d(TAG, "cache fragment: " + fragmentWeakReference!!.get())
        } else {
            Log.d(TAG, "cache fragment: ")
        }
    }

    fun startTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                show()
            }
        }, 0, 5000)
    }

    fun gc() {
        Runtime.getRuntime().gc()
    }
}