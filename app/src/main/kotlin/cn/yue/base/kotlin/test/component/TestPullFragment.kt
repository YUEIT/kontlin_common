package cn.yue.base.kotlin.test.component

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.mode.UserBean
import cn.yue.base.middle.components.BasePullFragment
import cn.yue.base.middle.net.observer.BasePullObserver
import cn.yue.base.middle.router.FRouter
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.Single

@Route(path = "/app/testPull")
class TestPullFragment : BasePullFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_pull
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("pull test")
    }

    private lateinit var adapter: CommonAdapter<UserBean>

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        adapter = object : CommonAdapter<UserBean>(mActivity) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: UserBean) {
                holder.setText(R.id.testTV, itemData.name)
                holder.getView<TextView>(R.id.testTV)?.setOnClickListener {
                    mHandler.postDelayed(Runnable { val router = FRouter.instance
                        router.build("/app/testDialog")
                        router.navigationDialogFragment(mActivity) }, 3000)
                }
            }
        }
        recyclerView.adapter = adapter
    }


    override fun loadData() {
        Single.just(arrayListOf(UserBean(1, "a", "", ""),
            UserBean(1, "a", "", "")))
                .compose(getLifecycleProvider().toBindLifecycle())
                .subscribe(object : BasePullObserver<List<UserBean>>(this) {
                    override fun onNext(t: List<UserBean>) {
                        super.onNext(t)
                        adapter.setList(t)
                    }
                })
    }
}