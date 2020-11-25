package cn.yue.base.common.utils.app

import android.app.Activity
import android.content.Context
import android.os.Build
import cn.yue.base.common.utils.Utils

object ActivityUtils {

    fun isActivityAlive(activity: Activity?): Boolean {
        return (activity != null && !activity.isFinishing
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed))
    }

    fun getTopActivity(): Activity? {
        return ActivityLifecycleImpl.INSTANCE.topActivity
    }

    fun requireContext(): Context {
        val topActivity = ActivityLifecycleImpl.INSTANCE.topActivity
        if (topActivity != null) {
            return topActivity
        }
        return Utils.getContext()
    }
}