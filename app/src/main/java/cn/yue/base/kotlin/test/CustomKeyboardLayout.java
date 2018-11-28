package cn.yue.base.kotlin.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.yue.base.common.widget.keyboard.BaseKeyboardLayout;
import cn.yue.base.common.widget.keyboard.EmotionLayout;

/**
 * Description :
 * Created by yue on 2018/11/14
 */
public class CustomKeyboardLayout extends BaseKeyboardLayout{

    public CustomKeyboardLayout(Context context) {
        super(context);
    }

    public CustomKeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomKeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView(Context context) {
        super.initView(context);
        ImageView changeEmotionIV = findViewById(R.id.changeEmotionIV);
        changeEmotionIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                getEmotionLayout().toggleEmotionShow();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_keyboard;
    }

    @Override
    protected EmotionLayout getEmotionLayout() {
        return findViewById(R.id.emotionL);
    }
}
