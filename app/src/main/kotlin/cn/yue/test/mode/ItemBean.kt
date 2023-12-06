package cn.yue.test.mode

import android.os.Parcel
import android.os.Parcelable

/**
 * Description :
 * Created by yue on 2021/1/13
 */
class ItemBean (var index:Int = 0,
                var name: String? = null): SuperItemBean() {


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(index)
    }

    override fun readToParcel(parcel: Parcel, flags: Int) {
        super.readToParcel(parcel, flags)
        index = parcel.readInt()
        // 20.87 1841 1469 2865
    }
}