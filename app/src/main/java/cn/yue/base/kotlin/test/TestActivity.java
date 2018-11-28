package cn.yue.base.kotlin.test;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.yue.base.common.activity.BaseActivity;

/**
 * Description :
 * Created by yue on 2018/11/19
 */
@Route(path = "/app/test")
public class TestActivity extends BaseActivity{

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {

    }
}
