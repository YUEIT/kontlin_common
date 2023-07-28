package cn.yue.test.web

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.AttributeSet
import android.webkit.*

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class CustomKitWebView(context: Context, attributeSet: AttributeSet? = null)
	: WebView(context, attributeSet), IWebView {
	
	private var customWebSettings: CustomWebSettings? = null
	private var customWebViewClient: CustomWebViewClient? = null
	private var customWebChromeClient: CustomWebChromeClient? = null
	
	override fun getUrl(): String {
		return originalUrl ?: ""
	}
	
	override fun getCustomWebSettings(): CustomWebSettings? {
		if (customWebSettings == null) {
			customWebSettings = CustomWebSettings(settings, null)
		}
		return customWebSettings
	}
	
	override fun setCustomWebViewClient(webViewClient: CustomWebViewClient?) {
		this.customWebViewClient = webViewClient
		if (webViewClient != null) {
			setWebViewClient(object : WebViewClient() {
				override fun shouldOverrideUrlLoading(
					view: WebView?,
					request: WebResourceRequest?
				): Boolean {
					val customWebResourceRequest = CustomWebResourceRequest(request, null)
					return webViewClient.shouldOverrideUrlLoading(this@CustomKitWebView, customWebResourceRequest)
				}
				
				override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
					webViewClient.onPageStarted(this@CustomKitWebView, url, favicon)
				}
				
				override fun onPageFinished(view: WebView?, url: String?) {
					webViewClient.onPageFinished(this@CustomKitWebView, url)
				}
				
				override fun onReceivedError(
					view: WebView?,
					request: WebResourceRequest?,
					error: WebResourceError?
				) {
					val customWebResourceRequest = CustomWebResourceRequest(request, null)
					webViewClient.onReceivedError(this@CustomKitWebView, customWebResourceRequest, error)
				}
				
				override fun onReceivedHttpError(
					view: WebView?,
					request: WebResourceRequest?,
					errorResponse: WebResourceResponse?
				) {
					val customWebResourceRequest = CustomWebResourceRequest(request, null)
					webViewClient.onReceivedHttpError(this@CustomKitWebView, customWebResourceRequest, errorResponse)
				}
				
				override fun onReceivedSslError(
					view: WebView?,
					handler: SslErrorHandler?,
					error: SslError?
				) {
					val customSslErrorHandler = CustomSslErrorHandler(handler, null)
					webViewClient.onReceivedSslError(this@CustomKitWebView, customSslErrorHandler, error)
				}
			})
		}
	}
	
	override fun getCustomWebViewClient(): CustomWebViewClient? {
		return customWebViewClient
	}
	
	override fun setCustomWebChromeClient(webChromeClient: CustomWebChromeClient?) {
		this.customWebChromeClient = webChromeClient
		if (webChromeClient != null) {
			setWebChromeClient(object : WebChromeClient() {
				override fun onProgressChanged(view: WebView?, newProgress: Int) {
					webChromeClient.onProgressChanged(this@CustomKitWebView, newProgress)
				}
				
				override fun onReceivedTitle(view: WebView?, title: String?) {
					webChromeClient.onReceivedTitle(this@CustomKitWebView, title)
				}
			})
		}
	}
	
	override fun getCustomWebChromeClient(): CustomWebChromeClient? {
		return customWebChromeClient
	}
	
	override fun invokeJavascript(str: String, block: ((any: String) -> Unit)?) {
		evaluateJavascript(str) {
			block?.invoke(it)
		}
	}
	
	override fun setWebViewHorizontalScrollBarEnabled(var1: Boolean) {
		isHorizontalScrollBarEnabled = var1
	}
	
	override fun setWebViewVerticalScrollBarEnabled(var1: Boolean) {
		isVerticalScrollBarEnabled = var1
	}
	
	override fun computeWebViewVerticalScrollOffset(): Int {
		return computeVerticalScrollOffset()
	}
	
}