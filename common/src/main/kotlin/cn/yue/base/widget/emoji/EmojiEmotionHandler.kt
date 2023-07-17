package cn.yue.base.widget.emoji

import android.content.Context
import android.text.Spannable
import cn.yue.base.common.R

object EmojiEmotionHandler {

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param useSystemDefault
     */
    fun addEmojis(
        context: Context?,
        text: Spannable,
        emojiSize: Int,
        textSize: Int,
        useSystemDefault: Boolean
    ) {
        addEmojis(context, text, emojiSize, textSize, 0, -1, useSystemDefault)
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     */
    @JvmOverloads
    fun addEmojis(
        context: Context?,
        text: Spannable,
        emojiSize: Int,
        textSize: Int,
        index: Int = 0,
        length: Int = -1,
        useSystemDefault: Boolean = false
    ) {
        if (useSystemDefault) {
            return
        }
        val textLength = text.length
        val textLengthToProcessMax = textLength - index
        val textLengthToProcess =
            if (length < 0 || length >= textLengthToProcessMax) textLength else length + index

        // remove spans throughout all text
        val oldSpans = text.getSpans(0, textLength, EmojiEmotionSpan::class.java)
        for (i in oldSpans.indices) {
            text.removeSpan(oldSpans[i])
        }

        var i = index
        while (i < textLengthToProcess) {
            var skip = 0
            var icon = 0
                val unicode = Character.codePointAt(text, i)
                skip = Character.charCount(unicode)
                if (unicode > 0xff) {
                    icon = getEmojiResource(unicode)
                }
                if (icon == 0 && i + skip < textLengthToProcess) {
                    val followUnicode = Character.codePointAt(text, i + skip)
                    if (followUnicode == 0x20e3) {
                        var followSkip = Character.charCount(followUnicode)
                        followSkip = when (unicode) {
                            else -> 0
                        }
                        skip += followSkip
                    } else {
                        var followSkip = Character.charCount(followUnicode)
                        when (unicode) {
                            0x1f1e8 -> icon =
                                if (followUnicode == 0x1f1f3) R.drawable.emoji_1f1e8_1f1f3 else 0
                            else -> followSkip = 0
                        }
                        skip += followSkip
                    }
                }

            if (icon > 0) {
                text.setSpan(
                    EmojiEmotionSpan(context!!, icon, emojiSize, textSize),
                    i,
                    i + skip,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            i += skip
        }
    }

    fun ensure(context: Context, spannable: Spannable, emojiSize: Int, textSize: Int) {
        val chars = spannable.toString().toCharArray()
        for (i in chars.indices) {
            if (!Character.isHighSurrogate(chars[i])) {
                var codePoint: Int
                var isSurrogatePair: Boolean
                if (Character.isLowSurrogate(chars[i])) {
                    if (i <= 0 || !Character.isSurrogatePair(chars[i - 1], chars[i])) {
                        continue
                    }
                    codePoint = Character.toCodePoint(chars[i - 1], chars[i])
                    isSurrogatePair = true
                } else {
                    codePoint = chars[i].toInt()
                    isSurrogatePair = false
                }
                val resId = getEmojiResource(codePoint)
                if (resId != 0) {
                    spannable.setSpan(
	                    EmojiEmotionSpan(context, resId, emojiSize, textSize),
                        if (isSurrogatePair) i - 1 else i, i + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
            }
        }
    }

    private fun getEmojiResource(codePoint: Int): Int {
        for (emoji in EmojiCollect.emojiCollect) {
            if (emoji.codePoint == codePoint) {
                return emoji.getImageResId()
            }
        }
        return 0
    }

    fun getEmojiEmotion(codePoint: Int): EmojiEmotion? {
        for (emoji in EmojiCollect.emojiCollect) {
            if (emoji.codePoint == codePoint) {
                return emoji
            }
        }
        return null
    }

    fun getEmojiEmotion(content: String): EmojiEmotion? {
        for (emoji in EmojiCollect.emojiCollect) {
            if (emoji.content == content) {
                return emoji
            }
        }
        return null
    }
}