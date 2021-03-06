package cn.yue.base.middle.net


import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.convert.SplitConverterFactory
import cn.yue.base.middle.net.intercept.NoNetInterceptor
import cn.yue.base.middle.net.intercept.ParamInterceptor
import cn.yue.base.middle.net.intercept.ResponseInterceptor
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by yue on 2018/7/6.
 */

class RetrofitManager private constructor() {
    private val okHttpClient: OkHttpClient

    private val defaultClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (InitConstant.isDebug()) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        builder.addInterceptor(NoNetInterceptor())
//                .addInterceptor(SignInterceptor())
                .addInterceptor(ParamInterceptor())
                .addInterceptor(ResponseInterceptor())
        builder.retryOnConnectionFailure(true)
        defaultClient = builder.build()

        val baseBuilder = OkHttpClient.Builder()
        baseBuilder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        baseBuilder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        baseBuilder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (InitConstant.isDebug()) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            baseBuilder.addInterceptor(logging)
        }
        baseBuilder.addInterceptor(NoNetInterceptor())
        baseBuilder.retryOnConnectionFailure(true)
        okHttpClient = baseBuilder.build()
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
                .client(defaultClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //注册自定义的工厂类
                .addConverterFactory(SplitConverterFactory.create())
                .build()
    }

    fun getCoroutineRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .client(defaultClient)
                .baseUrl(baseUrl)
                .addConverterFactory(SplitConverterFactory.create())
                .build()
    }

    fun getOkHttpClient(): OkHttpClient {
        return okHttpClient
    }

    private fun handlerError() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable !is ResultException) {
                throw Exception(throwable)
            }
        }
    }
}
