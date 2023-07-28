package cn.yue.base.binding.text

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.*
import android.text.style.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.base.utils.debug.LogUtils

object ViewAdapter {

    /**
     * textView 文字多样式设置，以string形式输入，默认“,” 分割；注意数组长度匹配
     * @param view
     * @param texts @{"aa,bb,cc"}  window上无法在xml使用中文，可指向variable中值，或者为空时，默认android:text值
     * @param textSplit texts分割符
     * @param textColors @{"#ff0000,#aaaaaa,#000000"}
     * @param textSizes @{"13,9,13"}
     * @param textStyles @{"normal,bold,italic"} or @{"0,1,2"}
     * @param lineStyles @{"null, center, under"}
     */
    @BindingAdapter(value = ["texts", "textSplit", "textColors", "textSizes", "textStyles", "lineStyles"], requireAll = false)
    fun setText(view: TextView, texts: String?, textSplit: String?, textColors: String?,
                textSizes: String?, textStyles: String?, lineStyles: String?) {
        val finalText = if (!TextUtils.isEmpty(texts)) {
            texts
        } else if (!TextUtils.isEmpty(view.text)) {
            view.text.toString()
        } else {
            ""
        }
        finalText?:return
        val split = if (TextUtils.isEmpty(textSplit)) "," else textSplit!!
        val textArray = finalText.split(split.toRegex()).toTypedArray()
        var textColorArray: Array<String?>? = null
        if (!TextUtils.isEmpty(textColors)) {
            textColorArray = textColors!!.replace("\\s*".toRegex(), "").split(",".toRegex()).toTypedArray()
            if (textColorArray.size != textArray.size) {
                LogUtils.e("color array is not match text array");
                return
            }
        }
        var textSizeArray: Array<String>? = null
        if (!TextUtils.isEmpty(textSizes)) {
            textSizeArray = textSizes!!.replace("\\s*".toRegex(), "").split(",".toRegex()).toTypedArray()
            if (textSizeArray.size != textArray.size) {
                LogUtils.e("size array is not match text array");
                return
            }
        }
        var textStyleArray: Array<String?>? = null
        if (!TextUtils.isEmpty(textStyles)) {
            textStyleArray = textStyles!!.replace("\\s*".toRegex(), "").split(",".toRegex()).toTypedArray()
            if (textStyleArray.size != textArray.size) {
                LogUtils.e("size array is not match text array");
                return
            }
        }
        var lineStyleArray: Array<String?>? = null
        if (!TextUtils.isEmpty(lineStyles)) {
            lineStyleArray = lineStyles!!.replace("\\s*".toRegex(), "").split(",".toRegex()).toTypedArray()
            if (lineStyleArray.size != textArray.size) {
                LogUtils.e("size array is not match text array");
                return
            }
        }
        val builder = StringBuilder()
        for (str in textArray) {
            builder.append(str)
        }
        val spannable: Spannable = SpannableString(builder.toString())
        var length = 0
        for (i in textArray.indices) {
            val str = textArray[i]
            val start = length
            val end = length + str.length
            if (textColorArray != null) {
                try {
                    val color = Color.parseColor(textColorArray[i])
                    val colorSpan = ForegroundColorSpan(color)
                    spannable.setSpan(colorSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
            if (textSizeArray != null) {
                try {
                    val textSize = textSizeArray[i].toInt()
                    val sizeSpan = AbsoluteSizeSpan(DisplayUtils.sp2dp(textSize), true)
                    spannable.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
            if (textStyleArray != null) {
                try {
                    val textStyle = textStyleArray[i]
                    var style: Int
                    style = when (textStyle) {
                        "0", "normal", "NORMAL" -> Typeface.NORMAL
                        "1", "bold", "BOLD" -> Typeface.BOLD
                        "2", "italic", "ITALIC" -> Typeface.ITALIC
                        else -> return
                    }
                    val styleSpan = StyleSpan(style)
                    spannable.setSpan(styleSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
            if (lineStyleArray != null) {
                try {
                    val lineStyle = lineStyleArray[i]
                    when (lineStyle) {
                        "1", "center" -> {
                            val strikethroughSpan = StrikethroughSpan()
                            spannable.setSpan(strikethroughSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                        "2", "under" -> {
                            val underlineSpan = UnderlineSpan()
                            spannable.setSpan(underlineSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                        else -> {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
            }
            length = end
        }
        view.text = spannable
    }

    @BindingAdapter(value = ["requestFocus"])
    @JvmStatic
    fun requestFocus(editText: EditText, requestFocus: Boolean) {
        if (requestFocus) {
            editText.setSelection(editText.text.length)
            editText.requestFocus()
            val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        editText.isFocusableInTouchMode = requestFocus
    }

    @BindingAdapter(value = ["clearFocus"])
    @JvmStatic
    fun clearFocus(editText: EditText, clear: Boolean) {
        if (clear) {
            editText.clearFocus()
        }
    }

    /**
     * EditText输入文字改变的监听
     */
    @BindingAdapter(value = ["onTextChanged"])
    @JvmStatic
    fun addTextChangedListener(editText: EditText, textChanged: (text: String) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(text: CharSequence, i: Int, i1: Int, i2: Int) {
                textChanged.invoke(text.toString())
            }
            override fun afterTextChanged(editable: Editable) {}
        })
    }
}