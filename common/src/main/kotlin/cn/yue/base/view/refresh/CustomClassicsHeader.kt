package cn.yue.base.view.refresh

import android.content.Context
import android.graphics.Color
import com.scwang.smart.refresh.header.ClassicsHeader

/**
 * Description :
 * Created by yue on 2020/12/30
 */
class CustomClassicsHeader(context: Context): ClassicsHeader(context) {

    init {
        setBackgroundColor(Color.parseColor("#409EFF"))
    }
}