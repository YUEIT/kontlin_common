package cn.yue.base.utils.app

import android.app.Activity
import android.content.Context
import cn.yue.base.utils.Utils

object ActivityUtils {

    fun isActivityAlive(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    fun getTopActivity(): Activity? {
        return ActivityLifecycleImpl.INSTANCE.getTopActivity()
    }

    fun requireContext(): Context {
        val topActivity = ActivityLifecycleImpl.INSTANCE.getTopActivity()
        if (topActivity != null) {
            return topActivity
        }
        return Utils.getContext()
    }
}