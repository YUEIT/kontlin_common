package cn.yue.test.mvp

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.mvp.components.BasePullFragment
import cn.yue.base.net.observer.BasePullObserver
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.widget.TopBar
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder
import cn.yue.test.R
import cn.yue.test.mode.ItemBean
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.rxjava3.core.Single

@Route(path = "/app/testPull")
class TestPullFragment : BasePullFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_pull
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("pull test")
    }

    private lateinit var adapter: CommonAdapter<ItemBean>

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        adapter = object : CommonAdapter<ItemBean>() {

            override fun bindData(holder: CommonViewHolder, position: Int, t: ItemBean) {
                holder.setText(R.id.testTV, t.name)
                holder.setOnClickListener(R.id.testTV) {
                    ToastUtils.showLongToast("$position")
                }
            }

            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }
        }

        recyclerView.adapter = adapter
    }


    override fun loadData() {
        Single.just(arrayListOf(
            ItemBean(1, "a"),
            ItemBean(2, "b"),
            ItemBean(3, "c")
        ))
                .compose(getLifecycleProvider().toBindLifecycle())
                .subscribe(object : BasePullObserver<List<ItemBean>>(this) {
                    override fun onSuccess(t: List<ItemBean>) {
                        super.onSuccess(t)
                        adapter.setList(t)
                    }
                })
    }
}