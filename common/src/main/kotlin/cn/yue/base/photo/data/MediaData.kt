package cn.yue.base.photo.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Description :
 * Created by yue on 2019/3/11
 */
data class MediaData(var id: String? = "",
                     var mimeType: String? = "",
                     var uri: Uri? = null,
                     var size: Long = 0,
                     var duration: Long = 0,
                     var width: Int = 0,
                     var height: Int = 0,
                     var url: String? = "") : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(mimeType)
        dest.writeParcelable(uri, flags)
        dest.writeLong(size)
        dest.writeLong(duration)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeString(url)
    }

    fun getMediaType(): MediaType {
        return when {
            MimeType.isImage(mimeType) -> {
	            MediaType.PHOTO
            }
            MimeType.isVideo(mimeType) -> {
	            MediaType.VIDEO
            }
            else -> {
	            MediaType.ALL
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<MediaData> {
        override fun createFromParcel(parcel: Parcel): MediaData {
            return MediaData(parcel)
        }

        override fun newArray(size: Int): Array<MediaData?> {
            return arrayOfNulls(size)
        }

        @JvmStatic
        fun equals(mediaData: MediaData?, mediaData1: MediaData?): Boolean {
            return if (mediaData?.uri == null || mediaData1?.uri == null) {
                false
            } else mediaData.uri == mediaData1.uri
        }
    }
}