package cn.yue.test.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import cn.yue.base.init.NotificationConfig
import cn.yue.base.net.download.DownloadApkWorker
import cn.yue.base.net.download.DownloadUtils
import cn.yue.base.utils.app.ActivityUtils

/**
 * Description :
 * Created by yue on 2020/12/10
 */
class UpdateService : Service() {

    override fun onBind(intent: Intent?): IBinder {
        return downInfo
    }

    var downInfo: DownloadInfo = DownloadInfo()

    class DownloadInfo : Binder() {

        var updateBlock: ((progress: Int, isFail: Boolean) -> Unit)? = null

        fun setUpdateListener(updateBlock: ((progress: Int, isFail: Boolean) -> Unit)?) {
            this.updateBlock = updateBlock
        }
    }

    private var workInfoLiveData: LiveData<WorkInfo>? = null
    private var observer = lazy {
        Observer<WorkInfo> { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.RUNNING -> {
                    val progress = workInfo.progress.getInt("progress", 0)
                    downInfo.updateBlock?.invoke(progress, false)
                }
                WorkInfo.State.SUCCEEDED -> {
                    downInfo.updateBlock?.invoke(100, false)
                    val intent = ActivityUtils.installIntent(this, workInfo.outputData.getString("path"))
                    startActivity(intent)
                }
                WorkInfo.State.FAILED -> {
                    downInfo.updateBlock?.invoke(0, true)
                }
                else -> {}
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(DownloadApkWorker.NOTIFICATION_ID,
            NotificationConfig.getNotification("应用下载", "", null).build())
        workInfoLiveData = DownloadUtils.downloadApk(
            "https://downpack.baidu.com/baidusearch_AndroidPhone_1027757i.apk",
//            "https://kuxiu-1257191655.cos.ap-shanghai.myqcloud.com/apks/kx_v383_01.apk",
            "hehe.apk")
        workInfoLiveData?.observeForever(observer.value)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        workInfoLiveData?.removeObserver(observer.value)
    }
}