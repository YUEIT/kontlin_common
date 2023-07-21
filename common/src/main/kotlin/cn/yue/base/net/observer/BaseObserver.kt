package cn.yue.base.net.observer

import cn.yue.base.common.R
import cn.yue.base.event.AppViewModes
import cn.yue.base.mvp.IStatusView
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import java.util.concurrent.CancellationException

/**
 * Description :
 * Created by yue on 2023/5/9
 */
open class WrapperObserver<T>(
	private val startBlock: (() -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val errorBlock: ((e: Throwable) -> Unit)? = null
): DisposableSingleObserver<T>() {
	
	public override fun onStart() {
		super.onStart()
		startBlock?.invoke()
	}
	
	override fun onSuccess(t: T) {
		successBlock?.invoke(t)
	}
	
	override fun onError(e: Throwable) {
		errorBlock?.invoke(e)
	}
}

open class WrapperNetObserver<T>(
	private val startBlock: (() -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
	private val errorBlock: ((e: Throwable) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, errorBlock) {
	
	override fun onError(e: Throwable) {
		super.onError(e)
		when (e) {
			is ResultException -> {
				if (e.code == ResponseCode.ERROR_TOKEN_INVALID
					|| e.code == ResponseCode.ERROR_LOGIN_INVALID) {
					onLoginInvalid()
					return
				}
				exceptionBlock?.invoke(e)
			}
			is CancellationException -> {
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_CANCEL, R.string.app_request_cancel.getString()))
			}
			else -> {
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}
	
	fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())
		AppViewModes.getNotifyViewModel().loginStatusLiveData.setValue(-1)
	}
	
}

class WrapperPullObserver<T>(
	private val iStatusView: IStatusView?,
	private val startBlock: (() -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
	private val errorBlock: ((e: Throwable) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, errorBlock) {
	
	override fun onSuccess(t: T) {
		iStatusView?.changePageStatus(PageStatus.NORMAL)
		successBlock?.invoke(t)
	}
	
	override fun onError(e: Throwable) {
		super.onError(e)
		when (e) {
			is ResultException -> {
				if (e.code == ResponseCode.ERROR_TOKEN_INVALID
					|| e.code == ResponseCode.ERROR_LOGIN_INVALID) {
					onLoginInvalid()
					return
				}
				onException(e)
			}
			is CancellationException -> {
				onException(ResultException(ResponseCode.ERROR_CANCEL, R.string.app_request_cancel.getString()))
			}
			else -> {
				onException(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}
	
	fun onException(e: ResultException) {
		when(e.code) {
			ResponseCode.ERROR_NO_NET -> {
				iStatusView?.changePageStatus(PageStatus.NO_NET)
			}
			ResponseCode.ERROR_NO_DATA -> {
				iStatusView?.changePageStatus(PageStatus.NO_DATA)
			}
			ResponseCode.ERROR_OPERATION -> {
				iStatusView?.changePageStatus(PageStatus.ERROR)
				ToastUtils.showShortToast(e.message)
			}
			ResponseCode.ERROR_CANCEL -> {
				iStatusView?.changePageStatus(PageStatus.NORMAL)
			}
			else -> {
				iStatusView?.changePageStatus(PageStatus.ERROR)
				ToastUtils.showShortToast(e.message)
			}
		}
		exceptionBlock?.invoke(e)
	}
	
	fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())
		AppViewModes.getNotifyViewModel().loginStatusLiveData.setValue(-1)
	}
	
}
