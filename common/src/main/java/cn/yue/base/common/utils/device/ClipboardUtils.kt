package cn.yue.base.common.utils.device

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

import cn.yue.base.common.utils.Utils


/**
 * 介绍：剪贴板相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ClipboardUtils {

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    @JvmStatic
    fun copyText(text: CharSequence) {
        val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText("text", text)
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    @JvmStatic
    val text: CharSequence?
        get() {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).coerceToText(Utils.getContext())
            } else null
        }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    @JvmStatic
    fun copyUri(uri: Uri) {
        val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newUri(Utils.getContext().contentResolver, "uri", uri)
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    @JvmStatic
    val uri: Uri?
        get() {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).uri
            } else null
        }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    @JvmStatic
    fun copyIntent(intent: Intent) {
        val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newIntent("intent", intent)
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    @JvmStatic
    val intent: Intent?
        get() {
            val clipboard = Utils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).intent
            } else null
        }

}
