package cn.yue.base.net.download

import androidx.lifecycle.LiveData
import androidx.work.*
import cn.yue.base.mvvm.data.CaseLiveData
import cn.yue.base.utils.Utils
import java.util.*


object DownloadUtils {

    fun downloadFileByLiveData(url: String, fileName: String?): LiveData<WorkInfo> {
        val downWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(workDataOf("url" to url, "fileName" to fileName))
                .build()
        WorkManager.getInstance(Utils.getContext())
                .enqueueUniqueWork("download", ExistingWorkPolicy.APPEND, downWorkRequest)
        return WorkManager.getInstance(Utils.getContext()).getWorkInfoByIdLiveData(downWorkRequest.id)
    }

    fun downloadFile(url: String, fileName: String?): UUID {
        val downWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(workDataOf("url" to url, "fileName" to fileName))
                .build()
        WorkManager.getInstance(Utils.getContext())
                .enqueueUniqueWork("download", ExistingWorkPolicy.APPEND, downWorkRequest)
        return downWorkRequest.id
//        return WorkManager.getInstance(Utils.getContext()).getWorkInfoById(downWorkRequest.id)
    }
    
    fun downloadApk(url: String, fileName: String?): LiveData<WorkInfo> {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val downWorkRequest = OneTimeWorkRequestBuilder<DownloadApkWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("url" to url, "fileName" to fileName))
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(Utils.getContext())
            .enqueueUniqueWork("download_apk", ExistingWorkPolicy.KEEP, downWorkRequest)
        val liveData = WorkManager.getInstance(Utils.getContext())
            .getWorkInfosForUniqueWorkLiveData("download_apk")
        return CaseLiveData(liveData)
    }
    
}