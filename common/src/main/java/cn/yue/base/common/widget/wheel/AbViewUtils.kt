package cn.yue.base.common.widget.wheel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.GridView
import android.widget.ListView

class AbViewUtils {

    companion object {
        /**
         * 描述：重置AbsListView的高度.
         * item 的最外层布局要用 RelativeLayout,如果计算的不准，就为RelativeLayout指定一个高度
         * @param absListView the abs list view
         * @param lineNumber 每行几个  ListView一行一个item
         * @param verticalSpace the vertical space
         */
        fun setAbsListViewHeight(absListView: AbsListView, lineNumber: Int, verticalSpace: Int) {

            val totalHeight = getAbsListViewHeight(absListView, lineNumber, verticalSpace)
            val params = absListView.layoutParams
            params.height = totalHeight
            (params as ViewGroup.MarginLayoutParams).setMargins(0, 0, 0, 0)
            absListView.layoutParams = params
        }

        /**
         * 描述：获取AbsListView的高度.
         * @param absListView the abs list view
         * @param lineNumber 每行几个  ListView一行一个item
         * @param verticalSpace the vertical space
         */
        fun getAbsListViewHeight(absListView: AbsListView, lineNumber: Int, verticalSpace: Int): Int {
            var totalHeight = 0
            val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            absListView.measure(w, h)
            val mListAdapter = absListView.adapter ?: return totalHeight

            val count = mListAdapter.count
            if (absListView is ListView) {
                for (i in 0 until count) {
                    val listItem = mListAdapter.getView(i, null, absListView)
                    listItem.measure(w, h)
                    totalHeight += listItem.measuredHeight
                }
                if (count == 0) {
                    totalHeight = verticalSpace
                } else {
                    totalHeight = totalHeight + absListView.dividerHeight * (count - 1)
                }

            } else if (absListView is GridView) {
                var remain = count % lineNumber
                if (remain > 0) {
                    remain = 1
                }
                if (mListAdapter.count == 0) {
                    totalHeight = verticalSpace
                } else {
                    val listItem = mListAdapter.getView(0, null, absListView)
                    listItem.measure(w, h)
                    val line = count / lineNumber + remain
                    totalHeight = line * listItem.measuredHeight + (line - 1) * verticalSpace
                }

            }
            return totalHeight

        }

        /**
         * 测量这个view，最后通过getMeasuredWidth()获取宽度和高度.
         *
         * @param v 要测量的view
         * @return 测量过的view
         */
        fun measureView(v: View?) {
            if (v == null) {
                return
            }
            val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(w, h)
        }

        /**
         * 描述：根据分辨率获得字体大小.
         *
         * @param screenWidth the screen width
         * @param screenHeight the screen height
         * @param textSize the text size
         * @return the int
         */
        fun resizeTextSize(screenWidth: Int, screenHeight: Int, textSize: Int): Int {
            var ratio = 1f
            try {
                val ratioWidth = screenWidth.toFloat() / 480
                val ratioHeight = screenHeight.toFloat() / 800
                ratio = Math.min(ratioWidth, ratioHeight)
            } catch (e: Exception) {
            }

            return Math.round(textSize * ratio)
        }

        /**
         *
         * 描述：dip转换为px
         * @param context
         * @param dipValue
         * @return
         * @throws
         */
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        /**
         *
         * 描述：px转换为dip
         * @param context
         * @param pxValue
         * @return
         * @throws
         */
        fun px2dip(context: Context?, pxValue: Float): Int {
            if (null == context) return pxValue.toInt()
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        fun sp2px(context: Context?, spValue: Float): Int {
            if (null == context) return spValue.toInt()
            val fontScale = context.resources.displayMetrics.density
            return (spValue * fontScale + 0.5f).toInt()
        }

    }

}