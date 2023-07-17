package cn.yue.base.activity

import androidx.fragment.app.Fragment
import cn.yue.base.activity.TransitionAnimation.getStopEnterAnim
import cn.yue.base.activity.TransitionAnimation.getStopExitAnim
import cn.yue.base.router.RouterCard
import cn.yue.base.utils.debug.LogUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Description :
 * Created by yue on 2019/3/11
 */
open class CommonActivity : BaseFragmentActivity() {
    private var transition = 0 //入场动画

    override fun getFragment(): Fragment? {
        val routerCard = intent?.extras?.getParcelable<RouterCard>(RouterCard.TAG)?: return null
        transition = routerCard.getTransition()
        return try {
            ARouter.getInstance()
                    .build(routerCard.getPath())
                    .with(intent.extras)
                    .setTimeout(routerCard.getTimeout())
//                        .withTransition(fRouter.getEnterAnim(), fRouter.getExitAnim())
                    .navigation(this, object : NavigationCallback {
                        override fun onFound(postcard: Postcard) {}
                        override fun onLost(postcard: Postcard) {
                            //showError();
                            LogUtils.e("no find page $postcard")
                        }
                        override fun onArrival(postcard: Postcard) {}
                        override fun onInterrupt(postcard: Postcard) {}
                    }) as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setExitAnim() {
        overridePendingTransition(getStopEnterAnim(transition), getStopExitAnim(transition))
    }
}