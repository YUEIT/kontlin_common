package cn.yue.test.web

import android.net.Uri
import android.os.Build

/**
 * Description :
 * Created by yue on 2023/7/27
 */
class CustomWebResourceRequest(
	private val kitWebResourceRequest: android.webkit.WebResourceRequest?,
	private val x5WebResourceRequest: com.tencent.smtt.export.external.interfaces.WebResourceRequest?,
){
	
	fun getUrl(): Uri? {
		return if (kitWebResourceRequest == null) {
			x5WebResourceRequest?.url
		} else {
			kitWebResourceRequest.url
		}
	}
	
	fun isForMainFrame(): Boolean {
		return kitWebResourceRequest?.isForMainFrame
			?: (x5WebResourceRequest?.isForMainFrame ?: false)
	}
	
	fun isRedirect(): Boolean {
		if (kitWebResourceRequest != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				return kitWebResourceRequest.isRedirect
			}
			return false
		} else {
			return x5WebResourceRequest?.isRedirect ?: false
		}
	}
	
	fun hasGesture(): Boolean {
		return kitWebResourceRequest?.hasGesture()
			?: (x5WebResourceRequest?.hasGesture() ?: false)
	}
	
	fun getMethod(): String? {
		return if (kitWebResourceRequest == null) {
			x5WebResourceRequest?.method
		} else {
			kitWebResourceRequest.method
		}
	}
	
	fun getRequestHeaders(): MutableMap<String, String>? {
		return if (kitWebResourceRequest == null) {
			x5WebResourceRequest?.requestHeaders
		} else {
			kitWebResourceRequest.requestHeaders
		}
	}
	
	
}