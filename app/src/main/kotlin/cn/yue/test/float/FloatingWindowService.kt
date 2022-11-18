package cn.yue.test.float

import FloatWindowManager
import android.app.Service
import android.content.Intent
import android.os.IBinder


/**
 * Description :
 * Created by yue on 2022/7/15
 */

class FloatingWindowService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatWindow()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        closeFloatWindow()
        super.onDestroy()
    }

    private val floatWindowManager by lazy { FloatWindowManager() }

    private fun showFloatWindow() {
        if (floatWindowManager.requestPermission(baseContext)) {
            floatWindowManager.showFloatWindow(baseContext)
        } else {
            stopSelf()
        }
    }

    private fun closeFloatWindow() {
        floatWindowManager.closeFloatWindow()
    }
}