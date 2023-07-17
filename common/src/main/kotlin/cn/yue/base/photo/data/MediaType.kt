package cn.yue.base.photo.data

enum class MediaType(val value: Int) {
    ALL(0), PHOTO(1), VIDEO(2);

    companion object {
        @JvmStatic
        fun onlyShowImages(mediaType: MediaType): Boolean {
            return mediaType == PHOTO
        }

        @JvmStatic
        fun onlyShowVideos(mediaType: MediaType): Boolean {
            return mediaType == VIDEO
        }

        fun valueOf(value: Int): MediaType {
            return when (value) {
                1 -> PHOTO
                2 -> VIDEO
                else -> ALL
            }
        }
    }


}