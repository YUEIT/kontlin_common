package cn.yue.base.middle.net


import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.convert.SignGsonConverterFactory
import cn.yue.base.middle.net.intercept.NoNetInterceptor
import cn.yue.base.middle.net.intercept.SignInterceptor
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.Interceptor
import okhttp3.OkHttpClient
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
        val responseInterceptor = Interceptor { chain ->
            val request = chain.request()
            val proceed = chain.proceed(request)
            val code = proceed.code()
            if (code != 200 && code != 404) {
                val adapterResponse = proceed.newBuilder()
                        .code(200)
                        .build()
                return@Interceptor adapterResponse
            }
            proceed
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

        private val DEFAULT_TIMEOUT = 60

        val instance: RetrofitManager
            get() = RetrofitManagerHolder.instance
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


    // 查看CallObservable的subscribeActual方法可知，一般情况下异常会被“observer.onError(t);”中处理
    // 但是如果是onError中抛出的异常，会调用RxJavaPlugins.onError方法，所有这里实现Consumer<Throwable>接口，并让异常在accept中处理
    // 考虑到ResultException是自定义异常，并不能让APP闪退，这里拦截，如果是其他异常直接抛出。
    private fun handlerError() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable !is ResultException) {
                throw Exception(throwable)
            }
        }
    }
}
