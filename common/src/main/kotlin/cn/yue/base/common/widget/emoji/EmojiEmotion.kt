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

class EmojiEmotion() : Serializable, IEmotion {

    constructor(codePoint: Int, icon: Int, content: String): this() {
        this.codePoint = codePoint
        this.icon = icon
        this.content = content
    }

    var icon: Int = 0
    var content: String = "[表情]"
    var codePoint: Int = 0
    var emoji: String = ""   // http: gif
    var type = 0

    override fun equals(o: Any?): Boolean {
        return o is EmojiEmotion && emoji == o.emoji
    }

    override fun hashCode(): Int {
        return emoji.hashCode()
    }

    override fun getImageResId(): Int {
        return icon
    }

    override fun getImageUrl(): String {
        return emoji
    }

    companion object {

        fun fromCodePoint(codePoint: Int): EmojiEmotion {
            val emoji = EmojiEmotion()
            val emotion = EmojiEmotionHandler.getEmojiEmotion(codePoint)
            emotion?.let {
                emoji.icon = it.icon
                emoji.content = it.content
                emoji.codePoint = it.codePoint
                emoji.type = 0
            }
            return emoji
        }

        fun fromContent(fromContent: String): EmojiEmotion {
            val emoji = EmojiEmotion()
            val emotion = EmojiEmotionHandler.getEmojiEmotion(fromContent)
            emotion?.let {
                emoji.icon = it.icon
                emoji.content = it.content
                emoji.codePoint = it.codePoint
                emoji.type = 0
            }
            return emoji
        }

        fun newString(codePoint: Int): String {
            return String(Character.toChars(codePoint))
        }

    }
}
