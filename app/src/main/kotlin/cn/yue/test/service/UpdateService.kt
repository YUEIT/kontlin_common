package cn.yue.test.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import cn.yue.base.middle.init.NotificationConfig
import cn.yue.base.middle.net.download.DownloadUtils
import java.io.File

/**
 * Description :
 * Created by yue on 2020/12/10
 */
class UpdateService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
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
                    notification("已下载" , progress, null)
                    downInfo.updateBlock?.invoke(progress, false)
                }
                WorkInfo.State.SUCCEEDED -> {
                    downInfo.updateBlock?.invoke(100, false)
                    val intent = installIntent(this, workInfo.outputData.getString("path"))
                    notification("下载完成", 100, intent)
                    startActivity(intent)
                }
                WorkInfo.State.FAILED -> {
                    downInfo.updateBlock?.invoke(0, true)
                    notification("下载失败 " + workInfo.outputData.getString("error"), 100, null)
                }
            }
        }
    }

    private fun notification(content: String, progress: Int?, intent: Intent?) {
        NotificationConfig.notify(10, "应用下载", content, progress, intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(10, NotificationConfig.getNotification("应用下载", "准备下载", null).build())
        workInfoLiveData = DownloadUtils.downFileByLiveData("https://downpack.baidu.com/litebaiduboxapp_AndroidPhone_1020164i.apk", "hehe.apk")
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

    private fun installIntent(context: Context, downloadApk: String?): Intent? {
        if (TextUtils.isEmpty(downloadApk)) return null
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(downloadApk!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //适配Android Q,注意mFilePath是通过ContentResolver得到的，上述有相关代码
            val contentUri = FileProvider.getUriForFile(context, "cn.yue.base.kotlin.test.fileprovider", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else { //7.0以下
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}