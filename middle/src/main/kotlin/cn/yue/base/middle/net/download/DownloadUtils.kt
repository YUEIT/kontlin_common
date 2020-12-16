package cn.yue.base.middle.net.download

import androidx.lifecycle.LiveData
import androidx.work.*
import cn.yue.base.common.utils.Utils
import java.util.*


object DownloadUtils {

    fun downFileByLiveData(url: String, fileName: String?): LiveData<WorkInfo> {
        val downWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(workDataOf("url" to url, "fileName" to fileName))
                .build()
        WorkManager.getInstance(Utils.getContext())
                .enqueueUniqueWork("down", ExistingWorkPolicy.APPEND, downWorkRequest)
        return WorkManager.getInstance(Utils.getContext()).getWorkInfoByIdLiveData(downWorkRequest.id)
    }

    fun downFile(url: String, fileName: String?): UUID {
        val downWorkRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(workDataOf("url" to url, "fileName" to fileName))
                .build()
        WorkManager.getInstance(Utils.getContext())
                .enqueueUniqueWork("down", ExistingWorkPolicy.APPEND, downWorkRequest)
        return downWorkRequest.id
//        return WorkManager.getInstance(Utils.getContext()).getWorkInfoById(downWorkRequest.id)
    }
}