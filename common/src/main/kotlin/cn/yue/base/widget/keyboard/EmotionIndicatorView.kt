package cn.yue.base.widget.keyboard

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import cn.yue.base.common.R
import java.util.*

class EmotionIndicatorView(protected var mContext: Context, attrs: AttributeSet) : LinearLayout(mContext, attrs) {
    protected var mImageViews: ArrayList<ImageView>? = null
    protected var mBmpSelect: Bitmap
    protected var mBmpNormal: Bitmap
    protected var mPlayToAnimatorSet: AnimatorSet? = null
    protected var mPlayByInAnimatorSet: AnimatorSet? = null
    protected var mPlayByOutAnimatorSet: AnimatorSet? = null
    protected var layoutParams: LinearLayout.LayoutParams

    init {
        this.orientation = LinearLayout.HORIZONTAL

        mBmpSelect = BitmapFactory.decodeResource(resources, R.drawable.indicator_emotion_select)
        mBmpNormal = BitmapFactory.decodeResource(resources, R.drawable.indicator_emotion_normal)

        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER_VERTICAL
        layoutParams.setMargins(10, 0, 10, 0)
    }

    //移动到的位置
    fun playTo(position: Int, count: Int) {
        updateIndicatorCount(count)

        for (iv in mImageViews!!) {
            iv.setImageBitmap(mBmpNormal)
        }

        mImageViews!![position].setImageBitmap(mBmpSelect)
        val imageViewStrat = mImageViews!![position]
        val animIn1 = ObjectAnimator.ofFloat(imageViewStrat, "scaleX", 0.25f, 1.0f)
        val animIn2 = ObjectAnimator.ofFloat(imageViewStrat, "scaleY", 0.25f, 1.0f)

        if (mPlayToAnimatorSet != null && mPlayToAnimatorSet!!.isRunning) {
            mPlayToAnimatorSet!!.cancel()
            mPlayToAnimatorSet = null
        }
        mPlayToAnimatorSet = AnimatorSet()
        mPlayToAnimatorSet!!.play(animIn1).with(animIn2)
        mPlayToAnimatorSet!!.duration = 100
        mPlayToAnimatorSet!!.start()
    }

    //从上一个位置滚动到下一个位置
    fun playBy(startPosition: Int, nextPosition: Int, count: Int) {
        var startPosition = startPosition
        var nextPosition = nextPosition

        updateIndicatorCount(count)

        var isShowInAnimOnly = false
        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            nextPosition = 0
            startPosition = nextPosition
        }

        if (startPosition < 0) {
            isShowInAnimOnly = true
            nextPosition = 0
            startPosition = nextPosition
        }

        val imageViewStrat = mImageViews!![startPosition]
        val imageViewNext = mImageViews!![nextPosition]

        val anim1 = ObjectAnimator.ofFloat(imageViewStrat, "scaleX", 1.0f, 0.25f)
        val anim2 = ObjectAnimator.ofFloat(imageViewStrat, "scaleY", 1.0f, 0.25f)

        if (mPlayByOutAnimatorSet != null && mPlayByOutAnimatorSet!!.isRunning) {
            mPlayByOutAnimatorSet!!.cancel()
            mPlayByOutAnimatorSet = null
        }
        mPlayByOutAnimatorSet = AnimatorSet()
        mPlayByOutAnimatorSet!!.play(anim1).with(anim2)
        mPlayByOutAnimatorSet!!.duration = 100

        val animIn1 = ObjectAnimator.ofFloat(imageViewNext, "scaleX", 0.25f, 1.0f)
        val animIn2 = ObjectAnimator.ofFloat(imageViewNext, "scaleY", 0.25f, 1.0f)

        if (mPlayByInAnimatorSet != null && mPlayByInAnimatorSet!!.isRunning) {
            mPlayByInAnimatorSet!!.cancel()
            mPlayByInAnimatorSet = null
        }
        mPlayByInAnimatorSet = AnimatorSet()
        mPlayByInAnimatorSet!!.play(animIn1).with(animIn2)
        mPlayByInAnimatorSet!!.duration = 100

        if (isShowInAnimOnly) {
            mPlayByInAnimatorSet!!.start()
            return
        }

        anim1.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                imageViewStrat.setImageBitmap(mBmpNormal)
                val animFil1l = ObjectAnimator.ofFloat(imageViewStrat, "scaleX", 1.0f)
                val animFill2 = ObjectAnimator.ofFloat(imageViewStrat, "scaleY", 1.0f)
                val mFillAnimatorSet = AnimatorSet()
                mFillAnimatorSet.play(animFil1l).with(animFill2)
                mFillAnimatorSet.start()
                imageViewNext.setImageBitmap(mBmpSelect)
                mPlayByInAnimatorSet!!.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        mPlayByOutAnimatorSet!!.start()
    }

    protected fun updateIndicatorCount(count: Int) {
        if (mImageViews == null) {
            mImageViews = ArrayList()
        }
        if (count > mImageViews!!.size) {
            for (i in mImageViews!!.size until count) {
                val imageView = ImageView(mContext)
                imageView.setImageBitmap(if (i == 0) mBmpSelect else mBmpNormal)
                this.addView(imageView, layoutParams)
                mImageViews!!.add(imageView)
            }
        }
        for (i in mImageViews!!.indices) {
            if (i >= count) {
                mImageViews!![i].visibility = View.GONE
            } else {
                mImageViews!![i].visibility = View.VISIBLE
            }
        }
    }

    companion object {

        private val MARGIN_LEFT = 4
    }
}
