package cn.yue.base.kotlin.test;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.yue.base.common.utils.NetworkUtils;

/**
 * Description :
 * Created by yue on 2018/11/14
 */
public class AppApplication extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
        ARouter.openDebug();
        NetworkUtils.setContext(this);
    }
}
