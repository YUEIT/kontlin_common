package cn.yue.test.web

import android.graphics.Bitmap

/**
 * Description :
 * Created by yue on 2023/7/27
 */
open class CustomWebViewClient {
	
	open fun shouldOverrideUrlLoading(
		view: IWebView?,
		request: CustomWebResourceRequest?
	): Boolean {
		return false
	}
	
	open fun onPageStarted(view: IWebView?, url: String?, favicon: Bitmap?) {
	
	}
	
	open fun onPageFinished(view: IWebView?, url: String?) {
	
	}
	
	open fun onReceivedError(
		view: IWebView?,
		request: CustomWebResourceRequest?,
		error: Any?
	) {
	
	}
	
	open fun onReceivedHttpError(
		view: IWebView?,
		request: CustomWebResourceRequest?,
		errorResponse: Any?
	) {
	
	}
	
	open fun onReceivedSslError(
		view: IWebView?,
		handler: CustomSslErrorHandler?,
		error: Any?
	) {
	
	}
}