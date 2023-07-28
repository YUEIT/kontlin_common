package cn.yue.test.mvp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebSettings
import android.widget.EditText
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.base.net.download.DownloadUtils
import cn.yue.base.utils.code.UrlUtils
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.utils.device.KeyboardUtils
import cn.yue.base.widget.TopBar
import cn.yue.base.widget.dialog.HintDialog
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestWebviewBinding
import cn.yue.test.web.*
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*


/**
 * Description :
 * Created by yue on 2022/2/28
 */
@Route(path = "/app/testWeb")
class TestWebFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_webview
    }
    
    private lateinit var binding: FragmentTestWebviewBinding
    
    override fun bindLayout(inflated: View) {
        binding = FragmentTestWebviewBinding.bind(inflated)
    }
    
    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setRightImage(R.drawable.icon_search)
            .setRightClickListener {
                showSearchDialog()
            }
    }

    private fun showSearchDialog() {
        val contentView = View.inflate(mActivity, R.layout.layout_search, null)
        val searchET = contentView.findViewById<EditText>(R.id.searchET)
        HintDialog.Builder(mActivity)
            .setContentView(contentView)
            .setTitleStr("搜索")
            .setSingleClick(true)
            .setLeftClickStr("确定")
            .setOnLeftClickListener {
                val textStr = searchET.text
                if (textStr.startsWith("http://") || textStr.startsWith("https://")) {
                    webView?.loadUrl(textStr.toString())
                } else {
                    webView?.loadUrl("http://$textStr")
                }
            }
            .build()
            .show()
        mHandler.postDelayed(Runnable {
            KeyboardUtils.showSoftInput(searchET)
        }, 500)
    }
    
    private var webView: ProxyWebView? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initWebView()
    }
    
    private fun initWebView() {
        webView = ProxyWebView(mActivity)
        webView?.initView(false)
        binding.webContainer.addView(webView)
        webView?.apply {
            getSettings()?.apply {
                setJavaScriptEnabled(true)
                setDomStorageEnabled(true)
                setAppCacheEnabled(true)
                setCacheMode(WebSettings.LOAD_DEFAULT)
                setAppCacheMaxSize(40 * 1024 * 1024)
                setMediaPlaybackRequiresUserGesture(false)
                setTextZoom(100)
//                val ua = getUserAgentString()
//                setUserAgentString("$ua Android/${InitConstant.getVersionName()}")
//                setUseWideViewPort(true)
                setLoadWithOverviewMode(true)
            }
            setScrollBarEnabled(false)
            setWebViewClient(object : CustomWebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: IWebView?,
                    request: CustomWebResourceRequest?
                ): Boolean {
                    val url = request?.getUrl()?.path ?: ""
                    if (url.contains(".apk")) {
                        DownloadUtils.downloadApk(url, "${UUID.randomUUID()}.apk")
                        return true
                    }
                    try {
                        if (url.startsWith("weixin://") || url.startsWith("alipays://")) {
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                            return true
                        }
                    } catch (e: Exception) {
                        if (url.startsWith("weixin://")) {
                            ToastUtils.showShortToast("请安装最新版微信")
                        } else if (url.startsWith("alipays://")) {
                            ToastUtils.showShortToast("请安装最新版支付宝")
                        }
                        return true
                    }
                    if (url.contains("https://wx.tenpay.com")) {
                        var redirectUrl: String =
                            UrlUtils.getParamsFromUrl(url).getParameter("redirect_url")
                        if (TextUtils.isEmpty(redirectUrl)) {
                            redirectUrl = ""
                        }
                        val extraHeaders = HashMap<String, String>()
                        extraHeaders["Referer"] = redirectUrl
                        view!!.loadUrl(url, extraHeaders)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
    
                override fun onReceivedSslError(
                    view: IWebView?,
                    handler: CustomSslErrorHandler?,
                    error: Any?
                ) {
                    handler?.proceed()
                }
            })
            
            setWebChromeClient(object : CustomWebChromeClient() {
    
                override fun onProgressChanged(view: IWebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    
                }
    
                override fun onReceivedTitle(view: IWebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    topBar.setCenterTextStr(title)
                }
            })
            loadUrl("http://www.baidu.com")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        binding.webContainer.removeView(webView)
    }
    
    override fun onFragmentBackPressed(): Boolean {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return true
        }
        return super.onFragmentBackPressed()
    }
}