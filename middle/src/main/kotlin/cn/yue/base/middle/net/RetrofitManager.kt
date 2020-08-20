package cn.yue.base.middle.net


import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.convert.SignGsonConverterFactory
import cn.yue.base.middle.net.intercept.NoNetInterceptor
import cn.yue.base.middle.net.intercept.SignInterceptor
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by yue on 2018/7/6.
 */

class RetrofitManager private constructor() {
    private val builder: OkHttpClient.Builder

    init {
        val responseInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val proceed = chain.proceed(request)
                val code = proceed.code
                if (code != 200 && code != 404) {
                    return proceed.newBuilder()
                            .code(200)
                            .build()
                }
                return proceed
            }
        }
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (InitConstant.isDebug) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        builder.addInterceptor(NoNetInterceptor())
                .addInterceptor(SignInterceptor())
                .addInterceptor(responseInterceptor)
        builder.retryOnConnectionFailure(true)
        this.builder = builder
    }

    companion object {

        private const val DEFAULT_TIMEOUT = 60

        val instance = RetrofitManagerHolder.instance
    }

    private object RetrofitManagerHolder {
        val instance = RetrofitManager()
    }

    fun getRetrofit(baseUrl: String): Retrofit {
        handlerError()
        return Retrofit.Builder()
                .client(builder.build())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //注册自定义的工厂类
                .addConverterFactory(SignGsonConverterFactory.create())
                .build()
    }

    private fun handlerError() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable !is ResultException) {
                throw Exception(throwable)
            }
        }
    }
}
