package cn.yue.base.net

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Description :
 * Created by yue on 2023/7/25
 */
class HttpEventListener: EventListener() {
	
	
	override fun cacheConditionalHit(call: Call, cachedResponse: Response) {
		super.cacheConditionalHit(call, cachedResponse)
		log("cacheConditionalHit")
	}
	
	override fun cacheHit(call: Call, response: Response) {
		super.cacheHit(call, response)
		log("cacheHit")
	}
	
	override fun cacheMiss(call: Call) {
		super.cacheMiss(call)
		log("cacheMiss")
	}
	
	override fun callEnd(call: Call) {
		super.callEnd(call)
		log("callEnd")
	}
	
	override fun callFailed(call: Call, ioe: IOException) {
		super.callFailed(call, ioe)
		log("callFailed")
	}
	
	override fun callStart(call: Call) {
		super.callStart(call)
		log("callStart")
	}
	
	override fun canceled(call: Call) {
		super.canceled(call)
		log("canceled")
	}
	
	override fun connectEnd(
		call: Call,
		inetSocketAddress: InetSocketAddress,
		proxy: Proxy,
		protocol: Protocol?
	) {
		super.connectEnd(call, inetSocketAddress, proxy, protocol)
		log("connectEnd")
	}
	
	override fun connectFailed(
		call: Call,
		inetSocketAddress: InetSocketAddress,
		proxy: Proxy,
		protocol: Protocol?,
		ioe: IOException
	) {
		super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
		log("connectFailed")
	}
	
	override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
		super.connectStart(call, inetSocketAddress, proxy)
		log("connectStart")
	}
	
	override fun connectionAcquired(call: Call, connection: Connection) {
		super.connectionAcquired(call, connection)
		log("connectionAcquired")
	}
	
	override fun connectionReleased(call: Call, connection: Connection) {
		super.connectionReleased(call, connection)
		log("connectionReleased")
	}
	
	override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
		super.dnsEnd(call, domainName, inetAddressList)
		log("dnsEnd")
	}
	
	override fun dnsStart(call: Call, domainName: String) {
		super.dnsStart(call, domainName)
		log("dnsStart")
	}
	
	override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
		super.proxySelectEnd(call, url, proxies)
		log("proxySelectEnd")
	}
	
	override fun proxySelectStart(call: Call, url: HttpUrl) {
		super.proxySelectStart(call, url)
		log("proxySelectStart")
	}
	
	override fun requestBodyEnd(call: Call, byteCount: Long) {
		super.requestBodyEnd(call, byteCount)
		log("requestBodyEnd")
	}
	
	override fun requestBodyStart(call: Call) {
		super.requestBodyStart(call)
		log("requestBodyStart")
	}
	
	override fun requestFailed(call: Call, ioe: IOException) {
		super.requestFailed(call, ioe)
		log("requestFailed")
	}
	
	override fun requestHeadersEnd(call: Call, request: Request) {
		super.requestHeadersEnd(call, request)
		log("requestHeadersEnd")
	}
	
	override fun requestHeadersStart(call: Call) {
		super.requestHeadersStart(call)
		log("requestHeadersStart")
	}
	
	override fun responseBodyEnd(call: Call, byteCount: Long) {
		super.responseBodyEnd(call, byteCount)
		log("responseBodyEnd")
	}
	
	override fun responseBodyStart(call: Call) {
		super.responseBodyStart(call)
		log("responseBodyStart")
	}
	
	override fun responseFailed(call: Call, ioe: IOException) {
		super.responseFailed(call, ioe)
		log("responseFailed")
	}
	
	override fun responseHeadersEnd(call: Call, response: Response) {
		super.responseHeadersEnd(call, response)
		log("responseHeadersEnd")
	}
	
	override fun responseHeadersStart(call: Call) {
		super.responseHeadersStart(call)
		log("responseHeadersStart")
	}
	
	override fun satisfactionFailure(call: Call, response: Response) {
		super.satisfactionFailure(call, response)
		log("satisfactionFailure")
	}
	
	override fun secureConnectEnd(call: Call, handshake: Handshake?) {
		super.secureConnectEnd(call, handshake)
		log("secureConnectEnd")
	}
	
	override fun secureConnectStart(call: Call) {
		super.secureConnectStart(call)
		log("secureConnectStart")
	}
	
	private fun log(str: String) {
		Log.d("http", str)
	}
}