package cn.yue.base.kotlin.test;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.jetbrains.annotations.Nullable;

import cn.yue.base.common.activity.BaseFragment;

/**
 * Description :
 * Created by yue on 2018/11/23
 */
@Route(path = "/app/testFr")
public class TestFragment extends BaseFragment{
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {

    }
}
