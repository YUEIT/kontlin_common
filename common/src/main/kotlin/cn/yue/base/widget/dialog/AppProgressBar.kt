package cn.yue.base.widget.dialog

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import cn.yue.base.R

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class AppProgressBar(context: Context, attributeSet: AttributeSet)
    : RelativeLayout(context, attributeSet) {

    private lateinit var circleView: CircleImageView
    private lateinit var progressDrawable: MaterialProgressDrawable
    private var isLoading: Boolean = false

    private var progressColors: IntArray = intArrayOf (
            R.color.progress_color_1,
            R.color.progress_color_2,
            R.color.progress_color_3,
            R.color.progress_color_4
    )

    init {
        initView()
    }

    private fun initView() {
        circleView = CircleImageView(context, -0x50506, 40 / 2f)
        progressDrawable = MaterialProgressDrawable(context, this)
        progressDrawable.setBackgroundColor(-0x50506)
        circleView.setImageDrawable(progressDrawable)
        circleView.visibility = View.VISIBLE
        progressDrawable.alpha = 255
        val res = resources
        val colorRes = IntArray(progressColors.size)
        for (i in progressColors.indices) {
            colorRes[i] = res.getColor(progressColors[i])
        }
        progressDrawable.setColorSchemeColors(*colorRes)
        setProgressBarBackgroundColor(Color.WHITE)
        addView(circleView)
        startAnimation()
    }

    fun setColorSchemeResources(vararg colors: Int) {
        val res = resources
        val colorRes = IntArray(colors.size)
        for (i in colors.indices) {
            colorRes[i] = res.getColor(colors[i])
        }
        progressDrawable.setColorSchemeColors(*colorRes)
    }

    fun setProgressBarBackgroundColor(color: Int) {
        circleView.setBackgroundColor(color)
    }

    fun startAnimation() {
        progressDrawable.start()
        isLoading = true
    }

    fun stopAnimation() {
        progressDrawable.stop()
        isLoading = false
    }

    fun setInnerVisibility(innerVisibility: Int) {
        circleView.visibility = innerVisibility
        if (innerVisibility == View.VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    fun isLoading(): Boolean {
        return isLoading
    }
}