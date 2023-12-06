package cn.yue.base.net.download

import android.content.Context
import androidx.work.*
import cn.yue.base.Constant
import cn.yue.base.R
import cn.yue.base.net.RetrofitManager
import cn.yue.base.utils.code.getString
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile


/**
 * Description :
 * Created by yue on 2020/12/10
 */
open class DownloadWorker(context: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(context, workerParameters) {
    
    override suspend fun doWork(): Result {
        val fileName = inputData.getString("fileName")
        val file = File(Constant.cachePath + File.separator.toString() + fileName)
        val downloadLength = if (file.exists()) file.length() else 0
        val url = inputData.getString("url") ?: return Result.failure()
        val request = Request.Builder()
            .addHeader("RANGE", "bytes=${downloadLength}-")
            .url(url).get().build()
        // 这里的okhttpClient 不能使用HttpLoggingInterceptor
        // 不然会阻塞execute执行，导致将所有内容全部提取后才完成执行
        val mCall = RetrofitManager.instance.getOkHttpClient().newCall(request)
        try {
            val response = mCall.execute()
            return if (response.isSuccessful) {
                val body = response.body
                if (body == null) {
                    val workerData = workDataOf("error" to R.string.app_request_data_fail.getString())
                    updateWorkState(WorkInfo.State.FAILED, workerData)
                    Result.failure(workerData)
                } else {
                    saveFile(body)
                }
            } else {
                if (response.code == 416) {
                    //416 说明下载范围超出了文件的大小
                    val workerData = workDataOf("path" to file.path)
                    updateWorkState(WorkInfo.State.SUCCEEDED, workerData)
                    Result.success(workerData)
                } else {
                    val workerData = workDataOf("error" to response.message)
                    updateWorkState(WorkInfo.State.FAILED, workerData)
                    Result.failure(workerData)
                }
            }
        } catch (e : IOException) {
            val workerData = workDataOf("error" to e.message)
            updateWorkState(WorkInfo.State.FAILED, workerData)
            return Result.failure(workerData)
        }
    }
    
    private fun saveFile(body: ResponseBody): Result {
        var inputStream: InputStream? = null
        var savedFile: RandomAccessFile? = null
        try {
            // 改成自己需要的存储位置
            val fileName = inputData.getString("fileName")
            val file = File(Constant.cachePath + File.separator.toString() + fileName)
            val downloadedLength = if (file.exists()) file.length() else 0
            val fileSize: Long = body.contentLength()
            if (fileSize == 0L){
                //若文件长度等于0，说明文件有问题
                return Result.failure(workDataOf("error" to "file error"))
            } else if(fileSize == downloadedLength){
                //已下载的字节和文件总字节相等，说明已经下载完成了
                return Result.success(workDataOf("path" to file.path))
            }
            savedFile = RandomAccessFile(file, "rw")
            //跳过已经下载的字节
            savedFile.seek(downloadedLength)
            var fileSizeDownloaded: Long = 0
            inputStream = body.byteStream()
            var lastProgress = 0
            val fileReader = ByteArray(4096)
            while (true) {
                val read: Int = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                savedFile.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
                //计算当前下载百分比，并经由回调传出
                val progress = (100 * (fileSizeDownloaded + downloadedLength) / (fileSize + downloadedLength)).toInt()
                if (lastProgress < progress) {
                    val workerData = workDataOf("progress" to progress)
                    setProgressAsync(workerData)
                    updateWorkState(WorkInfo.State.RUNNING, workerData)
                    lastProgress = progress
                }
            }
            val workerData = workDataOf("path" to file.path)
            updateWorkState(WorkInfo.State.SUCCEEDED, workerData)
            return Result.success(workerData)
        } catch (e: IOException) {
            val workerData = workDataOf("error" to e.message)
            updateWorkState(WorkInfo.State.FAILED, workerData)
            return Result.failure(workerData)
        } finally {
            inputStream?.close()
            savedFile?.close()
        }
    }
    
    open fun updateWorkState(state: WorkInfo.State, data: Data) { }
}