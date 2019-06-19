package cn.yue.base.common.widget.dialog

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import cn.yue.base.common.R

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class AppProgressBar(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {

    private val CIRCLE_DIAMETER = 40
    private val CIRCLE_BG_LIGHT = -0x50506
    private var circleView: CircleImageView? = null
    private var progressDrawable: MaterialProgressDrawable? = null
    private var isLoading: Boolean = false



    var MD_RF_COLOR: IntArray? = intArrayOf(R.color.progress_color_1, R.color.progress_color_2, R.color.progress_color_3, R.color.progress_color_4)

    init {
        circleView = CircleImageView(context, CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2f)
        progressDrawable = MaterialProgressDrawable(context, this)
        progressDrawable!!.setBackgroundColor(CIRCLE_BG_LIGHT)
        circleView!!.setImageDrawable(progressDrawable)
        circleView!!.setVisibility(View.VISIBLE)
        progressDrawable!!.setAlpha(255)
        if (null != MD_RF_COLOR) {
            val res = resources
            val colorRes = IntArray(MD_RF_COLOR!!.size)
            for (i in MD_RF_COLOR!!.indices) {
                colorRes[i] = res.getColor(MD_RF_COLOR!![i])
            }
            progressDrawable!!.setColorSchemeColors(*colorRes)
        }
        setProgressBarBackgroundColor(Color.WHITE)
        addView(circleView)
        startAnimation()
    }


    fun setColorSchemeResources(vararg colors: Int) {
        if (null != progressDrawable) {
            val res = resources
            val colorRes = IntArray(colors.size)
            for (i in colors.indices) {
                colorRes[i] = res.getColor(colors[i])
            }
            progressDrawable!!.setColorSchemeColors(*colorRes)
        }
    }

    fun setProgressBarBackgroundColor(color: Int) {
        if (null != circleView) {
            circleView!!.setBackgroundColor(color)
        }
    }

    fun startAnimation() {
        if (null != progressDrawable) {
            progressDrawable!!.start()
            isLoading = true
        }
    }

    fun stopAnimation() {
        if (null != progressDrawable) {
            progressDrawable!!.stop()
            isLoading = false
        }
    }

    fun setInnerVisibility(innerVisibility: Int) {
        if (null != circleView) {
            circleView!!.setVisibility(innerVisibility)
            if (innerVisibility == View.VISIBLE) {
                startAnimation()
            } else {
                stopAnimation()
            }
        }
    }

    fun isLoading(): Boolean {
        return isLoading
    }
}