package cn.yue.base.middle.init

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.yue.base.common.utils.Utils.getContext
import cn.yue.base.common.utils.device.NotificationUtils
import cn.yue.base.common.utils.device.NotificationUtils.ChannelConfig
import cn.yue.base.common.utils.device.NotificationUtils.initChannelConfig

object NotificationConfig {
    private const val CHANNEL_ID = "YUE_CHANNEL"
    fun initChannel() {
        val channelConfig = ChannelConfig(CHANNEL_ID, "通知", NotificationUtils.IMPORTANCE_DEFAULT)
        initChannelConfig(channelConfig)
    }

    fun notify(id: Int, notification: Notification?) {
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, notification)
    }

    fun notify(id: Int, title: String?, content: String?) {
        val builder = getNotification(title, content)
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, builder.build())
    }

    fun notify(id: Int, title: String?, content: String?, intent: Intent) {
        val builder = getNotification(title, content)
        val pendingIntent = PendingIntent.getActivities(getContext(), 0, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        val nm = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nm?.notify(id, builder.build())
    }

    fun getNotification(title: String?, content: String?): NotificationCompat.Builder {
        var builder: NotificationCompat.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(getContext(), CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(getContext())
            builder.priority = NotificationCompat.PRIORITY_DEFAULT
        }
        //标题
        builder.setContentTitle(title)
        //文本内容
        builder.setContentText(content)
        //小图标
//        builder.setSmallIcon(R.mipmap.ic_launcher)
        //设置点击信息后自动清除通知
        builder.setAutoCancel(true)
        return builder
    }
}