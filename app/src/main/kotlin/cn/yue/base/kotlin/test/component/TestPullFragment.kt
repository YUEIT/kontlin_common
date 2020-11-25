package cn.yue.base.kotlin.test.component

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.components.BasePullFragment
import cn.yue.base.middle.net.observer.BasePullObserver
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

@Route(path = "/app/testPull")
class TestPullFragment : BasePullFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_pull
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("pull test")
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val testList: MutableList<String> = ArrayList()
        for (i in 0..9) {
            testList.add("ssssa$i")
        }
        recyclerView.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        val adapter: CommonAdapter<String> = object : CommonAdapter<String>(mActivity, testList) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }

            override fun bindData(holder: CommonViewHolder, position: Int, s: String) {
                holder.setText(R.id.testTV, s)
            }
        }
        recyclerView.adapter = adapter
        findViewById<View>(R.id.notifyTV).setOnClickListener { adapter.notifyDataSetChanged() }
    }

    override fun loadData() {
        Single.just("ssss")
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(getLifecycleProvider().toBindLifecycle())
                .subscribe(object : BasePullObserver<String>(this@TestPullFragment) {
                    override fun onNext(s: String) {
                        showShortToast(s)
                    }
                })
    }
}