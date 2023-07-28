package cn.yue.base.utils.code

import android.util.Log
import java.net.URLDecoder

/**
 * Description :
 * Created by yue on 2023/7/27
 */
object UrlUtils {
	
	fun getParamsFromUrl(url: String?): Parameters {
		var parameters: Parameters? = null
		if (url != null && url.indexOf('?') > -1) {
			val tempUrl = url.substring(url.indexOf('?') + 1)
			val indexSharp = tempUrl.indexOf('#')
			if (indexSharp > -1) {
				parameters = splitUrlQuery(tempUrl.substring(0, indexSharp))
			} else {
				parameters = splitUrlQuery(tempUrl)
			}
		}
		if (parameters == null) {
			parameters = Parameters()
		}
		return parameters
	}
	
	private fun splitUrlQuery(query: String): Parameters {
		val parameters = Parameters()
		try {
			val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }
				.toTypedArray()
			if (pairs.isNotEmpty()) {
				for (pair in pairs) {
					val param = pair.split("=".toRegex(), limit = 2).toTypedArray()
					if (param.size == 2) {
						parameters.addParameter(
							URLDecoder.decode(param[0]), URLDecoder.decode(
								param[1]
							)
						)
					}
				}
			}
		} catch (e: Exception) {
			Log.e("UrlUtils", e.message!!)
		}
		return parameters
	}
	
	class Parameters {
		private val paramHashValues = LinkedHashMap<String, MutableMap<Int, String>>()
		private var limit = -1
		private var parameterCount = 0
		private var parameterIndex = 0
		
		@Throws(IllegalStateException::class)
		fun addParameter(key: String?, value: String?) {
			if (key == null) {
				return
			}
			parameterCount++
			check(!(limit > -1 && parameterCount > limit)) { "parameters.maxCountFail: $limit" }
			var values = paramHashValues[key]
			if (values == null) {
				values = LinkedHashMap(1)
				paramHashValues[key] = values
			}
			values[parameterIndex++] = value ?: ""
		}
		
		fun getParameterValues(name: String): Array<String>? {
			val values = paramHashValues[name] ?: return null
			return values.values.toTypedArray()
		}
		
		fun getParameter(name: String): String {
			val values: Map<Int, Any>? = paramHashValues[name]
			return if (values != null) {
				if (values.isEmpty()) {
					return ""
				}
				val value = values.values.iterator().next().toString()
				if (value != null && "null" != value) value else ""
			} else {
				""
			}
		}
		
		val parameterNames: Set<String>
			get() = paramHashValues.keys
	}
	
}