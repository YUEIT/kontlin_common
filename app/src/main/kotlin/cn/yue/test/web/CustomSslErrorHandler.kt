package cn.yue.test.web

/**
 * Description :
 * Created by yue on 2023/7/27
 */
class CustomSslErrorHandler(
	private val kitSslErrorHandler: android.webkit.SslErrorHandler?,
	private val x5SslErrorHandler: com.tencent.smtt.export.external.interfaces.SslErrorHandler?
) {
	
	fun proceed() {
		kitSslErrorHandler?.proceed()
		x5SslErrorHandler?.proceed()
	}
	
	fun cancel() {
		kitSslErrorHandler?.cancel()
		x5SslErrorHandler?.cancel()
	}
}