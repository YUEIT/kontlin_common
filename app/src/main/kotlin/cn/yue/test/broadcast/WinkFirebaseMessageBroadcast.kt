package cn.yue.test.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WinkFirebaseMessageBroadcast : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val route = intent?.getStringExtra("route")
        Log.d("luo", "onReceive: ${route}")
    }

}