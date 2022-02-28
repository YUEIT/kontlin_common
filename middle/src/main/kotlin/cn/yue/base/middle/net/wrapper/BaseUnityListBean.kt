package cn.yue.base.middle.net.wrapper

import com.google.gson.annotations.SerializedName

open class BaseUnityListBean<T>(
        @SerializedName(value="list", alternate= ["page_list", "dataList"])
        var mList: MutableList<T>? = null,
        @SerializedName(value = "total", alternate = ["count"])
        var mTotal: Int = 0,//总数。
        @SerializedName(value = "pageCount", alternate = ["page_count"])
        var mPageCount: Int = 0,    //页数
        @SerializedName(value = "pageSize", alternate = ["page_size"])
        var mPageSize: Int = 0,    //每页数量
        @SerializedName(value = "pageNo", alternate = ["page_no"])
        var mPageNo: Int = 0,    //当前页面号
        @SerializedName("nt")
        var nt: String? = null,    //版本Id，用作下一页版本号，null表示没有下一页
        @SerializedName("pt")
        var pt: String? = null    //版本Id，用作上一页版本号
    ) {

    fun isDataEmpty(): Boolean = getRealList().isNullOrEmpty()

    fun getRealList(): MutableList<T>? {
        return mList
    }

    fun getRealPageSize(): Int {
        return if (mPageSize == 0) {
            getRealList()?.size ?: 0
        } else {
            mPageSize
        }
    }

    fun getRealTotal(): Int {
        return mTotal
    }

    fun getRealPageNo(): Int {
        return mPageNo
    }

    fun getRealPageNt(): String? {
        return nt
    }
}
