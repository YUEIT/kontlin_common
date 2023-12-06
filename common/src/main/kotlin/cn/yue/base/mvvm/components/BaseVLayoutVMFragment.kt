package cn.yue.base.mvvm.components

import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.mvvm.PageViewModel
import cn.yue.base.widget.recyclerview.FooterAdapter
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager

/**
 * Description :
 * Created by yue on 2020/8/8
 */
open class BaseVLayoutVMFragment<VM : PageViewModel<S>, S> : BaseListVMFragment<VM, S>() {

    private var layoutManager: VirtualLayoutManager? = null

    override fun initRecyclerView(baseRV: RecyclerView) {
        layoutManager = VirtualLayoutManager(mActivity)
        val viewPool = RecyclerView.RecycledViewPool()
        viewPool.setMaxRecycledViews(0, 10)
        baseRV.itemAnimator = null
        baseRV.setRecycledViewPool(viewPool)
        baseRV.layoutManager = layoutManager
        val delegateAdapter = initAdapter()
        baseRV.adapter = delegateAdapter
    }

    override fun initAdapter(): DelegateAdapter? {
        val delegateAdapter = DelegateAdapter(layoutManager)
        val adapterList = createAdapters()
        adapterList.add(initFooter().also { footer = it })
        delegateAdapter.setAdapters(adapterList)
        return delegateAdapter
    }

    override fun initFooter(): FooterAdapter {
        val footer = FooterAdapter()
        footer.setOnReloadListener {
            viewModel.loadMoreData()
        }
        return footer
    }

    open fun createAdapters(): MutableList<DelegateAdapter.Adapter<*>> {
        return arrayListOf()
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return layoutManager!!
    }

    override fun getAdapter(): DelegateAdapter? {
        return super.getAdapter() as DelegateAdapter?
    }

    override fun setData(list: MutableList<S>) {

    }

}