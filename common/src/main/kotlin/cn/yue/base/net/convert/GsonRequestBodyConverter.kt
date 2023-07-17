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
package cn.yue.base.net.convert

import cn.yue.base.net.CharsetConfig
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonWriter
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer


/**
 * request请求 body参数解析
 * Created by yue on 2018/7/25.
 */
internal class GsonRequestBodyConverter<T>(private val json: Gson, private val adapter: TypeAdapter<T>)
    : Converter<T, RequestBody> {

    //body 里的内容直接修改为统一的参数；并且拦截器会判断直接使用该requestBody
    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), CharsetConfig.ENCODING)
        val jsonWriter: JsonWriter = json.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        return buffer.readByteArray().toRequestBody(CharsetConfig.CONTENT_TYPE)
    }

}
