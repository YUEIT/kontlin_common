package cn.yue.test.mvp

import FloatWindowManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.base.router.FRouter
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.ActivityLifecycleImpl
import cn.yue.base.utils.code.ThreadUtils
import cn.yue.base.utils.code.setOnSingleClickListener
import cn.yue.base.widget.TopBar
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestHintBinding
import cn.yue.test.float.FloatWindowView
import cn.yue.test.float.FloatingWindowService
import cn.yue.test.mode.LiveGiftComboData
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.rxjava3.internal.operators.flowable.FlowableElementAtMaybe
import java.util.*
import java.util.regex.Pattern


/**
 * Description :
 * Created by yue on 2021/11/12
 */

@Route(path = "/app/testHint")
class TestHintFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_hint
    }
    
    private lateinit var binding: FragmentTestHintBinding
    
    override fun bindLayout(inflated: View) {
        binding = FragmentTestHintBinding.bind(inflated)
    }
    
    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setBackgroundColor(Color.RED)
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.vComboGift.addComboGift(LiveGiftComboData().apply {
            iUserId = 11
            giftId = 1
            comboId = 1
            lastComboTime = 1L
        })
        binding.vComboGift.addComboGift(LiveGiftComboData().apply {
            iUserId = 14
            giftId = 2
            comboId = 2
            lastComboTime = 2L
        })
        binding.btnCombo.setOnClickListener {
            binding.vComboGift.queueComboMessage(LiveGiftComboData().apply {
                iUserId = 13
                giftId = 100
                comboId = 100
            })
        }
    }

    private var mWindowManager: WindowManager? = null
    private var windowView: FloatWindowView? = null

    private fun showFloatWindow() {
//        mWindowManager = mActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        windowView = FloatWindowView(mActivity)
//        windowView!!.setOnCloseListener {
//
//        }
//        windowView!!.setOnUpdateListener { contentView, mLayoutParams ->
//            mWindowManager?.updateViewLayout(contentView, mLayoutParams)
//        }
//        mWindowManager?.addView(windowView, windowView!!.layoutParams)

        val intent = Intent(mActivity, FloatingWindowService::class.java)
        mActivity.startService(intent)
    }

    fun loadHook(str: String): String {
        return "未被拦截${str}"
    }

    //<copy><copy></copy></copy>
    fun parseCopy(content: String) {

        //        String reg = "\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]";//校验表情正则
        val reg = "<copy>.*?</copy>" //校验表情正则
        val pattern = Pattern.compile(reg)
        val matcher = pattern.matcher(content)
        var startIndex = 0
        val stringArray = arrayListOf<String>()
        while (matcher.find()) {
            val matchStr = matcher.group() //获取匹配到的字符串
            val start = matcher.start() //匹配到字符串的开始位置
            val end = matcher.end() //匹配到字符串的结束位置
            Log.d("luo", "parseCopy: $matchStr , $start $end")
            if (start > startIndex) {
                val beforeString = content.substring(startIndex, start)
                stringArray.add(beforeString)
            }
            val matchString = content.substring(start, end)
            stringArray.add(matchString.replace("<copy>", "").replace("</copy>", ""))
            startIndex = end
        }
        if (startIndex < content.length) {
            val matchString = content.substring(startIndex, content.length)
            stringArray.add(matchString)
        }
        val stringBuilder = StringBuilder()
        for (s in stringArray) {
            stringBuilder.append(s)
        }
        Log.d("luo", "parseCopy: $stringBuilder")
    }

}