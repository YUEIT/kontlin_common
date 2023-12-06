package cn.yue.test.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.code.hasValue
import cn.yue.test.R
import cn.yue.test.mode.LiveGiftComboData
import java.util.concurrent.ConcurrentHashMap

class ComboGiftQueueLayout(context: Context, attributeSet: AttributeSet? = null)
    : LinearLayout(context, attributeSet) {

    private val maxShowTime = 3000L
    private val maxSize = 2
    private val cacheCombos = ConcurrentHashMap<String, LiveGiftComboData>()
    private val maxLoopTime = 500L
    private val queueComboMessages = ArrayList<String>()
    private val loopMessageId = 1
    private val loopMyselfMessageId = 2
    private val removeMessageId = 3

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                loopMessageId -> {
                    for (i in 0 until Math.min(queueComboMessages.size, maxSize)) {
                        val giftUniqueId = queueComboMessages.last()
                        cacheCombos[giftUniqueId]?.let {
                            addComboGift(it)
                        }
                    }
                    queueComboMessages.clear()
                }
                loopMyselfMessageId -> {
                    val giftUniqueId = msg.obj as String
                    cacheCombos[giftUniqueId]?.let {
                        addComboGift(it)
                    }
                }
                removeMessageId -> {
                    val giftUniqueId = msg.obj as String
                    val comboItem = cacheCombos[giftUniqueId]
                    if (comboItem != null && Math.abs(System.currentTimeMillis() - comboItem.lastComboTime) >= maxShowTime) {
                        val view = getShowingView(giftUniqueId)
                        if (view != null) {
                            removeView(view)
                        }
                        cacheCombos.remove(giftUniqueId)
                    }
                }
                else -> {}
            }
        }
    }

    private fun getGiftUniqueId(liveGiftNotice: LiveGiftComboData): String {
        return "${liveGiftNotice.giftId}_${liveGiftNotice.comboId}"
    }

    private fun isMyselfCombo(userId: Int): Boolean {
        return userId == 13
    }

    /**
     * 加个时间延时的机制，剥离自己送礼的延时
     */
    fun queueComboMessage(comboData: LiveGiftComboData) {
        val giftUniqueId = getGiftUniqueId(comboData)
        cacheCombos[giftUniqueId] = comboData.apply { lastComboTime = System.currentTimeMillis() }
//        handler.sendMessageDelayed(Message.obtain(handler, removeMessageId, giftUniqueId), maxShowTime)
        if (isMyselfCombo(comboData.iUserId)) {
            handler.removeMessages(loopMyselfMessageId)
            handler.sendMessage(Message.obtain(handler, loopMyselfMessageId, giftUniqueId))
        } else {
            queueComboMessages.add(giftUniqueId)
            if (!handler.hasMessages(loopMessageId)) {
                handler.sendMessageDelayed(
                    Message.obtain(handler, loopMessageId, giftUniqueId),
                    maxLoopTime
                )
            }
        }
    }

    fun addComboGift(comboData: LiveGiftComboData) {
        val giftUniqueId = getGiftUniqueId(comboData)
        var giftView = getShowingView(giftUniqueId)
        //是否已经在窗口内显示了
        val isAddToParent = giftView == null
        if (isAddToParent) {
            giftView = LayoutInflater.from(context).inflate(R.layout.layout_gift_combo_item, null)
            giftView?.setTag(R.id.id_live_gift_combo_view, giftUniqueId)
        }
        if (giftView == null) {
            return
        }
        //更新
        cacheCombos[giftUniqueId] = comboData.apply { lastComboTime = System.currentTimeMillis() }

        if (isAddToParent) {
            addComboView(giftView)
            showComboAnimation(giftView, comboData)
            showAddGiftAnimation(giftView)
        } else {
            showComboAnimation(giftView, 1)
        }
//        handler.sendMessageDelayed(Message.obtain(handler, removeMessageId, giftUniqueId), maxShowTime)
    }

    private fun addComboView(giftView: View) {
        if (childCount >= maxSize) {
            val replaceIndex = findReplaceIndexOfSpace()
            val reMoveView: View = getChildAt(replaceIndex)
            removeView(reMoveView)
            addView(giftView, replaceIndex)
        } else {
            addView(giftView)
        }
    }

    private fun showComboAnimation(giftView: View, comboData: LiveGiftComboData) {
        giftView.findViewById<TextView>(R.id.tv_user_name)?.text = "${comboData.giftId}"
        giftView.findViewById<TextView>(R.id.tv_combo)?.apply {
            val startColor = Color.parseColor("#fff734")
            val centerColor = Color.parseColor("#fff734")
            val endColor = Color.parseColor("#f9ba28")
            val shader = LinearGradient(0f, 0f, 0f, DisplayUtils.dp2px(25f)
                , intArrayOf(startColor, centerColor, endColor)
                , floatArrayOf(0f, 0.5f, 1f)
                , Shader.TileMode.CLAMP)
            paint.shader = shader
            text = "X${comboTestNum}"
        }

    }

    private var comboTestNum = 1

    private fun showAddGiftAnimation(giftView: View) {
        val translateAnim = ObjectAnimator.ofFloat(giftView, "translationX",
            -DisplayUtils.dp2px(270f), 0f)
        translateAnim.interpolator = DecelerateInterpolator()
        translateAnim.duration = 350
        val iconView = giftView.findViewById<ImageView>(R.id.iv_avatar)
        val iconRotationAnim = ObjectAnimator.ofFloat(iconView, "rotation", 0f,  4f, -1f, 0f)
        iconRotationAnim.startDelay = 667
        iconRotationAnim.duration = 1000
        val animSet = AnimatorSet()
        animSet.playTogether(translateAnim, iconRotationAnim)
        animSet.start()
    }

    private fun showComboAnimation(giftView: View, comboCount: Int) {
        val comboTextView = giftView.findViewById<TextView>(R.id.tv_combo)
//        comboTextView.text = "X${comboCount}"
        comboTextView.text = "X${comboTestNum}"
        comboTestNum += 1

        val anim = ScaleAnimation(1.3f, 1f, 1.3f, 1f,
            RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = 300

        comboTextView.startAnimation(anim)
    }

    private fun getShowingView(giftUniqueId: String): View? {
        for (i in 0 until childCount) {
            try {
                val child = getChildAt(i)
                if (child?.getTag(R.id.id_live_gift_combo_view) != null) {
                    val tag = child.getTag(R.id.id_live_gift_combo_view) as String
                    if (!TextUtils.isEmpty(tag) && tag == giftUniqueId) {
                        return child
                    }
                }
            } catch (e: Exception) {
                //
            }
        }
        return null
    }

    private fun findReplaceIndexOfSpace(): Int {
        if (childCount < maxSize) {
            return childCount - 1
        }
        var replaceIndex = childCount - 1
        var comboTime = Long.MAX_VALUE
        for (i in 0 until childCount) {
            try {
                val child = getChildAt(i)
                if (child?.getTag(R.id.id_live_gift_combo_view) != null) {
                    val tag = child.getTag(R.id.id_live_gift_combo_view) as String?
                    if (tag.hasValue()) {
                        val comboItem = cacheCombos[tag]
                        if (comboItem != null && !isMyselfCombo(comboItem.iUserId)) {
                            if (comboTime > comboItem.lastComboTime) {
                                comboTime = comboItem.lastComboTime
                                replaceIndex = i
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                //
            }
        }
        return replaceIndex
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeAllViews()
        cacheCombos.clear()
        handler.removeCallbacksAndMessages(null)
    }
}