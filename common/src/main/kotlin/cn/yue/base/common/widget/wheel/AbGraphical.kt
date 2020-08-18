package cn.yue.base.common.widget.wheel

import android.graphics.Canvas
import android.text.Layout
import android.text.TextPaint
import java.util.*

class AbGraphical {


    companion object {

        /**
         * 描述：获取字符的所在位置（按像素获取最大能容纳的）.
         *
         * @param str 指定的字符串
         * @param maxPix 要取到的位置（像素）
         * @param paint the paint
         * @return 字符的所在位置
         */
        fun subStringLength(str: String, maxPix: Int, paint: TextPaint): Int {
            if (AbStrUtils.isEmpty(str)) {
                return 0
            }
            var currentIndex = 0
            for (i in 0 until str.length) {
                //获取一个字符
                val temp = str.substring(0, i + 1)
                val valueLength = paint.measureText(temp)
                if (valueLength > maxPix) {
                    currentIndex = i - 1
                    break
                } else if (valueLength == maxPix.toFloat()) {
                    currentIndex = i
                    break
                }
            }
            //短于最大像素返回最后一个字符位置
            if (currentIndex == 0) {
                currentIndex = str.length - 1
            }
            return currentIndex
        }

        /**
         * 描述：获取文字的像素宽.
         *
         * @param str the str
         * @param paint the paint
         * @return the string width
         */
        fun getStringWidth(str: String, paint: TextPaint): Float {
            val strWidth = paint.measureText(str)
            return strWidth
        }

        /**
         * Return how wide a layout must be in order to display
         * the specified text with one line per paragraph.
         * @param str the str
         * @param paint the paint
         * @return the string width2
         */
        fun getDesiredWidth(str: String, paint: TextPaint): Float {
            val strWidth = Layout.getDesiredWidth(str, paint)
            return strWidth
        }

        /**
         * Gets the draw row string.
         * @param text the text
         * @param maxWPix the max w pix
         * @param paint the paint
         * @return the draw row count
         */
        fun getDrawRowStr(text: String, maxWPix: Int, paint: TextPaint): List<String> {
            var texts: Array<String?>? = null
            if (text.indexOf("\n") != -1) {
                texts = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            } else {
                texts = arrayOfNulls(1)
                texts[0] = text
            }
            //共多少行
            val mStrList = ArrayList<String>()

            for (i in texts.indices) {
                var textLine = texts[i]
                //计算这个文本显示为几行
                while (true) {
                    //可容纳的最后一个字的位置
                    val endIndex = subStringLength(textLine!!, maxWPix, paint)
                    if (endIndex <= 0) {
                        mStrList.add(textLine)
                    } else {
                        if (endIndex == textLine.length - 1) {
                            mStrList.add(textLine)
                        } else {
                            mStrList.add(textLine.substring(0, endIndex + 1))
                        }

                    }
                    //获取剩下的
                    if (textLine.length > endIndex + 1) {
                        //还有剩下的
                        textLine = textLine.substring(endIndex + 1)
                    } else {
                        break
                    }
                }
            }

            return mStrList
        }

        /**
         * Gets the draw row count.
         * @param text the text
         * @param maxWPix the max w pix
         * @param paint the paint
         * @return the draw row count
         */
        fun getDrawRowCount(text: String, maxWPix: Int, paint: TextPaint): Int {
            var texts: Array<String?>? = null
            if (text.indexOf("\n") != -1) {
                texts = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            } else {
                texts = arrayOfNulls(1)
                texts[0] = text
            }
            //共多少行
            val mStrList = ArrayList<String>()

            for (i in texts.indices) {
                var textLine = texts[i]
                //计算这个文本显示为几行
                while (true) {
                    //可容纳的最后一个字的位置
                    val endIndex = subStringLength(textLine!!, maxWPix, paint)
                    if (endIndex <= 0) {
                        mStrList.add(textLine)
                    } else {
                        if (endIndex == textLine.length - 1) {
                            mStrList.add(textLine)
                        } else {
                            mStrList.add(textLine.substring(0, endIndex + 1))
                        }

                    }
                    //获取剩下的
                    if (textLine.length > endIndex + 1) {
                        //还有剩下的
                        textLine = textLine.substring(endIndex + 1)
                    } else {
                        break
                    }
                }
            }

            return mStrList.size
        }

        /**
         * 描述：绘制文本，支持换行.
         *
         * @param canvas the canvas
         * @param text the text
         * @param maxWPix the max w pix
         * @param paint the paint
         * @param left the left
         * @param top the top
         * @return the int
         */
        fun drawText(canvas: Canvas, text: String, maxWPix: Int, paint: TextPaint, left: Int, top: Int): Int {
            if (AbStrUtils.isEmpty(text)) {
                return 1
            }
            //需要根据文字长度控制换行
            //测量文字的长度
            val mStrList = getDrawRowStr(text, maxWPix, paint)

            val fm = paint.fontMetrics
            val hSize = Math.ceil((fm.descent - fm.ascent).toDouble()).toInt() + 2

            for (i in mStrList.indices) {
                //计算坐标
                val x = left
                val y = top + hSize / 2 + hSize * i
                val textLine = mStrList[i]
                canvas.drawText(textLine, x.toFloat(), y.toFloat(), paint)

            }
            return mStrList.size
        }
    }
}
