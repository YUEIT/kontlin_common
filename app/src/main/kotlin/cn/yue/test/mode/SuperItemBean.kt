package cn.yue.test.mode

import android.os.Parcel
import android.os.Parcelable

open class SuperItemBean(var title: String? = null) {

    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    open fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    open fun readToParcel(parcel: Parcel, flags: Int) {

    }
}