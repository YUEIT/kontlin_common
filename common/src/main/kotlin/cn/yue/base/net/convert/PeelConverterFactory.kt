package cn.yue.base.net.convert

import cn.yue.base.R
import cn.yue.base.utils.code.getString
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.wrapper.BaseBean
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Description : 参数解析
 * Created by yue on 2018/7/24
 */
class PeelConverterFactory : Converter.Factory() {

    private val json = Gson()

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>?, retrofit: Retrofit?)
            : Converter<ResponseBody, *>? {
        return PeelResponseBodyConverter<Any>(json, type)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit)
            : Converter<*, RequestBody> {
        val adapter = json.getAdapter(TypeToken.get(type) as  TypeToken<Any>)
        return GsonRequestBodyConverter<Any>(json, adapter)
    }

    companion object {
        fun create(): PeelConverterFactory {
            return PeelConverterFactory()
        }
    }

    internal class PeelResponseBodyConverter<T>(private val json: Gson, private val type: Type)
        : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.string()
            try {
                val baseBeanType = `$Gson$Types`.newParameterizedTypeWithOwner(null, BaseBean::class.java, type)
                var baseBean: BaseBean<T>? = null
                if (!response.startsWith("[") && !response.endsWith("]")) {
                    baseBean = json.fromJson<BaseBean<T>>(response, baseBeanType)
                }
                if (baseBean?.code == null) {
                    //不是BaseBean结构
                    val realData = json.fromJson<T>(response, type)
                    if (type == Any::class.java) {
                        if (null == realData) {
                            return Any() as T
                        }
                    } else {
                        //空数据返回异常
                        if (realData == null) {
                            throw ResultException(ResponseCode.ERROR_NO_DATA, R.string.app_server_response_empty.getString())
                        }
                        if (realData is List<*> && (realData as List<*>).isEmpty()) {
                            throw ResultException(ResponseCode.ERROR_NO_DATA, R.string.app_server_response_empty.getString())
                        }
                    }
                    return realData
                } else {
                    if (ResponseCode.SUCCESS_FLAG == baseBean.code) {
                        if (baseBean.data != null) {
                            return baseBean.data!!
                        }
                    }
                    throw ResultException(baseBean.code!!, baseBean.message ?: "")
                }
            } catch (e : JsonSyntaxException) {
                e.printStackTrace()
                throw ResultException(ResponseCode.ERROR_SERVER, "${R.string.app_convert_data_fail.getString()}：${e.message}")
            } catch (e : IllegalStateException) {
                e.printStackTrace()
                throw ResultException(ResponseCode.ERROR_SERVER, "${e.message}")
            }
        }

    }
}
