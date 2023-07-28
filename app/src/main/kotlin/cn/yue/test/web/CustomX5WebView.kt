package cn.yue.test.web

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * Description :
 * Created by yue on 2023/7/26
 */
class CustomX5WebView(context: Context, attributeSet: AttributeSet? = null)
	: WebView(context, attributeSet), IWebView {
	
	private var customWebSettings: CustomWebSettings? = null
	private var customWebViewClient: CustomWebViewClient? = null
	private var customWebChromeClient: CustomWebChromeClient? = null
	
	override fun getCustomWebSettings(): CustomWebSettings? {
		if (customWebSettings == null) {
			customWebSettings = CustomWebSettings(null, settings)
		}
		return customWebSettings
	}
	
	override fun setCustomWebViewClient(webViewClient: CustomWebViewClient?) {
		customWebViewClient = webViewClient
		if (webViewClient != null) {
			setWebViewClient(object : WebViewClient() {
				
				override fun shouldOverrideUrlLoading(
					p0: WebView?,
					p1: WebResourceRequest?
				): Boolean {
					val customWebResourceRequest = CustomWebResourceRequest(null, p1)
					return webViewClient.shouldOverrideUrlLoading(this@CustomX5WebView, customWebResourceRequest)
				}
				
				override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
					webViewClient.onPageStarted(this@CustomX5WebView, p1, p2)
				}
				
				override fun onPageFinished(p0: WebView?, p1: String?) {
					webViewClient.onPageFinished(this@CustomX5WebView, p1)
				}
				
				override fun onReceivedError(
					p0: WebView?,
					p1: WebResourceRequest?,
					p2: WebResourceError?
				) {
					val customWebResourceRequest = CustomWebResourceRequest(null, p1)
					webViewClient.onReceivedError(this@CustomX5WebView, customWebResourceRequest, p2)
				}
				
				override fun onReceivedHttpError(
					p0: WebView?,
					p1: WebResourceRequest?,
					p2: WebResourceResponse?
				) {
					val customWebResourceRequest = CustomWebResourceRequest(null, p1)
					webViewClient.onReceivedHttpError(this@CustomX5WebView, customWebResourceRequest, p2)
				}
				
				override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
					val customSslErrorHandler = CustomSslErrorHandler(null, p1)
					webViewClient.onReceivedSslError(this@CustomX5WebView, customSslErrorHandler, p2)
				}
			})
		}
	}
	
	override fun getCustomWebViewClient(): CustomWebViewClient? {
		return customWebViewClient
	}
	
	override fun setCustomWebChromeClient(webChromeClient: CustomWebChromeClient?) {
		this.customWebChromeClient = webChromeClient
		if (webChromeClient == null) {
			setWebChromeClient(null)
		} else {
			setWebChromeClient(object : WebChromeClient() {
				
				override fun onProgressChanged(p0: WebView?, p1: Int) {
					webChromeClient.onProgressChanged(this@CustomX5WebView, p1)
				}
				
				override fun onReceivedTitle(p0: WebView?, p1: String?) {
					webChromeClient.onReceivedTitle(this@CustomX5WebView, p1)
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
		x5WebViewExtension.isHorizontalScrollBarEnabled = var1
	}
	
	override fun setWebViewVerticalScrollBarEnabled(var1: Boolean) {
		x5WebViewExtension.isVerticalScrollBarEnabled = var1
	}
	
	override fun computeWebViewVerticalScrollOffset(): Int {
		return computeVerticalScrollOffset()
	}
	
}