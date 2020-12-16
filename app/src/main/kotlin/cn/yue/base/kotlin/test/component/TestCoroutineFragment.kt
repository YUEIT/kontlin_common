package cn.yue.base.kotlin.test.component

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.mode.ApiManager
import cn.yue.base.kotlin.test.utils.UpdateService
import cn.yue.base.middle.components.BaseHintFragment
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BaseNetObserver
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.fragment_test_coroutine.*
import java.util.*

/**
 * Description :
 * Created by yue on 2020/8/24
 */
@Route(path = "/app/testCoroutine")
class TestCoroutineFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_coroutine
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        syncTV.setOnClickListener {
            request()
        }
        downTV.setOnClickListener {
            val intent = Intent(mActivity, UpdateService::class.java)
            mActivity.startService(intent)
        }
    }

    private fun request() {
        lifecycleScope.request(
                arrayListOf(suspend {
                    ApiManager.getApi().getJson()
                }, suspend {
                    ApiManager.getApi().getUuid()
                }),
                object : BaseNetObserver<ArrayList<*>>() {
                    override fun onException(e: ResultException) {

                    }

                    override fun onSuccess(t: ArrayList<*>) {

                    }

                }
        )
    }

}



