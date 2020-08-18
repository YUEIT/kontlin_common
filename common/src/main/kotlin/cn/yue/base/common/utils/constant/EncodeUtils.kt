package cn.yue.base.common.utils.constant

import android.os.Build
import android.text.Html
import android.util.Base64

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * 介绍：编码解码相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object EncodeUtils {

    /**
     * URL编码
     *
     * 若系统不支持指定的编码字符集,则直接将input原样返回
     *
     * @param input   要编码的字符
     * @param charset 字符集
     * @return 编码为字符集的字符串
     */
    @JvmStatic
    fun urlEncode(input: String, charset: String = "UTF-8"): String {
        try {
            return URLEncoder.encode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            return input
        }

    }

    /**
     * URL解码
     *
     * 若系统不支持指定的解码字符集,则直接将input原样返回
     *
     * @param input   要解码的字符串
     * @param charset 字符集
     * @return URL解码为指定字符集的字符串
     */
    @JvmStatic
    fun urlDecode(input: String, charset: String = "UTF-8"): String {
        try {
            return URLDecoder.decode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            return input
        }

    }

    /**
     * Base64编码
     *
     * @param input 要编码的字符串
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64Encode(input: String): ByteArray {
        return base64Encode(input.toByteArray())
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64Encode(input: ByteArray): ByteArray {
        return Base64.encode(input, Base64.NO_WRAP)
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    @JvmStatic
    fun base64Encode2String(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.NO_WRAP)
    }

    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    @JvmStatic
    fun base64Decode(input: String): ByteArray {
        return Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    @JvmStatic
    fun base64Decode(input: ByteArray): ByteArray {
        return Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Base64URL安全编码
     *
     * 将Base64中的URL非法字符�?,/=转为其他字符, 见RFC3548
     *
     * @param input 要Base64URL安全编码的字符串
     * @return Base64URL安全编码后的字符串
     */
    @JvmStatic
    fun base64UrlSafeEncode(input: String): ByteArray {
        return Base64.encode(input.toByteArray(), Base64.URL_SAFE)
    }

    /**
     * Html编码
     *
     * @param input 要Html编码的字符串
     * @return Html编码后的字符串
     */
    @JvmStatic
    fun htmlEncode(input: CharSequence): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Html.escapeHtml(input)
        } else {
            // 参照Html.escapeHtml()中代码
            val out = StringBuilder()
            var i = 0
            val len = input.length
            while (i < len) {
                val c = input[i]
                if (c == '<') {
                    out.append("&lt;")
                } else if (c == '>') {
                    out.append("&gt;")
                } else if (c == '&') {
                    out.append("&amp;")
                } else if (c.toInt() >= 0xD800 && c.toInt() <= 0xDFFF) {
                    if (c.toInt() < 0xDC00 && i + 1 < len) {
                        val d = input[i + 1]
                        if (d.toInt() >= 0xDC00 && d.toInt() <= 0xDFFF) {
                            i++
                            val codepoint = 0x010000 or (c.toInt() - 0xD800 shl 10) or d.toInt() - 0xDC00
                            out.append("&#").append(codepoint).append(";")
                        }
                    }
                } else if (c.toInt() > 0x7E || c < ' ') {
                    out.append("&#").append(c.toInt()).append(";")
                } else if (c == ' ') {
                    while (i + 1 < len && input[i + 1] == ' ') {
                        out.append("&nbsp;")
                        i++
                    }
                    out.append(' ')
                } else {
                    out.append(c)
                }
                i++
            }
            return out.toString()
        }
    }

    /**
     * Html解码
     *
     * @param input 待解码的字符串
     * @return Html解码后的字符串
     */
    @JvmStatic
    fun htmlDecode(input: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(input)
        }
    }

}
