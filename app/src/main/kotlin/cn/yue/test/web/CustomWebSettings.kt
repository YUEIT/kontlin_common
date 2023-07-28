package cn.yue.test.web

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class CustomWebSettings(private val kitWebSettings: android.webkit.WebSettings?,
                        private val x5WebSettings: com.tencent.smtt.sdk.WebSettings?) {
	
	fun setTextZoom(zoom: Int) {
		kitWebSettings?.textZoom = zoom
		x5WebSettings?.textZoom = zoom
	}
	
	fun setJavaScriptEnabled(b: Boolean) {
		kitWebSettings?.javaScriptEnabled = b
		x5WebSettings?.javaScriptEnabled = b
	}
	
	fun setCacheMode(cacheMode: Int) {
		kitWebSettings?.cacheMode = cacheMode
		x5WebSettings?.cacheMode = cacheMode
	}
	
	fun setDomStorageEnabled(b: Boolean) {
		kitWebSettings?.domStorageEnabled = b
		x5WebSettings?.domStorageEnabled = b
	}
	
	fun setAppCacheEnabled(b: Boolean) {
		x5WebSettings?.setAppCacheEnabled(b)
	}
	
	fun setAppCachePath(b: String?) {
		x5WebSettings?.setAppCachePath(b)
	}
	
	fun setAppCacheMaxSize(maxSize: Long) {
		x5WebSettings?.setAppCacheMaxSize(maxSize)
	}
	
	fun getUserAgentString(): String? {
		return if (kitWebSettings == null) {
			x5WebSettings?.userAgentString
		} else {
			kitWebSettings.userAgentString
		}
	}
	
	fun setUserAgentString(s: String?) {
		kitWebSettings?.userAgentString = s
		x5WebSettings?.userAgentString = s
	}
	
	fun setUseWideViewPort(b: Boolean) {
		kitWebSettings?.useWideViewPort = b
		x5WebSettings?.useWideViewPort = b
	}
	
	fun setLoadWithOverviewMode(b: Boolean) {
		kitWebSettings?.loadWithOverviewMode = b
		x5WebSettings?.loadWithOverviewMode = b
	}
	
	fun setMediaPlaybackRequiresUserGesture(b: Boolean) {
		kitWebSettings?.mediaPlaybackRequiresUserGesture = b
		x5WebSettings?.mediaPlaybackRequiresUserGesture = b
	}
}