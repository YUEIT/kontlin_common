package cn.yue.base.middle.net.convert

import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.wrapper.BaseBean
import com.google.gson.Gson
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
class SplitConverterFactory : Converter.Factory() {

    private val json = Gson()

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>?, retrofit: Retrofit?)
            : Converter<ResponseBody, *>? {
        return SplitResponseBodyConverter<Any>(json, type)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit)
            : Converter<*, RequestBody> {
        val adapter = json.getAdapter(TypeToken.get(type) as  TypeToken<Any>)
        return GsonRequestBodyConverter<Any>(json, adapter)
    }

    companion object {
        fun create(): SplitConverterFactory {
            return SplitConverterFactory()
        }
    }

    internal class SplitResponseBodyConverter<T>(private val json: Gson, private val type: Type)
        : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.string()
            LogUtils.i("服务器返回:$response")
            val baseBeanType = `$Gson$Types`.newParameterizedTypeWithOwner(null, BaseBean::class.java, type)
            val (message, code, data) = json.fromJson<BaseBean<T>>(response, baseBeanType)
            if (ResponseCode.SUCCESS_FLAG == code) {
                //表示成功返回，继续用本来的Model类解析
                //不在乎服务器的返回值 传入Object ，则此时 data不判空
                if (type !== Any::class.java) {
                    //空数据返回异常
                    if (data == null) {
                        throw ResultException(ResponseCode.ERROR_NO_DATA, "服务器返回数据为空")
                    }
                    if (data is List<*> && (data as List<*>).isEmpty()) {
                        throw ResultException(ResponseCode.ERROR_NO_DATA, "服务器返回数据为空")
                    }
                } else if (null == data) {
                    //Rxjava sucess complete 都不能传空数据 所以....
                    return Any() as T
                }
                //剥离无用字段
                return data
            } else if (null == code) {
                //不是BaseBean结构
                val json = json.fromJson<T>(response, type)
                LogUtils.d("TAG", "convert() called with: json = [$json]")
                return json
            } else {
                LogUtils.d(" error $code , $message")
                throw ResultException(code, message?:"")
            }

        }

    }
}