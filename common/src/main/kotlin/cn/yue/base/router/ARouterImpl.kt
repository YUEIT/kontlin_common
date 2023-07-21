package cn.yue.base.router

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import cn.yue.base.activity.BaseFragment
import cn.yue.base.activity.CommonActivity
import cn.yue.base.activity.WrapperResultLauncher
import cn.yue.base.common.R
import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.mvvm.data.RouterModel
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Description :
 * Created by yue on 2023/7/21
 */
class ARouterImpl: INavigation() {
	
	private lateinit var mRouterCard: RouterCard
	
	override fun bindRouterCard(routerCard: RouterCard): INavigation {
		mRouterCard = routerCard
		return this
	}
	
	private fun getRouteType(): RouteType {
		if (TextUtils.isEmpty(mRouterCard.getPath())) {
			return RouteType.UNKNOWN
		}
		val postcard = ARouter.getInstance().build(mRouterCard.getPath())
		try {
			LogisticsCenter.completion(postcard)
		} catch (e: NoRouteFoundException) {
			return RouteType.UNKNOWN
		}
		return postcard.type
	}
	
	override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
		val realContext: Context
		when (context) {
			is Context -> {
				realContext = context
			}
			is BaseFragment -> {
				realContext = context.mActivity
			}
			is BaseViewModel -> {
				context.navigation(RouterModel(mRouterCard, requestCode, toActivity))
				return
			}
			is WrapperResultLauncher -> {
				launch(context, toActivity)
				return
			}
			else -> {
				return
			}
		}
		when (getRouteType()) {
			RouteType.ACTIVITY -> {
				jumpToActivity(realContext, requestCode)
			}
			RouteType.FRAGMENT -> {
				jumpToFragment(realContext, toActivity, requestCode)
			}
			else -> {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			}
		}
	}
	
	private fun jumpToActivity(context: Context, requestCode: Int) {
		val postcard = ARouter.getInstance()
			.build(mRouterCard.getPath())
			.withFlags(mRouterCard.getFlags())
			.with(mRouterCard.getExtras())
			.withTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
			.setTimeout(mRouterCard.getTimeout())
		if (requestCode <= 0 || context !is Activity) {
			postcard.navigation(context)
		} else {
			postcard.navigation(context, requestCode)
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun jumpToFragment(context: Context, toActivity: String? = null, requestCode: Int) {
		val intent = Intent()
		intent.putExtra(RouterCard.TAG, mRouterCard)
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		if (toActivity == null) {
			intent.setClass(context, CommonActivity::class.java)
		} else {
			intent.setClassName(context, toActivity)
		}
		if (requestCode <= 0 || context !is Activity) {
			context.startActivity(intent)
		} else {
			context.startActivityForResult(intent, requestCode)
		}
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
	
	private fun launch(
		launcher: WrapperResultLauncher,
		toActivity: String?
	) {
		val realContext = when (launcher.context) {
			is Context -> {
				launcher.context
			}
			is BaseFragment -> {
				launcher.context.mActivity
			}
			else -> {
				return
			}
		}
		when (getRouteType()) {
			RouteType.ACTIVITY -> {
				launchToActivity(realContext, launcher)
			}
			RouteType.FRAGMENT -> {
				launchToFragment(realContext, toActivity, launcher)
			}
			else -> {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			}
		}
	}
	
	private fun getDestination(): String {
		if (TextUtils.isEmpty(mRouterCard.getPath())) {
			return ""
		}
		val postcard = ARouter.getInstance().build(mRouterCard.getPath())
		try {
			LogisticsCenter.completion(postcard)
		} catch (e: NoRouteFoundException) {
			return ""
		}
		return postcard.destination.name
	}
	
	@SuppressLint("WrongConstant")
	private fun launchToActivity(context: Context, launcher: WrapperResultLauncher) {
		val className = getDestination()
		if (className.isEmpty()) {
			ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			return
		}
		val intent = Intent()
		intent.setClassName(context, className)
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		launcher.launcher.launch(intent)
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun launchToFragment(context: Context, toActivity: String? = null, launcher: WrapperResultLauncher) {
		val intent = Intent()
		intent.putExtra(RouterCard.TAG, mRouterCard)
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		if (toActivity == null) {
			intent.setClass(context, CommonActivity::class.java)
		} else {
			intent.setClassName(context, toActivity)
		}
		launcher.launcher.launch(intent)
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
}