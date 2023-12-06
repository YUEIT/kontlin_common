package cn.yue.base.net


import cn.yue.base.init.InitConstant
import cn.yue.base.net.convert.SplitConverterFactory
import cn.yue.base.net.intercept.NoNetInterceptor
import cn.yue.base.net.intercept.ParamInterceptor
import cn.yue.base.net.intercept.ResponseInterceptor
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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
        val sslParams = SslHttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!)
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
//        val sslParams = SslHttpsUtils.getSslSocketFactory()
//        baseBuilder.sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!)
//        if (InitConstant.isDebug()) {
//            val logging = HttpLoggingInterceptor()
//            logging.level = HttpLoggingInterceptor.Level.BODY
//            baseBuilder.addInterceptor(logging)
//        }
        baseBuilder.eventListenerFactory { HttpEventListener() }
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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
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
