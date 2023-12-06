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
import cn.yue.base.R
import cn.yue.base.utils.Utils.getContext
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.device.NotificationUtils
import cn.yue.base.utils.device.NotificationUtils.hasPermission
import cn.yue.base.utils.device.NotificationUtils.initChannelConfig

object NotificationConfig {

    private const val CHANNEL_ID = "YUE_CHANNEL"
    private const val CHANNEL_LOW_ID = "YUE_CHANNEL_LOW"

    fun initChannel() {
        val channelBuilder = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName(R.string.app_notification.getString())
            .build()
        initChannelConfig(channelBuilder)
        val lowChannelBuilder = NotificationChannelCompat.Builder(
            CHANNEL_LOW_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName(R.string.app_notification.getString())
            .build()
        initChannelConfig(lowChannelBuilder)
    }

    fun notify(id: Int, title: String?, content: String?) {
        if (!hasPermission() || !NotificationUtils.areNotificationsEnabled()) {
            return
        }
        val notification = getNotificationBuilder()
            .setTitle(title)
            .setShowContent(content)
            .build()
        notify(id, notification)
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

    fun NotificationCompat.Builder.notify(id: Int) {
        if (ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val nmc = NotificationManagerCompat.from(getContext())
        nmc.notify(id, this.build())
    }

    fun NotificationCompat.Builder.setTitle(title: String?): NotificationCompat.Builder {
        return this
            //标题
            .setContentTitle(title)
            .setSmallIcon(R.drawable.app_icon_clear)
            //只会响动一次
            .setOnlyAlertOnce(true)
            //设置点击信息后自动清除通知
            .setAutoCancel(true)
    }

    fun NotificationCompat.Builder.setShowContent(
        content: String? = null,
        progress: Int? = null
    ): NotificationCompat.Builder {
        return if (progress != null) {
            if (progress >= 100) {
                this.setContentText(content)
                    .setProgress(0, 0, false)
            } else {
                this.setProgress(100, progress, false)
            }
        } else {
            //文本内容
            this.setContentText(content)
        }
    }

    fun NotificationCompat.Builder.setIntent(intent: Intent?): NotificationCompat.Builder {
        if (intent != null) {
            val pendingIntent =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getActivity(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            return this.setContentIntent(pendingIntent)
        }
        return this
    }

    fun NotificationCompat.Builder.setBroadcastIntent(intent: Intent?): NotificationCompat.Builder {
        if (intent != null) {
            val pendingIntent =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getBroadcast(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            return this.setContentIntent(pendingIntent)
        }
        return this
    }

    fun getNotificationBuilder():NotificationCompat.Builder {
        return NotificationCompat.Builder(getContext(), CHANNEL_ID)
    }

    fun getLowNotificationBuilder():NotificationCompat.Builder {
        return NotificationCompat.Builder(getContext(), CHANNEL_LOW_ID)
    }

    fun getNotification(
        title: String?,
        content: String? = null,
        progress: Int? = null,
        intent: Intent? = null
    )
            : NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(getContext(), CHANNEL_ID)
            //标题
            .setContentTitle(title)
            //小图标
            .setSmallIcon(R.drawable.app_icon_clear)
            .setOnlyAlertOnce(true)
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
            val pendingIntent =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getActivity(
                        getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            builder.setContentIntent(pendingIntent)
        }

        return builder
    }
}