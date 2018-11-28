package cn.yue.base.kotlin.test


import cn.yue.base.middle.net.RetrofitManager

/**
 * Created by yue on 2018/7/16.
 */

object ArticleRetrofit {
    val purchaseSnsService = RetrofitManager.instance.getRetrofit("http://yjj-sns-test.imcome.net").create(SnsService::class.java)

}
