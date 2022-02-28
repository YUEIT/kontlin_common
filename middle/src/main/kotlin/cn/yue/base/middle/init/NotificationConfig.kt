package cn.yue.base.middle.init

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.yue.base.common.utils.Utils.getContext
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.device.NotificationUtils
import cn.yue.base.common.utils.device.NotificationUtils.ChannelConfig
import cn.yue.base.common.utils.device.NotificationUtils.initChannelConfig
import cn.yue.base.middle.R

object NotificationConfig {
    private const val CHANNEL_ID = "YUE_CHANNEL"
    fun initChannel() {
        val channelConfig = ChannelConfig(CHANNEL_ID, R.string.app_notification.getString(), NotificationUtils.IMPORTANCE_DEFAULT)
        initChannelConfig(channelConfig)
    }

    fun notify(id: Int, notification: Notification?) {
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, notification)
    }

    fun notify(id: Int, title: String?, content: String?) {
        val builder = getNotification(title, content, null)
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, builder.build())
    }

    fun notify(id: Int, title: String?, content: String?, progress: Int?, intent: Intent? = null) {
        val builder = getNotification(title, content, progress, intent)
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, builder.build())
    }

    fun notify(id: Int, title: String?, content: String?, intent: Intent) {
        val builder = getNotification(title, content, null)
        val pendingIntent = PendingIntent.getActivities(getContext(), 0, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, builder.build())
    }

    fun getNotification(title: String?, content: String?, progress: Int?, intent: Intent? = null): NotificationCompat.Builder {
        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(getContext(), CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(getContext())
            builder.priority = NotificationCompat.PRIORITY_DEFAULT
        }
        //标题
        builder.setContentTitle(title)
        //小图标
        builder.setSmallIcon(R.drawable.ic_launcher)
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
            val pendingIntent = PendingIntent.getActivities(getContext(),
                    0, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentIntent(pendingIntent)
        }
        //设置点击信息后自动清除通知
        builder.setAutoCancel(true)
        return builder
    }
}