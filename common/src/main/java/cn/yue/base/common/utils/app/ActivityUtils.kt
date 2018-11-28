package cn.yue.base.common.utils.app

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import cn.yue.base.common.utils.Utils
import java.util.*

/**
 * 介绍：Activity相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ActivityUtils {

    /**
     * 判断是否存在Activity
     *
     * @param packageName 包名
     * @param className   activity全路径类名
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    fun isActivityExists(packageName: String, className: String): Boolean {
        val intent = Intent()
        intent.setClassName(packageName, className)
        return !(Utils.getContext().packageManager.resolveActivity(intent, 0) == null ||
                intent.resolveActivity(Utils.getContext().packageManager) == null ||
                Utils.getContext().packageManager.queryIntentActivities(intent, 0).size == 0)
    }

    /**
     * 打开Activity
     *
     * @param packageName 包名
     * @param className   全类名
     * @param bundle      bundle
     */
    @JvmStatic
    fun launchActivity(packageName: String, className: String, bundle: Bundle? = null) {
        Utils.getContext().startActivity(IntentUtils.getComponentIntent(packageName, className, bundle))
    }

    /**
     * 获取launcher activity
     *
     * @param packageName 包名
     * @return launcher activity
     */
    @JvmStatic
    fun getLauncherActivity(packageName: String): String {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pm = Utils.getContext().packageManager
        val infos = pm.queryIntentActivities(intent, 0)
        for (info in infos) {
            if (info.activityInfo.packageName == packageName) {
                return info.activityInfo.name
            }
        }
        return "no $packageName"
    }


    /**
     * 获取栈顶Activity
     *
     * @return 栈顶Activity
     */
    @JvmStatic
    fun getTopActivity(): Activity? {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val activitiesField = activityThreadClass.getDeclaredField("mActivities")
            activitiesField.isAccessible = true
            var activities: Map<*, *>? = null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                activities = activitiesField.get(activityThread) as HashMap<*, *>
            } else {
                activities = activitiesField.get(activityThread) as ArrayMap<*, *>
            }
            for (activityRecord in activities.values) {
                val activityRecordClass = activityRecord!!.javaClass
                val pausedField = activityRecordClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(activityRecord)) {
                    val activityField = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    return activityField.get(activityRecord) as Activity
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}
