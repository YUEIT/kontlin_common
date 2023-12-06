package cn.yue.base.net.observer

import cn.yue.base.R
import cn.yue.base.event.NotifyViewModel
import cn.yue.base.mvp.IStatusView
import cn.yue.base.mvvm.data.LoaderLiveData
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.CancellationException

/**
 * Description :
 * Created by yue on 2023/5/9
 */
open class WrapperObserver<T>(
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val errorBlock: ((e: Throwable) -> Unit)? = null
): SingleObserver<T> {

	override fun onSubscribe(d: Disposable) {
		startBlock?.invoke(d)
	}

	override fun onSuccess(t: T) {
		successBlock?.invoke(t)
	}

	override fun onError(e: Throwable) {
		errorBlock?.invoke(e)
	}
}

open class WrapperNetObserver<T>(
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, null) {

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

			}
			else -> {
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}

	private fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())
		NotifyViewModel.getLoadStatus().setValue(-1)
	}

}

class WrapperPullObserver<T>(
	private val iStatusView: IStatusView?,
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, null) {

	override fun onSuccess(t: T) {
		iStatusView?.changePageStatus(PageStatus.NORMAL)
		iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
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
			else -> {
				iStatusView?.changePageStatus(PageStatus.ERROR)
				ToastUtils.showShortToast(e.message)
			}
		}
		exceptionBlock?.invoke(e)
		iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
	}

	private fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())
		NotifyViewModel.getLoadStatus().setValue(-1)
	}

}

open class WrapperPageObserver<T>(
	private val loader: LoaderLiveData,
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, null) {

	private var isLoadingRefresh = false
	override fun onSubscribe(d: Disposable) {
		super.onSubscribe(d)
		isLoadingRefresh = (loader.pageStatus == PageStatus.REFRESH || loader.loadStatus == LoadStatus.REFRESH)
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
				exceptionBlock?.invoke(e)
				loadFailed(e)
			}
			is CancellationException -> {

			}
			else -> {
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}

	private fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())
		NotifyViewModel.getLoadStatus().setValue(-1)
	}


	override fun onSuccess(t: T) {
		if (t == null) {
			if (isLoadingRefresh) {
				loadEmpty()
			} else {
				loadNoMore()
			}
		} else {
			loader.pageStatus = PageStatus.NORMAL
			loader.loadStatus = LoadStatus.NORMAL
		}
		successBlock?.invoke(t)
	}

	open fun loadFailed(e: ResultException) {
		if (loader.isFirstLoad) {
			when(e.code) {
				ResponseCode.ERROR_NO_NET -> {
					loader.pageStatus = PageStatus.NO_NET
				}
				else -> {
					loader.pageStatus = PageStatus.ERROR
					ToastUtils.showShortToast(e.message)
				}
			}
		} else {
			when(e.code) {
				ResponseCode.ERROR_NO_NET -> {
					loader.loadStatus = LoadStatus.NO_NET
				}
				else -> {
					loader.loadStatus = LoadStatus.NORMAL
					ToastUtils.showShortToast(e.message)
				}
			}
		}
	}

	open fun loadNoMore() {
		loader.loadStatus = LoadStatus.END
	}

	open fun loadEmpty() {
		loader.pageStatus = PageStatus.NO_DATA
		loader.loadStatus = LoadStatus.NORMAL
	}
}
