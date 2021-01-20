package cn.yue.base.kotlin.test.mode

import cn.yue.base.middle.init.BaseUrlAddress
import cn.yue.base.middle.net.RetrofitManager
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {

    @GET("/user/all")
    suspend fun getAllUser(): List<UserBean>

    @POST("/account/login")
    suspend fun login(@Body body: MutableMap<String, String>): TokenBean

    @GET("/account/getUserInfo")
    suspend fun getUserInfo(): UserBean

}

interface RxApi {

    @GET("/user/all")
    fun getAllUser(): Single<List<UserBean>>

}

object ApiManager {

    private var api: Api? = null

    fun getApi() : Api {
        if (api == null) {
            api = RetrofitManager.instance.getCoroutineRetrofit(BaseUrlAddress.baseUrl).create(Api::class.java)
        }
        return api!!
    }

    private var rxApi: RxApi? = null

    fun getRxApi() : RxApi {
        if (rxApi == null) {
            rxApi = RetrofitManager.instance.getRetrofit(BaseUrlAddress.baseUrl).create(RxApi::class.java)
        }
        return rxApi!!
    }
}