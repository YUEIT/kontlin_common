package cn.yue.base.kotlin.test;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.yue.base.common.activity.BaseActivity;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Description :
 * Created by yue on 2018/11/18
 */

@Route(path = "/app/videoPlayer")
public class VideoPlayerActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        playerStandard = (JCVideoPlayerStandard) findViewById(R.id.playerstandard);
        playerStandard.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "追龙");
        playerStandard.startVideo();
    }

    private JCVideoPlayerStandard playerStandard;
    private String videoUrl = "https://key002.ku6.com/xy/d7b3278e106341908664638ac5e92802.mp4";


}
