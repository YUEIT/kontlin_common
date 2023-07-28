package cn.yue.test.checker

import androidx.annotation.Keep
import java.lang.reflect.Modifier

/**
 * Description :
 * Created by yue on 2023/7/24
 */
object XposedNative {
	
	init {
		try {
			System.loadLibrary("xposed_native")
		} catch (e: UnsatisfiedLinkError) {
			e.printStackTrace()
		}
	}
	
	external fun auth(): Boolean
	
	fun invokeMethod(className: String?, methodName: String?, args: Array<Any>?): Any? {
		try {
			val clazz = Class.forName(className)
			val any = clazz.newInstance()
			val result: Any
			if (args == null || args.isEmpty()) {
				val method = clazz.getDeclaredMethod(methodName)
				method.isAccessible = true
				result = method.invoke(any)
			} else {
				val parameterTypes: Array<Class<*>?> = arrayOfNulls(args.size)
				for (i in args.indices) {
					val param = args[i]
					parameterTypes[i] = param.javaClass
				}
				val method = clazz.getDeclaredMethod(methodName, *parameterTypes)
				method.isAccessible = true
				result = method.invoke(any, *args)
			}
			return result
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return null
	}
	
	@Keep
	fun testClassLoader(param: String?): Boolean {
		return try {
			Class.forName(param)
			true
		} catch (e: ClassNotFoundException) {
			e.message == null
		}
	}
	
	@Keep
	fun testException(params: String): Boolean {
		return try {
			throw Exception()
		} catch (e: Exception) {
			val arrayOfStackTraceElement = e.stackTrace
			for (s in arrayOfStackTraceElement) {
				if (s.className != null) {
					for (param in params.split(";".toRegex()).dropLastWhile { it.isEmpty() }
						.toTypedArray()) {
						if (s.className.contains(param!!)) {
							return true
						}
					}
				}
			}
			false
		}
	}
	
	@Keep
	fun testStackTrace(param: String?): Boolean {
		try {
			val method = Throwable::class.java.getDeclaredMethod(param)
			return Modifier.isNative(method.modifiers)
		} catch (e: NoSuchMethodException) {
			e.printStackTrace()
		}
		return false
	}
}