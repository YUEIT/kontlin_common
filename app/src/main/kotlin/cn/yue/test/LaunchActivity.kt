package cn.yue.test

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.utils.Utils
import cn.yue.base.module.manager.ModuleManager
import cn.yue.test.utils.LocalStorage

class LaunchActivity : BaseFragmentActivity() {

    private fun onSystemBarShow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val window = window
            window.addFlags(Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            // Translucent status bar
            window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSystemBarShow()
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        setContentView(R.layout.activity_launch)
    }

    override fun onStart() {
        super.onStart()
        if (LocalStorage.getUserPermission()) {
            ModuleManager.doInit(Utils.getContext())
            delayToDo { toStart() }
        } else {
            UserAuthDialog().apply {
                setConfirmListener {
                    LocalStorage.setUserPermission(true)
                    ModuleManager.doInit(Utils.getContext())
                    delayToDo { toStart() }
                }
            }.show(supportFragmentManager)
        }
    }

    private fun toStart() {
        startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
        finish()
    }

    private fun delayToDo(block: ()->Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (this@LaunchActivity.isDestroyed || this@LaunchActivity.isFinishing) {
                return@postDelayed
            }
            block.invoke()
        }, 2000)
    }


    override fun initView() {}
}