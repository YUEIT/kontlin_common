package cn.yue.base.kotlin.test.component

import android.net.Uri
import android.os.Bundle
import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.components.BaseHintFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.fragment_test_video.*

@Route(path = "/app/testVideo")
class TestVideoFragment: BaseHintFragment() {

    lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_video
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        simpleExoPlayer = SimpleExoPlayer.Builder(mActivity).build()
        val uri = arguments?.getParcelable<Uri>("uri")
        uri?.let {
            player.player = simpleExoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            simpleExoPlayer.setMediaItem(mediaItem)
            simpleExoPlayer.prepare()
            simpleExoPlayer.play()
        }

    }

    override fun onStart() {
        super.onStart()
        if (!simpleExoPlayer.isPlaying) {
            simpleExoPlayer.play()
        }
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
    }
}