package cn.yue.base.kotlin.test

import android.content.Context
import android.util.Log
import cn.yue.base.middle.module.IAppModule

/**
 * Description :
 * Created by yue on 2020/8/17
 */
class AppModuleService : IAppModule{
    override fun test(str: String) {
        Log.d("luobiao", "test: " + str)
    }

    override fun init(context: Context) {

    }
}