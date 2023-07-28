package cn.yue.test.web

/**
 * Description :
 * Created by yue on 2023/7/26
 */
interface IWebView {
	
	fun loadUrl(url: String)
	
	fun loadUrl(url: String, extraHeaders: Map<String, String>)
	
	fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, historyUrl: String?)
	
	fun getUrl(): String
	
	fun reload()
	
	fun addJavascriptInterface(o: Any, s: String)
	
	fun canGoBack(): Boolean
	
	fun goBack()
	
	fun stopLoading()
	
	fun getCustomWebSettings(): CustomWebSettings?
	
	fun setCustomWebViewClient(webViewClient: CustomWebViewClient?)
	
	fun getCustomWebViewClient(): CustomWebViewClient?
	
	fun setCustomWebChromeClient(webChromeClient: CustomWebChromeClient?)
	
	fun getCustomWebChromeClient(): CustomWebChromeClient?
	
	fun setInitialScale(var1: Int)
	
	fun invokeJavascript(str: String, block: ((any: String) -> Unit)?)
	
	fun setWebViewHorizontalScrollBarEnabled(var1: Boolean)
	
	fun setWebViewVerticalScrollBarEnabled(var1: Boolean)
	
	fun computeWebViewVerticalScrollOffset(): Int
	
	fun flingScroll(vx: Int, vy: Int)
	
	fun clearHistory()
	
	fun onResume()
	
	fun onPause()
	
	fun destroy()
}