package cn.yue.base.kotlin.test;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.yue.base.common.utils.debug.LogUtils;
import cn.yue.base.common.widget.keyboard.OnEmotionClickListener;
import cn.yue.base.common.widget.keyboard.mode.IEmotion;

/**
 * Description :
 * Created by yue on 2018/11/14
 */

@Route(path = "/app/keyboardTest")
public class KeyboardTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        CustomKeyboardLayout customKeyboardLayout = findViewById(R.id.keyboardL);
        customKeyboardLayout.getEmotionLayout().setOnEmotionClickListener(new OnEmotionClickListener() {
            @Override
            public void onClick(IEmotion emotion) {
                LogUtils.Companion.d(emotion.getImageUrl());
            }
        });
    }
}
