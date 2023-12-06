package cn.yue.base.net.observer

import cn.yue.base.R
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.upload.ImageResult
import cn.yue.base.net.upload.ImageResultListData
import cn.yue.base.utils.code.getString
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
            is CancellationException ->  onException(ResultException(ResponseCode.ERROR_CANCEL, e.message!!))
            else -> onException(ResultException(ResponseCode.ERROR_OPERATION, e.message!!))
        }
    }
    
    abstract fun onException(e: ResultException)

    abstract fun onSuccess(imageList: List<ImageResult>)
    
}