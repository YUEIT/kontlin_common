package cn.yue.base.middle.net.observer

import cn.yue.base.middle.net.NetworkConfig
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.upload.ImageResult
import cn.yue.base.middle.net.upload.ImageResultListData
import io.reactivex.observers.DisposableSingleObserver
import java.util.concurrent.CancellationException

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseUploadObserver : DisposableSingleObserver<ImageResultListData>() {

    override fun onSuccess(t: ImageResultListData) {
        if (t.data == null || t.data!!.isEmpty()) {
            onException(ResultException(NetworkConfig.ERROR_SERVER, "上传失败"))
            return
        }
        onSuccess(t.data!!)
    }

    override fun onError(e: Throwable) {
        when (e) {
            is ResultException -> onException(e)
            is CancellationException ->  onCancel(ResultException(NetworkConfig.ERROR_CANCEL, e.message!!))
            else -> onException(ResultException(NetworkConfig.ERROR_OPERATION, e.message!!))
        }
    }
    
    abstract fun onException(e: ResultException)

    abstract fun onSuccess(imageList: List<ImageResult>)

    open fun onCancel(e: ResultException) {}
}