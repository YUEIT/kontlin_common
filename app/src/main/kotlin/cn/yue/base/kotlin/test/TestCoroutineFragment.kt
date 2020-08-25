package cn.yue.base.kotlin.test

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import cn.yue.base.middle.components.BaseHintFragment
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BaseNetSingleObserver
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.fragment_test_coroutine.*

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
    }

    private fun request() {
        lifecycleScope.request(
                arrayListOf(suspend {
                    ApiManager.getApi().getJson()
                }, suspend {
                    ApiManager.getApi().getUuid()
                }),
                object : BaseNetSingleObserver<ArrayList<*>>() {
                    override fun onException(e: ResultException) {
                        Log.d("luobiao", "onException: " + e)
                    }

                    override fun onSuccess(t: ArrayList<*>) {
                        Log.d("luobiao", "onSuccess: " + t[0] + " , " + t[1])
                    }

                }
        )
    }

}



