package cn.yue.base.common.widget.emoji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import java.lang.reflect.Field

/**
 * 介绍：大于最大行数数  显示全文
 * 作者：qianjujun
 * 邮箱：qianjujun@imcoming.com
 * 时间： 2016/11/14
 */
class MaxLineTextView : EmojiconTextView {
    private var textChange: Boolean = false
    private val text = SpannableStringBuilder()
    private var mOnClickListener: View.OnClickListener? = null

    internal var mMaximumField: Field? = null
    internal var mMaxModeField: Field? = null

    private// Maximum value
    // Maximum mode value
    val maxLinesCus: Int
        get() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                return maxLines
            } else {
                if (mMaximumField != null && mMaxModeField != null) {
                    mMaximumField!!.isAccessible = true
                    mMaxModeField!!.isAccessible = true
                    try {
                        val mMaximum = mMaximumField!!.getInt(text)
                        val mMaxMode = mMaxModeField!!.getInt(text)
                        return if (mMaxMode == 1) mMaximum else -1
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }

                }
            }
            return 0
        }

    constructor(context: Context) : super(context) {
        init(context)
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        try {
            mMaximumField = text.javaClass.getDeclaredField("mMaximum")
            mMaxModeField = text.javaClass.getDeclaredField("mMaxMode")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

    }


    override fun setText(text: CharSequence, type: TextView.BufferType) {
        textChange = true
        super.setText(text, type)
    }


    fun setText(text: CharSequence, allOnClik: View.OnClickListener) {
        setText(text)
        this.mOnClickListener = allOnClik
    }

    override fun onDraw(canvas: Canvas) {
        if ((TextUtils.isEmpty(getText()) || text != null) && !textChange) {
            super.onDraw(canvas)
            return
        }
        var maxLines = 0
        maxLines = maxLinesCus

        if (maxLines == 0) {
            super.onDraw(canvas)
            return
        }
        val paint = paint
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight

        val bufferWidth = paint.textSize.toInt() * 3//缓冲区长度，空出三个字符的长度来给最后的省略号和全文

        // 计算出最大行文字所能显示的长度
        val availableTextWidth = (width - paddingLeft - paddingRight) * maxLines - bufferWidth


        // 根据长度截取出剪裁后的文字
        val ellipsizeStr = TextUtils.ellipsize(getText(), paint, availableTextWidth.toFloat(), TextUtils.TruncateAt.END)


        if (TextUtils.equals(ellipsizeStr, getText())) {
            super.onDraw(canvas)
            textChange = false
            return
        }


        text.clear()
        text.append(ellipsizeStr)
        text.append("全文")
        text.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (mOnClickListener != null) {
                    mOnClickListener!!.onClick(widget)
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val color = Color.parseColor("#3c6c9f")
                ds.color = color
                ds.isUnderlineText = false
            }
        }, ellipsizeStr.length, ellipsizeStr.length + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setText(text)
        textChange = false
        super.onDraw(canvas)
    }


}
