package cn.yue.base.utils.device

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.RunTimePermissionUtil.checkPermissions

object NotificationUtils {

    /**
     * Return whether the notifications enabled.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(Utils.getContext()).areNotificationsEnabled()
    }

    fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Utils.getContext().checkPermissions(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }
    
    /**
     * Post a notification to be shown in the status bar.
     *
     * @param id            An identifier for this notification.
     * @param channelConfig The notification channel of config.
     * @param consumer      The consumer of create the builder of notification.
     */
    fun notify(id: Int, channelConfig: NotificationChannelCompat,
               consumer: (builder: NotificationCompat.Builder?) -> Unit) {
        notify(null, id, channelConfig, consumer)
    }

    /**
     * Post a notification to be shown in the status bar.
     *
     * @param tag           A string identifier for this notification.  May be `null`.
     * @param id            An identifier for this notification.
     * @param channelConfig The notification channel of config.
     * @param consumer      The consumer of create the builder of notification.
     */
    @SuppressLint("MissingPermission")
    fun notify(tag: String?, id: Int, channelConfig: NotificationChannelCompat,
               consumer: (builder: NotificationCompat.Builder?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !Utils.getContext().checkPermissions(Manifest.permission.POST_NOTIFICATIONS)) {
            return
        }
        if (!areNotificationsEnabled()) {
            return
        }
        initChannelConfig(channelConfig)
        val nmc = NotificationManagerCompat.from(Utils.getContext())
        val builder = NotificationCompat.Builder(Utils.getContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelConfig.id)
        }
        consumer.invoke(builder)
        nmc.notify(tag, id, builder.build())
    }

    @JvmStatic
    fun initChannelConfig(channelConfig: NotificationChannelCompat) {
        NotificationManagerCompat.from(Utils.getContext()).createNotificationChannel(channelConfig)
    }

    /**
     * Cancel The notification.
     *
     * @param tag The tag for the notification will be cancelled.
     * @param id  The identifier for the notification will be cancelled.
     */
    fun cancel(tag: String?, id: Int) {
        NotificationManagerCompat.from(Utils.getContext()).cancel(tag, id)
    }

    /**
     * Cancel The notification.
     *
     * @param id The identifier for the notification will be cancelled.
     */
    fun cancel(id: Int) {
        NotificationManagerCompat.from(Utils.getContext()).cancel(id)
    }

    /**
     * Cancel all of the notifications.
     */
    fun cancelAll() {
        NotificationManagerCompat.from(Utils.getContext()).cancelAll()
    }

    /**
     * Set the notification bar's visibility.
     *
     * Must hold `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />`
     *
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    @RequiresPermission(permission.EXPAND_STATUS_BAR)
    fun setNotificationBarVisibility(isVisible: Boolean) {
        val methodName: String = if (isVisible) {
            "expandNotificationsPanel"
        } else {
            "collapsePanels"
        }
        invokePanels(methodName)
    }

    private fun invokePanels(methodName: String) {
        try {
            @SuppressLint("WrongConstant") val service: Any = Utils.getContext().getSystemService("statusbar")
            @SuppressLint("PrivateApi") val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod(methodName)
            expand.invoke(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}