package cn.yue.test.mvp

import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.EditText
import cn.yue.base.common.utils.device.KeyboardUtils
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.dialog.HintDialog
import cn.yue.base.middle.mvp.components.binding.BaseHintBindFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestWebviewBinding
import com.alibaba.android.arouter.facade.annotation.Route


/**
 * Description :
 * Created by yue on 2022/2/28
 */
@Route(path = "/app/testWeb")
class TestWebFragment : BaseHintBindFragment<FragmentTestWebviewBinding>() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_webview
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
                    binding.webView.loadUrl(textStr.toString())
                } else {
                    binding.webView.loadUrl("http://$textStr")
                }
            }
            .build()
            .show()
        mHandler.postDelayed(Runnable {
            KeyboardUtils.showSoftInput(searchET)
        }, 500)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.webView.loadUrl("https://www.baidu.com")
        val setting = binding.webView.settings
        setting.javaScriptEnabled = true
        setting.setSupportZoom(true)
        setting.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                topBar.setCenterTextStr(title)
            }
        }
    }

    override fun onFragmentBackPressed(): Boolean {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }
        return super.onFragmentBackPressed()
    }
}