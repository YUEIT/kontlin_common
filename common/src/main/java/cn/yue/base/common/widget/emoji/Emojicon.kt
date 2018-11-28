/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yue.base.common.widget.emoji

import cn.yue.base.common.widget.keyboard.mode.IEmotion
import java.io.Serializable

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class Emojicon() : Serializable, IEmotion {
    var icon: Int = 0
        private set
    var value: Char = ' '
        private set
    var emoji: String? = null   // http: gif
    var type = 0
    var content: String? = null

    override fun equals(o: Any?): Boolean {
        return o is Emojicon && emoji == o.emoji
    }

    override fun hashCode(): Int {
        return emoji!!.hashCode()
    }

    override fun getImageResId(): Int {
        return icon
    }

    override fun getImageUrl(): String {
        return emoji?:""
    }

    companion object {


        private const val serialVersionUID = 1L


        fun fromResource(icon: Int, value: Int): Emojicon {
            val emoji = Emojicon()
            emoji.icon = icon
            emoji.value = value.toChar()
            return emoji
        }

        fun fromCodePoint(codePoint: Int): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = newString(codePoint)
            emoji.icon = getRid(codePoint, -1)
            emoji.type = 0
            return emoji
        }

        fun fromChar(ch: Char): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = Character.toString(ch)
            return emoji
        }

        fun fromChars(chars: String): Emojicon {
            val emoji = Emojicon()
            emoji.emoji = chars
            return emoji
        }

        fun newString(codePoint: Int): String {
            return String(Character.toChars(codePoint))
            //2017 09 23 add by zhangxutnog 宋博的需求 表情包全部更换
            /*        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }*/
        }


        fun getRid(emoji: Int, notFindId: Int): Int {
            return EmojiconHandler.getEmojiResId(emoji, notFindId)
        }
    }
}
