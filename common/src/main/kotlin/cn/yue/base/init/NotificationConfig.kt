package cn.yue.base.init

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cn.yue.base.common.R
import cn.yue.base.utils.Utils.getContext
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.device.NotificationUtils
import cn.yue.base.utils.device.NotificationUtils.hasPermission
import cn.yue.base.utils.device.NotificationUtils.initChannelConfig

object NotificationConfig {
    
    private const val CHANNEL_ID = "YUE_CHANNEL"
    
    fun initChannel() {
        val channelBuilder = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(R.string.app_notification.getString())
            .build()
        initChannelConfig(channelBuilder)
    }

    fun notify(id: Int, title: String?, content: String?) {
        if (!hasPermission() || !NotificationUtils.areNotificationsEnabled()) {
            return
        }
        val builder = getNotification(title, content, null)
        notify(id, builder.build())
    }

    fun notify(id: Int, title: String?, content: String?, progress: Int?, intent: Intent? = null) {
        if (!hasPermission() || !NotificationUtils.areNotificationsEnabled()) {
            return
        }
        val builder = getNotification(title, content, progress, intent)
        notify(id, builder.build())
    }

    fun notify(id: Int, title: String?, content: String?, intent: Intent) {
        if (!hasPermission() || !NotificationUtils.areNotificationsEnabled()) {
            return
        }
        val builder = getNotification(title, content, null, intent)
        notify(id, builder.build())
    }
    
    fun notify(id: Int, notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val nmc = NotificationManagerCompat.from(getContext())
        nmc.notify(id, notification)
    }

    fun getNotification(title: String?, content: String?, progress: Int?, intent: Intent? = null)
        : NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(getContext(), CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //标题
            .setContentTitle(title)
            //小图标
            .setSmallIcon(R.drawable.app_icon_clear)
        if (progress != null) {
            if (progress >= 100) {
                builder.setContentText(content)
                builder.setProgress(0, 0, false)
            } else {
                builder.setProgress(100, progress, false)
            }
        } else {
            //文本内容
            builder.setContentText(content)
        }
        if (intent != null) {
            val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            } else {
                PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            builder.setContentIntent(pendingIntent)
        }
        //设置点击信息后自动清除通知
        builder.setAutoCancel(true)
        return builder
    }
}