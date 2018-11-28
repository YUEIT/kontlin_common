package cn.yue.base.middle.net.intercept

import cn.yue.base.common.utils.constant.EncryptUtils
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.NetworkConfig
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.gson.RequestConverterBean
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder
import java.util.*


/**
 * Created by yue on 2018/7/10.
 */

class SignInterceptor : Interceptor {

    private var isDataEncode = false

    constructor() {}

    constructor(isDataEncode: Boolean) {
        this.isDataEncode = isDataEncode
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val language = original.header("language")
        if (original.method() == "GET") {
            return chain.proceed(interceptGet(original))
        } else if (original.method() == "POST") {
            //这里获取的是url中的查询参数，body里的参数暂时没有想到方法获取
            return chain.proceed(interceptPost(original))
        }
        return chain.proceed(original)
    }

    private fun interceptGet(original: Request): Request {
        val map = HashMap<String, Any>()
        //获取传入的全部参数
        if (!original.url().queryParameterNames().isEmpty()) {
            for (key in original.url().queryParameterNames()) {
                for (o in original.url().queryParameterValues(key)) {
                    map[key] = o
                }
            }
        }
        LogUtils.i("okhttp", "  origin :  $map")
        var encodeData = toJsonStr(map)
        val url: HttpUrl
        val time = System.currentTimeMillis() / 1000
        try {
            if (isDataEncode) {
                encodeData = URLEncoder.encode(encodeData, "utf-8")
                encodeData = encodeData.replace("\\+".toRegex(), "%20")
            }
            val appVersion = InitConstant.versionName
            val deviceId = InitConstant.deviceId
            val sign = EncryptUtils.encryptMD5ToString((appVersion + InitConstant.APP_CLIENT_TYPE + encodeData +
                    deviceId + time + InitConstant.APP_SIGN_KEY).toByteArray())

            url = original.url().newBuilder().query(null)
                    .addQueryParameter("data", encodeData)
                    .addQueryParameter("app_version", appVersion)
                    .addQueryParameter("client_type", InitConstant.APP_CLIENT_TYPE + "")
                    .addQueryParameter("device_id", deviceId)
                    .addQueryParameter("time", time.toString() + "")
                    .addQueryParameter("sign", sign)
                    .build()
            LogUtils.i("okhttp", "  intercept : " + url.toString())
            val request = original.newBuilder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .build()
            return request
        } catch (e: Exception) {
            e.printStackTrace()
            return original
        }

    }

    @Throws(IOException::class)
    private fun interceptPost(original: Request): Request {
        if (original.body() != null && original.body()!!.contentLength() > 0) {
            //如果有body的情况，直接用body
            val url = original.url().newBuilder().query(null).build()
            return original.newBuilder().url(url).build()
        } else {
            //这里获取的是url中的查询参数，body里的参数暂时没有想到方法获取
            val map = HashMap<String, Any>()
            //获取传入的全部参数
            if (!original.url().queryParameterNames().isEmpty()) {
                for (key in original.url().queryParameterNames()) {
                    //URL后面的参数，暂时没想到如何识别list；比较挫的方式参数前加上"LIST_"用来区分；
                    if (key.startsWith("LIST_")) {
                        //含有list的情况;
                        val list = ArrayList<Any>()
                        for (value in original.url().queryParameterValues(key)) {
                            //如果能解析成功，说明是bean类型,转成Object ; 失败：基本类型直接add
                            try {
                                val bean: RequestConverterBean = gson.fromJson(value, RequestConverterBean::class.javaObjectType)
                                list.add(gson.fromJson(bean.json, Class.forName(bean.className)))
                            } catch (e: Exception) {
                                list.add(value)
                            }

                        }
                        map[key.replace("LIST_", "")] = list
                    } else {
                        if (original.url().queryParameterValues(key).size > 1) {
                            throw ResultException(NetworkConfig.ERROR_OTHER, "请求的数据类型为list，且参数名未以LIST_开始~")
                        }
                        val value = original.url().queryParameterValues(key)[0]
                        try {
                            val bean = gson.fromJson(value, RequestConverterBean::class.javaObjectType)
                            map[key] = gson.fromJson(bean.json, Class.forName(bean.className))
                        } catch (e: Exception) {
                            map[key] = value
                        }

                    }
                }
            }
            LogUtils.i("okhttp", "  origin :  $map")
            val url = original.url().newBuilder().query(null).build()
            LogUtils.i("okhttp", "  intercept : " + url.toString() + "  --------   body: " + gson.toJson(getBody(map)))
            val request = original.newBuilder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .method("POST", RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(getBody(map))))
                    .build()
            return request
        }
    }

    private fun getBody(map: Map<String, Any>): Map<String, Any> {
        var encodeData = toJsonStr(map)
        val tmp = HashMap<String, Any>()
        val time = System.currentTimeMillis() / 1000
        try {
            if (isDataEncode) {
                encodeData = URLEncoder.encode(encodeData, "utf-8")
                encodeData = encodeData.replace("\\+".toRegex(), "%20")

            }
            val appVersion = InitConstant.versionName
            val deviceId = InitConstant.deviceId
            val sign = EncryptUtils.encryptMD5ToString((appVersion + InitConstant.APP_CLIENT_TYPE + encodeData +
                    deviceId + time + InitConstant.APP_SIGN_KEY).toByteArray())
            tmp["app_version"] = appVersion.toString()
            tmp["client_type"] = InitConstant.APP_CLIENT_TYPE + ""
            tmp["data"] = encodeData
            tmp["device_id"] = deviceId.toString()
            tmp["time"] = time.toString() + ""
            tmp["sign"] = sign.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tmp
    }

    companion object {


        private val gson = Gson()
        private fun toJsonStr(map: Map<String, Any>?): String {
            var map = map
            if (map == null) {
                map = HashMap()
            }
            return gson.toJson(map)
        }
    }
}
