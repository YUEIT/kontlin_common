package cn.yue.test.web

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.tencent.smtt.sdk.QbSdk

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class ProxyWebView(context: Context, attributeSet: AttributeSet? = null) :
	FrameLayout(context, attributeSet), IWebView {
	
	private var useX5WebView = false
	private var webView: IWebView? = null
	
	fun initView(useX5: Boolean = true) {
		useX5WebView = useX5
		val isInit = QbSdk.isTbsCoreInited()
		if (isInit && useX5WebView) {
			webView = CustomX5WebView(context)
			addView(webView as CustomX5WebView)
		} else {
			webView = CustomKitWebView(context)
			addView(webView as CustomKitWebView)
			useX5WebView = false
		}
	}
	
	fun isUseX5WebView(): Boolean {
		return useX5WebView
	}
	
	fun getWebView(): View? {
		return webView as View?
	}
	
	fun getSettings(): CustomWebSettings? {
		return webView?.getCustomWebSettings()
	}
	
	fun getWebChromeClient(): CustomWebChromeClient? {
		return webView?.getCustomWebChromeClient()
	}
	
	override fun setBackgroundColor(color: Int) {
		(webView as View).setBackgroundColor(color)
	}
	
	override fun setLayerType(layerType: Int, paint: Paint?) {
		(webView as View?)?.setLayerType(layerType, paint)
	}
	
	fun setWebViewClient(webViewClient: CustomWebViewClient) {
		webView?.setCustomWebViewClient(webViewClient)
	}
	
	fun setWebChromeClient(webChromeClient: CustomWebChromeClient) {
		webView?.setCustomWebChromeClient(webChromeClient)
	}
	
	override fun stopLoading() {
		webView?.stopLoading()
	}
	
	override fun getCustomWebSettings(): CustomWebSettings? {
		return webView?.getCustomWebSettings()
	}
	
	override fun setCustomWebViewClient(webViewClient: CustomWebViewClient?) {
		webView?.setCustomWebViewClient(webViewClient)
	}
	
	override fun getCustomWebViewClient(): CustomWebViewClient? {
		return webView?.getCustomWebViewClient()
	}
	
	override fun setCustomWebChromeClient(webChromeClient: CustomWebChromeClient?) {
		webView?.setCustomWebChromeClient(webChromeClient)
	}
	
	override fun getCustomWebChromeClient(): CustomWebChromeClient? {
		return webView?.getCustomWebChromeClient()
	}
	
	override fun reload() {
		webView?.reload()
	}
	
	override fun addJavascriptInterface(o: Any, s: String) {
		webView?.addJavascriptInterface(o, s)
	}
	
	override fun onResume() {
		webView?.onResume()
	}
	
	override fun clearHistory() {
		webView?.clearHistory()
	}
	
	override fun destroy() {
		webView?.destroy()
	}
	
	override fun onPause() {
		webView?.onPause()
	}
	
	override fun loadUrl(url: String) {
		webView?.loadUrl(url)
	}
	
	override fun loadUrl(url: String, extraHeaders: Map<String, String>) {
		webView?.loadUrl(url, extraHeaders)
	}
	
	override fun loadDataWithBaseURL(
		baseUrl: String?,
		data: String,
		mimeType: String?,
		encoding: String?,
		historyUrl: String?
	) {
		webView?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
	}
	
	override fun getUrl(): String {
		return webView?.getUrl() ?: ""
	}
	
	override fun canGoBack(): Boolean {
		return webView?.canGoBack() ?: false
	}
	
	override fun goBack() {
		webView?.goBack()
	}
	
	override fun setInitialScale(var1: Int) {
		webView?.setInitialScale(var1)
	}
	
	override fun invokeJavascript(str: String, block: ((any: String) -> Unit)?) {
		webView?.invokeJavascript(str, block)
	}
	
	override fun setWebViewHorizontalScrollBarEnabled(var1: Boolean) {
		webView?.setWebViewHorizontalScrollBarEnabled(var1)
	}
	
	override fun setWebViewVerticalScrollBarEnabled(var1: Boolean) {
		webView?.setWebViewVerticalScrollBarEnabled(var1)
	}
	
	fun onDestroy() {
		(webView as View?)?.visibility = View.GONE
		webView?.destroy()
	}
	
	/**
	 * 是否禁用滑动按钮
	 * @param var1
	 */
	fun setScrollBarEnabled(var1: Boolean) {
		//水平
		webView?.setWebViewHorizontalScrollBarEnabled(var1)
		//垂直
		webView?.setWebViewVerticalScrollBarEnabled(var1)
	}
	
	override fun computeWebViewVerticalScrollOffset(): Int {
		return webView?.computeWebViewVerticalScrollOffset() ?: 0
	}
	
	override fun flingScroll(vx: Int, vy: Int) {
		webView?.flingScroll(vx, vy)
	}
}