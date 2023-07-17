package cn.yue.base.activity

import android.graphics.Color
import android.os.Bundle

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class CommonTransparentActivity : CommonActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        removeTopBar()
        setContentBackground(Color.TRANSPARENT)
    }
}