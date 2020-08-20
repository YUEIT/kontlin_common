package cn.yue.base.middle.net

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.nio.charset.Charset

object CharsetConfig {

    val CONTENT_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    val ENCODING: Charset = Charset.forName("UTF-8")
}