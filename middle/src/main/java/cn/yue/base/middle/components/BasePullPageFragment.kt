package cn.yue.base.middle.components

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvp.PageStatus
import cn.yue.base.middle.net.NetworkConfig
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.observer.BaseNetSingleObserver
import cn.yue.base.middle.net.wrapper.BaseListBean
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_base_pull_page.*

/**
 * Description :
 * Created by yue on 2018/11/13
 */
abstract class BasePullPageFragment<P : BaseListBean<S>, S> : BaseFragment(), IStatusView{

    private var pageNo: Int = 0
    private var pageNt: String = ""
    private var dataList: MutableList<S> = ArrayList()
    private lateinit var adapter: CommonAdapter<S>
    private lateinit var footer: BasePullFooter
    private var status: PageStatus = PageStatus.STATUS_NORMAL

    override fun getLayoutId(): Int = R.layout.fragment_base_pull_page

    override fun initView(savedInstanceState: Bundle?) {
        refreshL.setOnRefreshListener { refresh() }
        baseRV.layoutManager = getLayoutManager()
        baseRV.adapter = getAdapter()
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                var isTheLast = false
                if (recyclerView?.layoutManager is LinearLayoutManager) {
                    isTheLast = (recyclerView?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= dataList.size
                } else if (recyclerView?.layoutManager is GridLayoutManager) {
                    isTheLast = (recyclerView?.layoutManager as GridLayoutManager).findLastVisibleItemPosition() >= dataList.size - (recyclerView?.layoutManager as GridLayoutManager).spanCount
                }
                if (isTheLast && (status == PageStatus.STATUS_NORMAL || status == PageStatus.STATUS_SUCCESS)) {
                    status = PageStatus.STATUS_LOADING
                    adapter.addFooterView(footer)
                    footer.showStatusView(status)
                    loadData()
                }
            }
        })
        footer = getFooter()
        refresh()
    }

    protected fun getLayoutManager() : RecyclerView.LayoutManager  = LinearLayoutManager(mActivity)

    protected fun getAdapter() : CommonAdapter<S> {
        adapter = object :  CommonAdapter<S>(mActivity!!, ArrayList<S>()) {

            override fun bindData(holder: CommonViewHolder<S>, position: Int, t: S) {
                bindItemData(holder, position, t)
            }

            override fun getLayoutIdByType(viewType: Int): Int  = getItemLayoutId()

        }
        return adapter
    }

    protected fun getFooter(): BasePullFooter = BasePullFooter(mActivity)

    abstract fun getItemLayoutId() : Int

    abstract fun bindItemData(holder: CommonViewHolder<S>, position: Int, s: S)

    private fun refresh() {
        refreshL.isRefreshing = true
        pageNo = 0
        pageNt = ""
        loadData()
    }

    abstract fun getRequestSingle(pageNo: Int, nt: String): Single<P>?

    private fun loadData() {
        if (getRequestSingle(pageNo, pageNt) == null) {
            return
        }
        getRequestSingle(pageNo, pageNt)!!
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : BaseNetSingleObserver<P>() {
                    override fun onException(e: ResultException) {
                        if (refreshL.isRefreshing) {
                            refreshL.isRefreshing = false
                        }
                        loadFailed(e)
                    }

                    override fun onSuccess(p: P) {
                        if (refreshL.isRefreshing) {
                            refreshL.isRefreshing = false
                        }
                        if (p.getTotal() == 0) {
                            showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
                        }
                        if (p.getPageCount() == 0) {
                            loadNoMore()
                        } else {
                            loadSuccess(p)
                        }
                    }

                })
    }

    fun loadSuccess(p: P) {
        showStatusView(PageStatus.STATUS_SUCCESS)
        pageNt = p.getPageNt()?:""
        pageNo = p.getPageNo()
        dataList = p.getList()?:ArrayList()
        adapter.setList(dataList)
    }

    fun loadFailed(e: ResultException) {
        when (e.errCode) {
            NetworkConfig.ERROR_NO_NET -> showStatusView(PageStatus.STATUS_ERROR_NET)
            NetworkConfig.ERROR_NO_DATA -> showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
            NetworkConfig.ERROR_OTHER -> showStatusView(PageStatus.STATUS_ERROR_OPERATION)
        }
    }

    fun loadNoMore() {
        showStatusView(PageStatus.STATUS_END)
    }

    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_SUCCESS -> {
                showPageHintSuccess()
                adapter.removeFooterView(footer)
            }
            PageStatus.STATUS_END -> {
                showPageHintSuccess()
                footer.showStatusView(status)
            }
            PageStatus.STATUS_ERROR_NET -> {
                showPageHintErrorNet()
                footer.showStatusView(status)
            }
            PageStatus.STATUS_ERROR_NO_DATA -> {
                showPageHintErrorNoData()
                adapter.removeFooterView(footer)
            }
            PageStatus.STATUS_ERROR_OPERATION -> {
                showPageHintErrorOperation()
                adapter.removeFooterView(footer)
            }
            else -> return
        }
    }

    protected fun showPageHintSuccess() {
        hintView.visibility = View.GONE
    }

    protected fun showPageHintErrorNet() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("网络异常~")
    }

    protected fun showPageHintErrorNoData() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("无数据~")
    }

    protected fun showPageHintErrorOperation() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("未知异常~")
    }
}

