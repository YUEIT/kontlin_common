package cn.yue.base.middle.router

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.middle.R
import cn.yue.base.middle.activity.CommonActivity
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.data.RouterModel
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Description : 路由
 * Created by yue on 2019/3/11
 */
class FRouter() : INavigation(), Parcelable {

    private var mRouterCard = RouterCard(this)

    private object FRouterHolder {
        val instance = FRouter()
    }

    fun getRouterCard(): RouterCard {
        return mRouterCard
    }

    fun build(path: String?): RouterCard {
        mRouterCard.setPath(path)
        return mRouterCard
    }

    private var targetActivity: Class<*>? = null

    fun setTargetActivity(targetActivity: Class<*>?) {
        this.targetActivity = targetActivity
    }

    fun getRouteType(): RouteType {
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

    override fun bindRouterCard(routerCard: RouterCard): INavigation {
        this.mRouterCard = routerCard
        this.mRouterCard.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        val realContext: Context
        when (context) {
            is Activity -> {
                realContext = context
            }
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
            else -> {
                return
            }
        }
        if (mRouterCard.isInterceptLogin() && interceptLogin(realContext)) {
            return
        }
        when (getRouteType()) {
            RouteType.ACTIVITY -> {
                jumpToActivity(realContext, requestCode)
            }
            RouteType.FRAGMENT -> {
                jumpToFragment(realContext, toActivity, requestCode)
            }
            else -> {
                showShortToast(R.string.app_find_not_page.getString())
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
            if (targetActivity == null) {
                intent.setClass(context, CommonActivity::class.java)
            } else {
                intent.setClass(context, targetActivity!!)
            }
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

    private var onInterceptLoginListener: ((content: Context?) -> Boolean)? = null

    fun setOnInterceptLoginListener(onInterceptLoginListener: ((content: Context?) -> Boolean)?) {
        this.onInterceptLoginListener = onInterceptLoginListener
    }

    private fun interceptLogin(context: Context): Boolean {
        onInterceptLoginListener?.let {
            return it.invoke(context)
        }
        return false
    }

    constructor(source: Parcel) : this() {
        mRouterCard = source.readParcelable(RouterCard::class.java.classLoader)?: RouterCard()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int){
        dest.writeParcelable(mRouterCard, flags)
    }

    companion object {
        const val TAG = "FRouter"

        fun init(application: Application?) {
            debug()
            ARouter.init(application)
        }

        //必须写在init之前，否则这些配置在init过程中将无效
        fun debug() {
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }

        @JvmStatic
        val instance: FRouter
            get() {
                val fRouter = FRouterHolder.instance
                fRouter.mRouterCard = RouterCard(fRouter)
                return fRouter
            }

        @JvmField
        val CREATOR: Parcelable.Creator<FRouter> = object : Parcelable.Creator<FRouter> {
            override fun createFromParcel(source: Parcel): FRouter = FRouter(source)
            override fun newArray(size: Int): Array<FRouter?> = arrayOfNulls(size)
        }
    }
}