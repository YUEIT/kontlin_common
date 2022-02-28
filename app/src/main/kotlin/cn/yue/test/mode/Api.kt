package cn.yue.test.mode

import cn.yue.base.middle.init.BaseUrlAddress
import cn.yue.base.middle.net.RetrofitManager
import io.reactivex.Single
import retrofit2.http.GET

interface Api {

    @GET("/user/all")
    suspend fun getAllData(): List<ItemBean>

}

interface RxApi {

    @GET("/user/all")
    fun getAllData(): Single<List<ItemBean>>

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