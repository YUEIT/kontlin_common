package cn.yue.base.middle.net.upload

import cn.yue.base.common.utils.file.BitmapFileUtils
import cn.yue.base.middle.init.BaseUrlAddress
import cn.yue.base.middle.net.RetrofitManager
import cn.yue.base.middle.net.observer.BaseUploadObserver
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/18
 */
object UploadUtils {
    private val uploadServer = RetrofitManager.instance.getRetrofit(BaseUrlAddress.getUpLoadUrl()).create(UploadServer::class.java)

    fun getUploadServer(): UploadServer {
        return uploadServer
    }

    fun <E> upload(files: List<String>, lifecycleProvider: LifecycleProvider<E>, uploadObserver: BaseUploadObserver) {
        UploadUtils.getCompressFileList(files)
                .subscribeOn(Schedulers.io())
                .flatMap { files ->
                    val url: String
//                    if (InitConstant.isDebug) {
//                        url = "4tpBNVAu7iPQgmQetUXvXA"
//                    } else {
                        url = getUploadKey()
//                    }
                    UploadUtils.getUploadServer().upload(url, filesToMultipartBodyParts(files))
                }
                .subscribeOn(Schedulers.newThread())
                .compose(lifecycleProvider.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadObserver)
    }

    private fun filesToMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>(files.size)
        for (file in files) {
            val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            parts.add(part)
        }
        return parts
    }

    fun getCompressFileList(list: List<String>): Single<List<File>> {
        return Single.just(list)
                .flatMap { strings ->
                    val files = ArrayList<File>()
                    for (url in strings) {
                        val file = BitmapFileUtils.getCompressBitmapFile(url)
                        if (file != null) {
                            files.add(file)
                        }
                    }
                    Single.just<List<File>>(files)
                }
    }

    private fun getUploadKey(): String {
        return "LKdCxyqv0tvAuyT2NJmelw"
    }

}