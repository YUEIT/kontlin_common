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

import com.google.gson.GsonBuilder
import retrofit2.Converter
import java.io.IOException

/**
 * request请求参数 解析
 * Created by yue on 2018/7/25.
 */

class SignRequestStringConverter<T> : Converter<T, String> {

    //@Query, @QueryMap 会走这里；识别如果不是基本类型；那么为数据类型并构建Bean，传入ClassName和Json，用于拦截器解析
    @Throws(IOException::class)
    override fun convert(t: T): String {
        if (t is Number || t is String || t is Boolean) {
            return t.toString()
        } else {
            val clazz = t as Object
            val bean = RequestConverterData(clazz.`class`.name, gson.toJson(t))
            return gson.toJson(bean)
        }
    }

    companion object {
        private val gson = GsonBuilder().disableHtmlEscaping().create()
    }

    inline fun <reified K> className() = K::class.simpleName
}
