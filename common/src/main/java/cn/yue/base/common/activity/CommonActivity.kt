package cn.yue.base.common.activity

import android.os.Parcelable
import android.support.v4.app.Fragment
import cn.yue.base.common.utils.debug.LogUtils
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter


/**
 * Created by yue on 2018/6/4.
 */

class CommonActivity : BaseFragmentActivity() {

    private var transition: Int = 0
    override fun getFragment(): Fragment? {
        var fragment: Fragment? = null
        if (intent != null && intent.extras != null && intent.extras!!.getParcelable<Parcelable>(FRouter.TAG) != null) {
            val fRouter = intent.extras!!.getParcelable<FRouter>(FRouter.TAG)
            transition = fRouter.getTransition()
            try {
                fragment = ARouter.getInstance()
                        .build(fRouter!!.getPath())
                        .with(intent.extras)
                        .setTimeout(fRouter.getTimeout())
                        //.withTransition(fRouter.getEnterAnim(), fRouter.getExitAnim())
                        .navigation(this, object : NavigationCallback {
                            override fun onFound(postcard: Postcard) {

                            }

                            override fun onLost(postcard: Postcard) {
                                //showError();
                                LogUtils.e("no find page $postcard")
                            }

                            override fun onArrival(postcard: Postcard) {

                            }

                            override fun onInterrupt(postcard: Postcard) {

                            }
                        }) as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
        return fragment
    }

    override fun setExitAnim() {
        overridePendingTransition(TransitionAnimation.getStopEnterAnim(transition), TransitionAnimation.getStopExitAnim(transition))
    }
}