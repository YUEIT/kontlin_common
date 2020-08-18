package cn.yue.base.common.photo.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Description :
 * Created by yue on 2019/3/11
 */
data class MediaVO(var id: String? = "",
              var mimeType: String? = "",
              var uri: Uri? = null,
              var size: Long = 0,
              var duration: Long = 0,
              var url: String? = "") : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(id)
        dest.writeString(mimeType)
        dest.writeParcelable(uri, flags)
        dest.writeLong(size)
        dest.writeLong(duration)
    }

    companion object CREATOR : Parcelable.Creator<MediaVO> {
        override fun createFromParcel(parcel: Parcel): MediaVO {
            return MediaVO(parcel)
        }

        override fun newArray(size: Int): Array<MediaVO?> {
            return arrayOfNulls(size)
        }

        @JvmStatic
        fun equals(mediaVO: MediaVO?, mediaVO1: MediaVO?): Boolean {
            return if (mediaVO?.uri == null || mediaVO1?.uri == null) {
                false
            } else mediaVO.uri == mediaVO1.uri
        }
    }
}