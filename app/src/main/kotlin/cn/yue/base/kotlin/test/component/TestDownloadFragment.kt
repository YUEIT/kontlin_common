package cn.yue.base.kotlin.test.component

import android.content.Intent
import android.os.Bundle
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.utils.UpdateService
import cn.yue.base.middle.components.BaseHintFragment
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.fragment_test_download.*

/**
 * Description :
 * Created by yue on 2020/8/24
 */
@Route(path = "/app/testDownload")
class TestDownloadFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_download
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        downTV.setOnClickListener {
            val intent = Intent(mActivity, UpdateService::class.java)
            mActivity.startService(intent)
        }
    }

}



