package cn.yue.base.middle.net.wrapper


import com.google.gson.annotations.SerializedName

/**
 * @author qianjujun
 * @time   2016/3/5 16:11
 * @TODO  java分页数据
 */
open class BaseListJavaBean<T>(
        @SerializedName("list")
        private var list: MutableList<T>? = null,         // 瀑布式
        @SerializedName("page_list")  //分页式
        var pageList: MutableList<T>? = null,
        @SerializedName("dataList")  //分页式
        var dataList: MutableList<T>? = null,
        @SerializedName("total")  //分页式
        var mTotal: Int = 0,//总数。
        @SerializedName("page_count")
        var mPageCount: Int = 0,    //页数
        @SerializedName("page_size")
        var mPageSize: Int = 0,    //每页数量
        @SerializedName("page_no")
        var mPageNo: Int = 0,    //当前页面号
        @SerializedName("count")
        var mCount: Int = 0,//
        @SerializedName("nt")
        var nt: String? = null,    //版本Id，用作下一页版本号，null表示没有下一页
        @SerializedName("pt")
        var pt: String? = null    //版本Id，用作上一页版本号
    ) {

    fun isDataEmpty(): Boolean = getRealList() == null || getRealList()!!.size == 0

    fun getRealList(): MutableList<T>? {
        if (list != null) {
            return list
        } else if (dataList != null) {
            return dataList
        } else if (pageList != null) {
            return pageList
        }
        return null
    }

    fun getRealTotal(): Int {
        return if (mCount == 0) mTotal else mCount
    }

    fun getCount(): Int {
        return if (mCount == 0) mTotal else mCount
    }
}
