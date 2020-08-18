package cn.yue.base.common.photo.data

enum class MediaType {
    ALL, PHOTO, VIDEO;

    companion object {
        @JvmStatic
        fun onlyShowImages(mediaType: MediaType): Boolean {
            return mediaType == PHOTO
        }

        @JvmStatic
        fun onlyShowVideos(mediaType: MediaType): Boolean {
            return mediaType == VIDEO
        }
    }
}