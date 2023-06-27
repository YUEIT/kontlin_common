package cn.yue.base.middle.net.download

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import cn.yue.base.common.Constant
import cn.yue.base.common.utils.code.getString
import cn.yue.base.middle.R
import cn.yue.base.middle.net.RetrofitManager
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.*


/**
 * Description :
 * Created by yue on 2020/12/10
 */
class DownloadWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        val request = Request.Builder().url(url).get().build()
        val mCall = RetrofitManager.instance.getOkHttpClient().newCall(request)
        try {
            val response = mCall.execute()
            if (response.isSuccessful) {
                val body = response.body
                if (body == null) {
                    val workerData = workDataOf("error" to R.string.app_request_data_fail.getString())
                    return Result.failure(workerData)
                } else {
                    return saveFile(body)
                }
            } else {
                val workerData = workDataOf("error" to R.string.app_request_data_fail.getString())
                return Result.failure(workerData)
            }
        } catch (e : IOException) {
            val workerData = workDataOf("error" to e.message)
            return Result.failure(workerData)
        }
    }

    private fun saveFile(body: ResponseBody): Result {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            // 改成自己需要的存储位置
            val fileName = inputData.getString("fileName") ?: ("temp" + System.currentTimeMillis())
            val file = File(Constant.cachePath + File.separator.toString() + fileName)
            if (file.exists()) {
                file.delete()
            }
            val fileReader = ByteArray(4096)
            val fileSize: Long = body.contentLength()
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            outputStream = FileOutputStream(file)
            while (true) {
                val read: Int = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()

                //计算当前下载百分比，并经由回调传出
                val progress = (100 * fileSizeDownloaded / fileSize).toInt()
                val workerData = workDataOf("progress" to progress)
                setProgressAsync(workerData)
            }
            outputStream.flush()
            val workerData = workDataOf("path" to file.path)
            return Result.success(workerData)
        } catch (e: IOException) {
            val workerData = workDataOf("error" to e.message)
            return Result.failure(workerData)
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }
}