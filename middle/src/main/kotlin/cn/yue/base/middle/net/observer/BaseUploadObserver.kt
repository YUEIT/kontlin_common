package cn.yue.base.middle.net.observer

import cn.yue.base.common.utils.code.getString
import cn.yue.base.middle.R
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.upload.ImageResult
import cn.yue.base.middle.net.upload.ImageResultListData
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import java.util.concurrent.CancellationException

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseUploadObserver : DisposableSingleObserver<ImageResultListData>() {

    override fun onSuccess(t: ImageResultListData) {
        if (t.data == null || t.data!!.isEmpty()) {
            onException(ResultException(ResponseCode.ERROR_SERVER, R.string.app_upload_fail.getString()))
            return
        }
        onSuccess(t.data!!)
    }

    override fun onError(e: Throwable) {
        when (e) {
            is ResultException -> onException(e)
            is CancellationException ->  onCancel(ResultException(ResponseCode.ERROR_CANCEL, e.message!!))
            else -> onException(ResultException(ResponseCode.ERROR_OPERATION, e.message!!))
        }
    }
    
    abstract fun onException(e: ResultException)

    abstract fun onSuccess(imageList: List<ImageResult>)

    open fun onCancel(e: ResultException) {}
}