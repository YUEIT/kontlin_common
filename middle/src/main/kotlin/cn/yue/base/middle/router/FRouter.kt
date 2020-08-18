package cn.yue.base.middle.router

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.fragment.app.DialogFragment
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.middle.activity.CommonActivity
import cn.yue.base.middle.router.RouterCard
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Description : 路由
 * Created by yue on 2019/3/11
 */
class FRouter() : INavigation, Parcelable {

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
            throw NullPointerException("path is null")
        }
        val postcard = ARouter.getInstance().build(mRouterCard.getPath())
        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            return RouteType.UNKNOWN
        }
        return postcard.type
    }

    override fun bindRouterCard(routerCard: RouterCard): INavigation? {
        this.mRouterCard = routerCard
        this.mRouterCard.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Context) {
        this.navigation(context, null)
    }

    override fun navigation(context: Context, toActivity: String?) {
        if (mRouterCard.isInterceptLogin() && interceptLogin(context!!)) {
            return
        }
        when (getRouteType()) {
            RouteType.ACTIVITY -> {
                jumpToActivity(context)
            }
            RouteType.FRAGMENT -> {
                jumpToFragment(context, toActivity)
            }
            else -> {
                showShortToast("找不到页面~")
            }
        }
    }

    override fun navigation(context: Activity, requestCode: Int) {
        this.navigation(context, null, requestCode)
    }

    override fun navigation(context: Activity, toActivity: String?, requestCode: Int) {
        if (mRouterCard.isInterceptLogin() && interceptLogin(context)) {
            return
        }
        when (getRouteType()) {
            RouteType.ACTIVITY -> {
                jumpToActivity(context, requestCode)
            }
            RouteType.FRAGMENT -> {
                jumpToFragment(context, toActivity, requestCode)
            }
            else -> {
                showShortToast("找不到页面~")
            }
        }
    }

    private fun jumpToActivity(context: Context, requestCode: Int = -1) {
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

    private fun jumpToFragment(context: Context, toActivity: String? = null, requestCode: Int = -1) {
        val intent = Intent()
        intent.putExtra(TAG, this)
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
        if (requestCode <= 0) {
            context.startActivity(intent)
        } else {
            if (context is Activity) {
                context.startActivityForResult(intent, requestCode)
            }
        }
        if (context is Activity) {
            context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
        }
    }

    fun navigationDialogFragment(context: BaseFragmentActivity): DialogFragment {
        val dialogFragment = ARouter.getInstance()
                .build(mRouterCard.getPath())
                .with(mRouterCard.getExtras())
                .navigation(context) as DialogFragment
        dialogFragment.show(context.supportFragmentManager, null)
        return dialogFragment
    }

    private var onInterceptLoginListener: ((content: Context?) -> Boolean)? = null

    fun setOnInterceptLoginListener(onInterceptLoginListener: ((content: Context?) -> Boolean)?) {
        this.onInterceptLoginListener = onInterceptLoginListener
    }

    private fun interceptLogin(context: Context): Boolean {
        return onInterceptLoginListener!!(context)
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
                fRouter.mRouterCard.clear()
                return fRouter
            }

        @JvmField
        val CREATOR: Parcelable.Creator<FRouter> = object : Parcelable.Creator<FRouter> {
            override fun createFromParcel(source: Parcel): FRouter = FRouter(source)
            override fun newArray(size: Int): Array<FRouter?> = arrayOfNulls(size)
        }
    }
}