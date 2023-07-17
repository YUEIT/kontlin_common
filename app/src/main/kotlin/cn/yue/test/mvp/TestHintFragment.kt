package cn.yue.test.mvp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.base.utils.variable.TimeUtils
import cn.yue.base.widget.TopBar
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestHintBinding
import com.alibaba.android.arouter.facade.annotation.Route
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
        binding.tvChange.setOnClickListener {
            val str = binding.etNumber.text.toString()
            try {
            	val date = Date(str.toLong())
                binding.tvNumber.text = TimeUtils.date2String(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val array = arrayOf("1")
        array.asList()

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