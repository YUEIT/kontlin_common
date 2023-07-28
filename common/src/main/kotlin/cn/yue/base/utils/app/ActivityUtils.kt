package cn.yue.base.utils.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import cn.yue.base.Constant
import cn.yue.base.utils.Utils
import java.io.File

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
    
    fun installIntent(context: Context, downloadApk: String?): Intent? {
        if (TextUtils.isEmpty(downloadApk)) return null
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //适配Android Q,注意mFilePath是通过ContentResolver得到的，上述有相关代码
            val contentUri = FileProvider.getUriForFile(context, Constant.FILE_PROVIDER_AUTHORITY, file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else { //7.0以下
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}