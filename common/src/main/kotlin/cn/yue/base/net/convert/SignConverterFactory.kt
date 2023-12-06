package cn.yue.base.net.convert

import cn.yue.base.R
import cn.yue.base.init.InitConstant
import cn.yue.base.net.CharsetConfig
import cn.yue.base.utils.code.getString
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.netLog
import cn.yue.base.net.wrapper.BaseBean
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

/**
 * Description : 验签方式factory
 * Created by yue on 2018/7/24
 */
class SignConverterFactory : Converter.Factory() {

    private val json = Gson()

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>?, retrofit: Retrofit?)
            : Converter<ResponseBody, *>? {
        return SignResponseBodyConverter<Any>(json, type)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        val adapter = json.getAdapter<Any>(TypeToken.get(type) as TypeToken<Any>)
        return SignRequestBodyConverter(json, adapter)
    }

    override fun stringConverter(type: Type, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, String>? {
        return SignRequestStringConverter<Any>()
    }

    companion object {
        fun create(): SignConverterFactory {
            return SignConverterFactory()
        }
    }

    internal class SignRequestBodyConverter<T>(private val json: Gson, private val adapter: TypeAdapter<T>)
        : Converter<T, RequestBody> {

        //body 里的内容直接修改为统一的参数；并且拦截器会判断直接使用该requestBody
        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            "  origin :  $value".netLog()
            val encodeData = json.toJson(value)
            val content = json.toJson(getBody(encodeData))
            return content.toRequestBody(CharsetConfig.CONTENT_TYPE)
        }

        private fun getBody(encodeData: String): Map<String, Any> {
            val tmp = HashMap<String, Any>()
            val time = System.currentTimeMillis() / 1000
            try {
                //      if(isDataEncode){
                //        encodeData = URLEncoder.encode(encodeData.toString(), "utf-8");
                //        encodeData = encodeData.replaceAll("\\+", "%20");
                //
                //      }
                val appVersion = InitConstant.getVersionName()
                val deviceId = InitConstant.getDeviceId()
                val sign = ""
//            val sign = EncryptUtils.encryptMD5ToString((appVersion + InitConstant.APP_CLIENT_TYPE
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
    }

    class SignRequestStringConverter<T> : Converter<T, String> {

        private val json = GsonBuilder().disableHtmlEscaping().create()

        //@Query, @QueryMap 会走这里；识别如果不是基本类型；那么为数据类型并构建Bean，传入ClassName和Json，用于拦截器解析
        @Throws(IOException::class)
        override fun convert(t: T): String {
            if (t is Number || t is String || t is Boolean) {
                return t.toString()
            } else {
                val clazz = t as Object
                val bean = RequestConverterEntity(clazz.`class`.name, json.toJson(t))
                return json.toJson(bean)
            }
        }
    }

    internal class SignResponseBodyConverter<T>(private val json: Gson, private val type: Type)
        : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.string()
            val baseBeanType = `$Gson$Types`.newParameterizedTypeWithOwner(null, BaseBean::class.java, type)
            val (message, code, data) = json.fromJson<BaseBean<T>>(response, baseBeanType)
            if (ResponseCode.SUCCESS_FLAG == code) {
                //表示成功返回，继续用本来的Model类解析
                //不在乎服务器的返回值 传入Object ，则此时 data不判空
                if (type !== Any::class.java) {
                    //空数据返回异常
                    if (data == null) {
                        throw ResultException(ResponseCode.ERROR_NO_DATA, R.string.app_server_response_empty.getString())
                    }
                    if (data is List<*> && (data as List<*>).isEmpty()) {
                        throw ResultException(ResponseCode.ERROR_NO_DATA, R.string.app_server_response_empty.getString())
                    }
                } else if (null == data) {
                    return Any() as T
                }
                //剥离无用字段
                return data
            } else if (null == code) {
                //不是BaseBean结构
                return json.fromJson<T>(response, type)
            } else {
                throw ResultException(code, message?:"")
            }

        }

    }
}