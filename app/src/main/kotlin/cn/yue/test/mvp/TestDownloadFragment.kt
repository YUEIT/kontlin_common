package cn.yue.test.mvp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import cn.yue.base.middle.mvp.components.BaseHintFragment
import cn.yue.test.R
import cn.yue.test.service.UpdateService
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2020/8/24
 */
@Route(path = "/app/testDownload")
class TestDownloadFragment : BaseHintFragment() {

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_download
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val downTV = findViewById<TextView>(R.id.downTV)
        downTV.setOnClickListener {
            val intent = Intent(mActivity, UpdateService::class.java)
            val connectionService = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {

                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val downInfo = service as UpdateService.DownloadInfo
                    downInfo.setUpdateListener { process, isFail ->
                        val progressTV = findViewById<TextView>(R.id.progressTV)
                        if (isFail) {
                            progressTV.text = "下载失败"
                        } else {
                            progressTV.text = "下载进度$process"
                        }
                    }
                }
            }
            mActivity.bindService(intent, connectionService, 0)
            mActivity.startService(intent)
        }
    }



}



