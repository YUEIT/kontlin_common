/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.yue.base.middle.net.convert

import cn.yue.base.common.utils.constant.EncryptUtils
import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.net.CharsetConfig
import cn.yue.base.middle.net.netLog
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import java.io.IOException
import java.util.*

/**
 * request请求 body参数解析
 * Created by yue on 2018/7/25.
 */
internal class SignGsonRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>)
    : Converter<T, RequestBody> {

    //body 里的内容直接修改为统一的参数；并且拦截器会判断直接使用该requestBody
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        "  origin :  $value".netLog()
        val encodeData = gson.toJson(value)
        val content = gson.toJson(getBody(encodeData))
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
}
