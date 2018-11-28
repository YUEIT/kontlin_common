package cn.yue.base.kotlin.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import cn.yue.base.common.activity.FRouter;
import cn.yue.base.common.utils.debug.LogUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.commentTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FRouter.getInstance()
                        .build("/app/onePage")
                        .navigation(MainActivity.this, 101);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                List<String> list = data.getStringArrayListExtra("photos");
                LogUtils.Companion.i(""+list);
            }
        }
    }
}
