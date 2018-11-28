package cn.yue.base.common.widget.emoji

import android.text.TextUtils
import java.util.*

/**
 * 作者：xjzhao
 * 时间：2015-04-02 下午5:39
 */
class Emoji {

    companion object {
        private var emojicons: MutableList<Emojicon>? = null

        fun getEmojiList(): MutableList<Emojicon> {
                if (null == emojicons) {
                    emojicons = ArrayList()
                    val length = DATA.size
                    for (i in 0 until length) {
                        emojicons!!.add(DATA[i])
                    }
                }
                return emojicons!!
            }


        private val DATA = arrayOf(
                //2017 09 23 add by zhangxutnog 宋博的需求 表情包全部更换
                Emojicon.fromCodePoint(0x1f642), Emojicon.fromCodePoint(0x1f616), Emojicon.fromCodePoint(0x1f60d), Emojicon.fromCodePoint(0x1f62e), Emojicon.fromCodePoint(0x1f60e), Emojicon.fromCodePoint(0x1f62d), Emojicon.fromCodePoint(0x1f914), Emojicon.fromCodePoint(0x1f910), Emojicon.fromCodePoint(0x1f62a), Emojicon.fromCodePoint(0x1f629), Emojicon.fromCodePoint(0x1f628), Emojicon.fromCodePoint(0x1f621), Emojicon.fromCodePoint(0x1f61b), Emojicon.fromCodePoint(0x1f62c), Emojicon.fromCodePoint(0x1f627), Emojicon.fromCodePoint(0x1f641), Emojicon.fromCodePoint(0x1f624), Emojicon.fromCodePoint(0x1f61e), Emojicon.fromCodePoint(0x1f60a), Emojicon.fromCodePoint(0x1f644), Emojicon.fromCodePoint(0x1f60b), Emojicon.fromCodePoint(0x1f635), Emojicon.fromCodePoint(0x1f630), Emojicon.fromCodePoint(0x1f613), Emojicon.fromCodePoint(0x1f603), Emojicon.fromCodePoint(0x1f608), Emojicon.fromCodePoint(0x1f913), Emojicon.fromCodePoint(0x1f632), Emojicon.fromCodePoint(0x1f912), Emojicon.fromCodePoint(0x1f635), Emojicon.fromCodePoint(0x1f622), Emojicon.fromCodePoint(0x1f61c), Emojicon.fromCodePoint(0x1f61a), Emojicon.fromCodePoint(0x1f636), Emojicon.fromCodePoint(0x1f917), Emojicon.fromCodePoint(0x1f609), Emojicon.fromCodePoint(0x1f601), Emojicon.fromCodePoint(0x1f637), Emojicon.fromCodePoint(0x1f602), Emojicon.fromCodePoint(0x1f61d), Emojicon.fromCodePoint(0x1f633), Emojicon.fromCodePoint(0x1f631), Emojicon.fromCodePoint(0x1f614), Emojicon.fromCodePoint(0x1f612), Emojicon.fromCodePoint(0x1f60c), Emojicon.fromCodePoint(0x1f60f), Emojicon.fromCodePoint(0x1f643), Emojicon.fromCodePoint(0x1f47d), Emojicon.fromCodePoint(0x1f47b), Emojicon.fromCodePoint(0x1f480), Emojicon.fromCodePoint(0x1f31a), Emojicon.fromCodePoint(0x1f31d), Emojicon.fromCodePoint(0x1f4a4), Emojicon.fromCodePoint(0x1f31e), Emojicon.fromCodePoint(0x1f647), Emojicon.fromCodePoint(0x1f64b), Emojicon.fromCodePoint(0x1f646), Emojicon.fromCodePoint(0x1f47e), Emojicon.fromCodePoint(0x1f52a), Emojicon.fromCodePoint(0x1f349), Emojicon.fromCodePoint(0x1f37b), Emojicon.fromCodePoint(0x2615), Emojicon.fromCodePoint(0x1f437), Emojicon.fromCodePoint(0x1f339), Emojicon.fromCodePoint(0x1f48b), Emojicon.fromCodePoint(0x2764), Emojicon.fromCodePoint(0x1f494), Emojicon.fromCodePoint(0x1f382), Emojicon.fromCodePoint(0x1f4a3), Emojicon.fromCodePoint(0x1f4a9), Emojicon.fromCodePoint(0x1f469), Emojicon.fromCodePoint(0x1f595), Emojicon.fromCodePoint(0x1f44d), Emojicon.fromCodePoint(0x1f44e), Emojicon.fromCodePoint(0x1f44f), Emojicon.fromCodePoint(0x270c), Emojicon.fromCodePoint(0x1f918), Emojicon.fromCodePoint(0x1f44a), Emojicon.fromCodePoint(0x1f44c), Emojicon.fromCodePoint(0x1f44b), Emojicon.fromCodePoint(0x261d), Emojicon.fromCodePoint(0x1f4aa), Emojicon.fromCodePoint(0x1f64f), Emojicon.fromCodePoint(0x1f388), Emojicon.fromCodePoint(0x1f445), Emojicon.fromCodePoint(0x1f389), Emojicon.fromCodePoint(0x1f381), Emojicon.fromCodePoint(0x1f436), Emojicon.fromCodePoint(0x1f4b0), Emojicon.fromCodePoint(0x1f3b5), Emojicon.fromCodePoint(0x1f451))/*
// People
            Emojicon.fromCodePoint(0x1f604),
            Emojicon.fromCodePoint(0x1f603),
            Emojicon.fromCodePoint(0x1f60a),
            Emojicon.fromCodePoint(0x1f609),
            Emojicon.fromCodePoint(0x1f60d),
            Emojicon.fromCodePoint(0x1f618),
            Emojicon.fromCodePoint(0x1f61a),
            Emojicon.fromCodePoint(0x1f61c),
            Emojicon.fromCodePoint(0x1f61d),
            Emojicon.fromCodePoint(0x1f633),
            Emojicon.fromCodePoint(0x1f601),
            Emojicon.fromCodePoint(0x1f614),
            Emojicon.fromCodePoint(0x1f60c),
            Emojicon.fromCodePoint(0x1f61e),
            Emojicon.fromCodePoint(0x1f623),
            Emojicon.fromCodePoint(0x1f622),
            Emojicon.fromCodePoint(0x1f602),
            Emojicon.fromCodePoint(0x1f62d),
            Emojicon.fromCodePoint(0x1f62a),
            Emojicon.fromCodePoint(0x1f625),
            Emojicon.fromCodePoint(0x1f630),
            Emojicon.fromCodePoint(0x1f613),
            Emojicon.fromCodePoint(0x1f628),
            Emojicon.fromCodePoint(0x1f631),
            Emojicon.fromCodePoint(0x1f621),
            Emojicon.fromCodePoint(0x1f616),
            Emojicon.fromCodePoint(0x1f637),
            Emojicon.fromCodePoint(0x1f60e),
            Emojicon.fromCodePoint(0x1f634),
            Emojicon.fromCodePoint(0x1f632),
            Emojicon.fromCodePoint(0x1f47f),
            Emojicon.fromCodePoint(0x1f607),
            Emojicon.fromCodePoint(0x1f60f),
            Emojicon.fromCodePoint(0x1f47c),
            Emojicon.fromCodePoint(0x1f47d),
//            Emojicon.fromCodePoint(0x2728),
            Emojicon.fromCodePoint(0x1f4a2),
            Emojicon.fromCodePoint(0x1f4a6),
            Emojicon.fromCodePoint(0x1f4a4),
            Emojicon.fromCodePoint(0x1f44d),
            Emojicon.fromCodePoint(0x1f44c),
//            Emojicon.fromCodePoint(0x270a),
//            Emojicon.fromCodePoint(0x270c),
            Emojicon.fromCodePoint(0x1f64f),
            Emojicon.fromCodePoint(0x1f44f),
            Emojicon.fromCodePoint(0x1f4aa),
            Emojicon.fromCodePoint(0x1f451),
            Emojicon.fromCodePoint(0x1f302),
//            Emojicon.fromCodePoint(0x2764),
            Emojicon.fromCodePoint(0x1f494),
            Emojicon.fromCodePoint(0x1f48b),
            Emojicon.fromCodePoint(0x1f48d),
            Emojicon.fromCodePoint(0x1f463),
            Emojicon.fromCodePoint(0x1f490),
            Emojicon.fromCodePoint(0x1f338),
            Emojicon.fromCodePoint(0x1f339),
//            Emojicon.fromCodePoint(0x2b50),

            // Objects
            Emojicon.fromCodePoint(0x1f47b),
            Emojicon.fromCodePoint(0x1f381),
            Emojicon.fromCodePoint(0x1f389),
            Emojicon.fromCodePoint(0x1f388),
            Emojicon.fromCodePoint(0x1f4e2),
//            Emojicon.fromCodePoint(0x1f3a4),
            Emojicon.fromCodePoint(0x1f3b5),
//            Emojicon.fromCodePoint(0x1f3b6),
//            Emojicon.fromCodePoint(0x1f3ae),
            Emojicon.fromCodePoint(0x1f37b),
            Emojicon.fromCodePoint(0x1f382),
            Emojicon.fromCodePoint(0x1f36d),

            // Places
//            Emojicon.fromCodePoint(0xe513)


            //社区特殊字符
            Emojicon.fromCodePoint(0x2f001),
            Emojicon.fromCodePoint(0x2f002)*/


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
            if (length > 0 && null != emojicons) {
                val size = emojicons!!.size

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
                    val emojicon: Emojicon? = emojicons!![i]
                    val emojiStr: String? = emojicon?.emoji
                    if (null != emojicon && !TextUtils.isEmpty(emojiStr)) {
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

}
