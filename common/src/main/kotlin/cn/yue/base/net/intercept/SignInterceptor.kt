package cn.yue.base.net.intercept

import android.text.TextUtils
import cn.yue.base.R
import cn.yue.base.init.InitConstant
import cn.yue.base.net.CharsetConfig
import cn.yue.base.net.ResponseCode
import cn.yue.base.utils.code.getString
import cn.yue.base.net.ResultException
import cn.yue.base.net.convert.RequestConverterEntity
import cn.yue.base.net.netLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.net.URLEncoder
import java.util.*


/**
 * Created by yue on 2018/7/10.
 */

class SignInterceptor : Interceptor {

    private var isDataEncode = false

    constructor()

    constructor(isDataEncode: Boolean) {
        this.isDataEncode = isDataEncode
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.method == "GET") {
            return chain.proceed(interceptGet(original))
        } else if (original.method == "POST") {
            //这里获取的是url中的查询参数，body里的参数暂时没有想到方法获取
            return chain.proceed(interceptPost(original))
        }
        return chain.proceed(original)
    }

    private fun interceptGet(original: Request): Request {
        val map = HashMap<String, Any?>()
        //获取传入的全部参数
        if (original.url.queryParameterNames.isNotEmpty()) {
            for (key in original.url.queryParameterNames) {
                for (o in original.url.queryParameterValues(key)) {
                    map[key] = o
                }
            }
        }
        "  origin :  $map".netLog()
        var encodeData = toJsonStr(map)
        val url: HttpUrl
        val time = System.currentTimeMillis() / 1000
        try {
            if (isDataEncode) {
                encodeData = URLEncoder.encode(encodeData, "utf-8")
                encodeData = encodeData.replace("\\+".toRegex(), "%20")
            }
            val appVersion = InitConstant.getVersionName()
            val deviceId = InitConstant.getDeviceId()
            val sign = ""
//            val sign = EncryptUtils.encryptMD5ToString((appVersion + InitConstant.APP_CLIENT_TYPE
//                    + encodeData + deviceId + time + InitConstant.APP_SIGN_KEY).toByteArray())

            url = original.url.newBuilder().query(null)
                    .addQueryParameter("data", encodeData)
                    .addQueryParameter("app_version", appVersion)
                    .addQueryParameter("client_type",  "1")
                    .addQueryParameter("device_id", deviceId)
                    .addQueryParameter("time", time.toString() + "")
                    .addQueryParameter("sign", sign)
                    .build()
            "  intercept : $url".netLog()
            return original.newBuilder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .build()
        } catch (e: Exception) {
            e.printStackTrace()
            return original
        }

    }

    @Throws(IOException::class)
    private fun interceptPost(original: Request): Request {
        if (original.body != null && original.body!!.contentLength() > 0) {
            //如果有body的情况，直接用body
            val url = original.url.newBuilder().query(null).build()
            return original.newBuilder().url(url).build()
        } else {
            //这里获取的是url中的查询参数，body里的参数暂时没有想到方法获取
            val map = HashMap<String, Any?>()
            //获取传入的全部参数
            if (original.url.queryParameterNames.isNotEmpty()) {
                for (key in original.url.queryParameterNames) {
                    //URL后面的参数，以[]的方式用来区分数组；
                    if (key.endsWith("[]")) {
                        //含有list的情况;
                        val list = ArrayList<Any?>()
                        for (value in original.url.queryParameterValues(key)) {
                            //如果能解析成功，说明是bean类型,转成Object ; 失败：基本类型直接add
                            try {
                                val entity: RequestConverterEntity = gson.fromJson(value, RequestConverterEntity::class.javaObjectType)
                                list.add(gson.fromJson(entity.json, Class.forName(entity.className)))
                            } catch (e: Exception) {
                                list.add(value)
                            }

                        }
                        map[key.replace("[]", "")] = list
                    } else {
                        if (original.url.queryParameterValues(key).size > 1) {
                            throw ResultException(ResponseCode.ERROR_OPERATION, R.string.app_request_list_params_error.getString())
                        }
                        val value = original.url.queryParameterValues(key)[0]
                        try {
                            val bean = gson.fromJson(value, RequestConverterEntity::class.javaObjectType)
                            map[key] = gson.fromJson(bean.json, Class.forName(bean.className))
                        } catch (e: Exception) {
                            map[key] = value
                        }

                    }
                }
            }
            // 获取 Body 参数
            var bodyString = ""
            if (original.body != null) {
                val buffer = Buffer()
                original.body!!.writeTo(buffer)
                bodyString = buffer.readUtf8()
            }
            if (isJsonObject(bodyString) || isJsonArray(bodyString)) {
                val bodyMap: TreeMap<String, String> = gson.fromJson(bodyString, object : TypeToken<TreeMap<String?, String?>?>() {}.type)
                for (key in bodyMap.keys) {
                    map[key] = bodyMap[key]
                }
            }
            "  origin :  $map".netLog()
            val url = original.url.newBuilder().query(null).build()
            "  intercept : $url  --------   body: ${gson.toJson(getBody(map))}".netLog()
            return original.newBuilder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .method("POST", gson.toJson(getBody(map)).toRequestBody(CharsetConfig.CONTENT_TYPE))
                    .build()
        }
    }

    private fun getBody(map: Map<String, Any?>): Map<String, Any> {
        var encodeData = toJsonStr(map)
        val tmp = HashMap<String, Any>()
        val time = System.currentTimeMillis() / 1000
        try {
            if (isDataEncode) {
                encodeData = URLEncoder.encode(encodeData, "utf-8")
                encodeData = encodeData.replace("\\+".toRegex(), "%20")

            }
            val appVersion = InitConstant.getVersionName()
            val deviceId = InitConstant.getDeviceId()
            val sign = ""
//            val sign = encryptMD5ToString((appVersion + InitConstant.APP_CLIENT_TYPE
//                    + encodeData + deviceId + time + InitConstant.APP_SIGN_KEY).toByteArray())
            tmp["app_version"] = appVersion.toString()
            tmp["client_type"] = "1"
            tmp["data"] = encodeData
            tmp["device_id"] = deviceId.toString()
            tmp["time"] = time.toString() + ""
            tmp["sign"] = sign.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tmp
    }

    private val gson = Gson()

    private fun toJsonStr(map: Map<String, Any?>?): String {
        var tamp = map
        if (tamp == null) {
            tamp = HashMap()
        }
        return gson.toJson(tamp)
    }

    private fun isJsonObject(content: String): Boolean {
        return !TextUtils.isEmpty(content) && content.trim { it <= ' ' }.startsWith("{")
                && content.trim { it <= ' ' }.endsWith("}")
    }

    private fun isJsonArray(content: String): Boolean {
        return !TextUtils.isEmpty(content) && content.trim { it <= ' ' }.startsWith("[")
                && content.trim { it <= ' ' }.endsWith("]")
    }
}
