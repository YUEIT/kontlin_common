package cn.yue.base.router

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Description : 路由
 * Created by yue on 2019/3/11
 */
class FRouter() : INavigation(), Parcelable {

    private var mRouterCard = RouterCard(this)
    
    private val aRouterImpl = ARouterImpl()
    
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

    override fun bindRouterCard(routerCard: RouterCard): INavigation {
        this.mRouterCard = routerCard
        this.mRouterCard.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        aRouterImpl.bindRouterCard(mRouterCard)
        aRouterImpl.navigation(context, requestCode, toActivity)
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