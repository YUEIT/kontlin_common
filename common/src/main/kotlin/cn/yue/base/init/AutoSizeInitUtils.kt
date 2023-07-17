package cn.yue.base.init

import android.app.Activity
import android.app.Application
import cn.yue.base.utils.device.ScreenUtils
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.DefaultAutoAdaptStrategy


/**
 * Description :
 * Created by yue on 2022/8/5
 */

object AutoSizeInitUtils {

    fun init(application: Application) {
        AutoSize.checkAndInit(application)
        AutoSizeConfig.getInstance()
            .setAutoAdaptStrategy(CustomAutoAdapterStrategy())
    }

    class CustomAutoAdapterStrategy : DefaultAutoAdaptStrategy() {

        override fun applyAdapt(target: Any?, activity: Activity?) {
            super.applyAdapt(target, activity)
            if ((ScreenUtils.screenWidth.toFloat() / ScreenUtils.screenHeight.toFloat()) > 0.75f) {
                AutoSize.cancelAdapt(activity)
            }
        }
    }
}