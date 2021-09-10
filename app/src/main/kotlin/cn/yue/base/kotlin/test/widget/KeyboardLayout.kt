package cn.yue.base.kotlin.test.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import cn.yue.base.common.widget.emoji.EmojiEmotion
import cn.yue.base.common.widget.emoji.EmojiEmotionEditText
import cn.yue.base.common.widget.keyboard.BaseKeyboardLayout
import cn.yue.base.common.widget.keyboard.EmotionLayout
import cn.yue.base.kotlin.test.R

class KeyboardLayout(context: Context, attrs: AttributeSet?)
    : BaseKeyboardLayout(context, attrs) {

    override fun getLayoutId(): Int {
        return R.layout.layout_keyboard
    }

    override fun getEmotionLayout(): EmotionLayout {
        return findViewById(R.id.emotion)
    }

    override fun initView(context: Context) {
        super.initView(context)
        val inputET = findViewById<EmojiEmotionEditText>(R.id.inputET)
        findViewById<TextView>(R.id.changeTV).setOnClickListener {
            getEmotionLayout().toggleEmotionShow(inputET)
        }
        getEmotionLayout().setOnEmotionClickListener {
            if (it is EmojiEmotion) {
                inputET.addEmojiEmotion(it)
            }
        }
    }

}