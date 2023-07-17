package cn.yue.base.wx

import android.os.Bundle

/**
 * Description :
 * Created by yue on 2020/12/13
 */
class WXShareEntity (
       val type: Int,
       val params: Bundle
) {
    companion object {
        const val SHARE_TO_FRIEND = 1
        const val SHARE_TO_PYQ = 2

        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
        const val TYPE_MUSIC = 2
        const val TYPE_VIDEO = 3
        const val TYPE_WEB = 4

        const val KEY_WX_TYPE = "key_wx_type"
        const val KEY_WX_TITLE = "key_wx_title"
        const val KEY_WX_SUMMARY = "key_wx_summary"
        const val KEY_WX_TEXT = "key_wx_text"
        const val KEY_WX_IMG_LOCAL = "key_wx_local_img"
        const val KEY_WX_IMG_RES = "key_wx_img_res"
        const val KEY_WX_MUSIC_URL = "key_wx_music_url"
        const val KEY_WX_VIDEO_URL = "key_wx_video_url"
        const val KEY_WX_WEB_URL = "key_wx_web_url"

        fun createTextInfo(isSharePYQ: Boolean, text: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_TEXT)
            params.putString(KEY_WX_TEXT, text)
            return WXShareEntity(type, params)
        }

        fun createImageInfo(isSharePYQ: Boolean, imageUrl: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_IMAGE)
            params.putString(KEY_WX_IMG_LOCAL, imageUrl)
            return WXShareEntity(type, params)
        }

        fun createImageInfo(isSharePYQ: Boolean, imageRes: Int): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_IMAGE)
            params.putInt(KEY_WX_IMG_RES, imageRes)
            return WXShareEntity(type, params)
        }

        fun createMusicInfo(isSharePYQ: Boolean, musicUrl: String, imageUrl: String, title: String, summary: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_MUSIC)
            params.putString(KEY_WX_MUSIC_URL, musicUrl)
            params.putString(KEY_WX_TITLE, title)
            params.putString(KEY_WX_SUMMARY, summary)
            params.putString(KEY_WX_IMG_LOCAL, imageUrl)
            return WXShareEntity(type, params)
        }

        fun createVideoInfo(isSharePYQ: Boolean, videoUrl: String, imageUrl: String, title: String, summary: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_VIDEO)
            params.putString(KEY_WX_VIDEO_URL, videoUrl)
            params.putString(KEY_WX_TITLE, title)
            params.putString(KEY_WX_SUMMARY, summary)
            params.putString(KEY_WX_IMG_LOCAL, imageUrl)
            return WXShareEntity(type, params)
        }

        fun createVideoInfo(isSharePYQ: Boolean, videoUrl: String, imageRes: Int, title: String, summary: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_VIDEO)
            params.putString(KEY_WX_VIDEO_URL, videoUrl)
            params.putString(KEY_WX_TITLE, title)
            params.putString(KEY_WX_SUMMARY, summary)
            params.putInt(KEY_WX_IMG_RES, imageRes)
            return WXShareEntity(type, params)
        }

        fun createWebPageInfo(isSharePYQ: Boolean, webUrl: String, imageUrl: String, title: String, summary: String): WXShareEntity {
            val type = if (isSharePYQ) SHARE_TO_PYQ else SHARE_TO_FRIEND
            val params = Bundle()
            params.putInt(KEY_WX_TYPE, TYPE_WEB)
            params.putString(KEY_WX_WEB_URL, webUrl)
            params.putString(KEY_WX_TITLE, title)
            params.putString(KEY_WX_SUMMARY, summary)
            params.putString(KEY_WX_IMG_LOCAL, imageUrl)
            return WXShareEntity(type, params)
        }
    }
}