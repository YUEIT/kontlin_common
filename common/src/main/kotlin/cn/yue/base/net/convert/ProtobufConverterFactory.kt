package cn.yue.base.net.convert

import cn.yue.base.net.CharsetConfig
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.wrapper.BaseProtoDataOuterClass.BaseProtoData
import com.google.protobuf.MessageLite
import com.google.protobuf.MessageOrBuilder
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ProtobufConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>?, retrofit: Retrofit?)
            : Converter<ResponseBody, *> {
        return ProtobufResponseBodyConverter<Any>(type)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<*, RequestBody> {
        return ProtobufRequestBodyConverter<Any>()
    }

    override fun stringConverter(type: Type, annotations: Array<Annotation>?,
                                 retrofit: Retrofit?
    ): Converter<*, String> {
        return ProtobufRequestStringConverter<Any>()
    }

    companion object {
        fun create(): ProtobufConverterFactory {
            return ProtobufConverterFactory()
        }
    }

    internal class ProtobufResponseBodyConverter<T>(private val type: Type)
        : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val response = value.byteStream()
            val clazz = Class.forName((type as Class<*>).name)
            val methodParseFrom = clazz.getDeclaredMethod("parseFrom", InputStream::class.java)
            val invokeData = methodParseFrom.invoke(null, response)
            if (invokeData is MessageOrBuilder) {
                val descriptor = invokeData.descriptorForType
                val fieldDescriptor = descriptor.findFieldByNumber(1)
                val realData = invokeData.getField(fieldDescriptor)
                if (realData is BaseProtoData) {
                    val code = realData.code.toString()
                    val message = realData.message
                    if (ResponseCode.SUCCESS_FLAG == code) {
                        return invokeData as T
                    } else {
                        throw ResultException(code, message?:"")
                    }
                }
            }
            throw ResultException(ResponseCode.ERROR_SERVER, "")
        }

    }

    inner class ProtobufRequestBodyConverter<T>: Converter<T, RequestBody> {

        //body 里的内容直接修改为统一的参数；并且拦截器会判断直接使用该requestBody
        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            if (value is MessageLite) {
                return value.toByteArray().toRequestBody(CharsetConfig.CONTENT_TYPE)
            }
            throw ResultException(ResponseCode.ERROR_OPERATION, "request not protobuf")
        }

    }

    inner class ProtobufRequestStringConverter<T>: Converter<T, String> {


        @Throws(IOException::class)
        override fun convert(value: T): String {
            if (value is MessageLite) {
                return byteToHex(value.toByteArray())
            }
            throw ResultException(ResponseCode.ERROR_OPERATION, "request not protobuf")
        }

    }


    fun byteToHex(arr: ByteArray): String {
        val len = arr.size
        val sb = StringBuilder(len * 2)
        for (b in arr) {
            var intTemp: Int = b.toInt()
            while (intTemp < 0) {
                intTemp += 256
            }
            if (intTemp < 16) {
                sb.append("0")
            }
            sb.append(intTemp.toString(16))
        }
        return sb.toString()
    }

    fun toByteBuffer(str: String): ByteBuffer? {
        if ((str.isEmpty())) {
            return ByteBuffer.wrap(ByteArray(0))
        }
        val bytes = ByteArray(str.length / 2)
        for (i in 0 until str.length / 2) {
            val subStr = str.substring(i * 2, i * 2 + 2)
            bytes[i] = subStr.toInt(16).toByte()
        }
        val byteBuffer = ByteBuffer.wrap(bytes)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return byteBuffer
    }
}