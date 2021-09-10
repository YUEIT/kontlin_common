package cn.yue.base.common.widget.emoji

import android.text.TextUtils
import java.util.*


object Emoji {
    private var emojiEmotions: MutableList<EmojiEmotion>? = null

    fun getEmojiList(): MutableList<EmojiEmotion> {
        if (null == emojiEmotions) {
            emojiEmotions = ArrayList()
            val length = DATA.size
            for (i in 0 until length) {
                emojiEmotions!!.add(DATA[i])
            }
        }
        return emojiEmotions!!
    }


    private val DATA = arrayOf(

        EmojiEmotion.fromCodePoint(0x1f642),
        EmojiEmotion.fromCodePoint(0x1f616),
        EmojiEmotion.fromCodePoint(0x1f60d),
        EmojiEmotion.fromCodePoint(0x1f62e),
        EmojiEmotion.fromCodePoint(0x1f60e),
        EmojiEmotion.fromCodePoint(0x1f62d),
        EmojiEmotion.fromCodePoint(0x1f914),
        EmojiEmotion.fromCodePoint(0x1f910),
        EmojiEmotion.fromCodePoint(0x1f62a),
        EmojiEmotion.fromCodePoint(0x1f629),
        EmojiEmotion.fromCodePoint(0x1f628),
        EmojiEmotion.fromCodePoint(0x1f621),
        EmojiEmotion.fromCodePoint(0x1f61b),
        EmojiEmotion.fromCodePoint(0x1f62c),
        EmojiEmotion.fromCodePoint(0x1f627),
        EmojiEmotion.fromCodePoint(0x1f641),
        EmojiEmotion.fromCodePoint(0x1f624),
        EmojiEmotion.fromCodePoint(0x1f61e),
        EmojiEmotion.fromCodePoint(0x1f60a),
        EmojiEmotion.fromCodePoint(0x1f644),
        EmojiEmotion.fromCodePoint(0x1f60b),
        EmojiEmotion.fromCodePoint(0x1f635),
        EmojiEmotion.fromCodePoint(0x1f630),
        EmojiEmotion.fromCodePoint(0x1f613),
        EmojiEmotion.fromCodePoint(0x1f603),
        EmojiEmotion.fromCodePoint(0x1f608),
        EmojiEmotion.fromCodePoint(0x1f913),
        EmojiEmotion.fromCodePoint(0x1f632),
        EmojiEmotion.fromCodePoint(0x1f912),
        EmojiEmotion.fromCodePoint(0x1f635),
        EmojiEmotion.fromCodePoint(0x1f622),
        EmojiEmotion.fromCodePoint(0x1f61c),
        EmojiEmotion.fromCodePoint(0x1f61a),
        EmojiEmotion.fromCodePoint(0x1f636),
        EmojiEmotion.fromCodePoint(0x1f917),
        EmojiEmotion.fromCodePoint(0x1f609),
        EmojiEmotion.fromCodePoint(0x1f601),
        EmojiEmotion.fromCodePoint(0x1f637),
        EmojiEmotion.fromCodePoint(0x1f602),
        EmojiEmotion.fromCodePoint(0x1f61d),
        EmojiEmotion.fromCodePoint(0x1f633),
        EmojiEmotion.fromCodePoint(0x1f631),
        EmojiEmotion.fromCodePoint(0x1f614),
        EmojiEmotion.fromCodePoint(0x1f612),
        EmojiEmotion.fromCodePoint(0x1f60c),
        EmojiEmotion.fromCodePoint(0x1f60f),
        EmojiEmotion.fromCodePoint(0x1f643),
        EmojiEmotion.fromCodePoint(0x1f47d),
        EmojiEmotion.fromCodePoint(0x1f47b),
        EmojiEmotion.fromCodePoint(0x1f480),
        EmojiEmotion.fromCodePoint(0x1f31a),
        EmojiEmotion.fromCodePoint(0x1f31d),
        EmojiEmotion.fromCodePoint(0x1f4a4),
        EmojiEmotion.fromCodePoint(0x1f31e),
        EmojiEmotion.fromCodePoint(0x1f647),
        EmojiEmotion.fromCodePoint(0x1f64b),
        EmojiEmotion.fromCodePoint(0x1f646),
        EmojiEmotion.fromCodePoint(0x1f47e),
        EmojiEmotion.fromCodePoint(0x1f52a),
        EmojiEmotion.fromCodePoint(0x1f349),
        EmojiEmotion.fromCodePoint(0x1f37b),
        EmojiEmotion.fromCodePoint(0x2615),
        EmojiEmotion.fromCodePoint(0x1f437),
        EmojiEmotion.fromCodePoint(0x1f339),
        EmojiEmotion.fromCodePoint(0x1f48b),
        EmojiEmotion.fromCodePoint(0x2764),
        EmojiEmotion.fromCodePoint(0x1f494),
        EmojiEmotion.fromCodePoint(0x1f382),
        EmojiEmotion.fromCodePoint(0x1f4a3),
        EmojiEmotion.fromCodePoint(0x1f4a9),
        EmojiEmotion.fromCodePoint(0x1f469),
        EmojiEmotion.fromCodePoint(0x1f595),
        EmojiEmotion.fromCodePoint(0x1f44d),
        EmojiEmotion.fromCodePoint(0x1f44e),
        EmojiEmotion.fromCodePoint(0x1f44f),
        EmojiEmotion.fromCodePoint(0x270c),
        EmojiEmotion.fromCodePoint(0x1f918),
        EmojiEmotion.fromCodePoint(0x1f44a),
        EmojiEmotion.fromCodePoint(0x1f44c),
        EmojiEmotion.fromCodePoint(0x1f44b),
        EmojiEmotion.fromCodePoint(0x261d),
        EmojiEmotion.fromCodePoint(0x1f4aa),
        EmojiEmotion.fromCodePoint(0x1f64f),
        EmojiEmotion.fromCodePoint(0x1f388),
        EmojiEmotion.fromCodePoint(0x1f445),
        EmojiEmotion.fromCodePoint(0x1f389),
        EmojiEmotion.fromCodePoint(0x1f381),
        EmojiEmotion.fromCodePoint(0x1f436),
        EmojiEmotion.fromCodePoint(0x1f4b0),
        EmojiEmotion.fromCodePoint(0x1f3b5),
        EmojiEmotion.fromCodePoint(0x1f451)
    )


    /**
     * 判断字符串最后有emoji表情时返回其长度
     * @param s
     * @return
     */
    fun isEmojiLength(s: String?): Int {
        if (s == null) {
            return 0
        }
        val length: Int = s.length
        getEmojiList()
        if (length > 0 && null != emojiEmotions) {
            val size = emojiEmotions!!.size

            var last2: String? = null
            var last3: String? = null
            var last4: String? = null
            if (length >= 2) {
                last2 = s.substring(length - 2, length)
            }

            if (length >= 3) {
                last3 = s.substring(length - 3, length)
            }

            if (length >= 4) {
                last4 = s.substring(length - 4, length)
            }
            for (i in 0 until size) {
                val emojiEmotion: EmojiEmotion? = emojiEmotions!![i]
                val emojiStr: String? = emojiEmotion?.emoji
                if (null != emojiEmotion && !TextUtils.isEmpty(emojiStr)) {
                    if (emojiStr == last2) {
                        return 2
                    } else if (emojiStr == last3) {
                        return 3
                    } else if (emojiStr == last4) {
                        return 4
                    }
                }
            }
        }
        return 0
    }

}
