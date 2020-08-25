package cn.yue.base.kotlin.test

import cn.yue.base.kotlin.test.mode.SlidesData
import cn.yue.base.kotlin.test.mode.UuidData
import cn.yue.base.middle.net.RetrofitManager
import retrofit2.http.GET

interface Api {

    @GET("/json")
    suspend fun getJson(): SlidesData

    @GET("/uuid")
    suspend fun getUuid(): UuidData
}

object ApiManager {

    fun getApi() : Api{
       return RetrofitManager.instance.getCoroutineRetrofit("https://httpbin.org").create(Api::class.java)
    }
}