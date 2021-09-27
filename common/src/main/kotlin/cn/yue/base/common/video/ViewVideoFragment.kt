package cn.yue.base.common.video

import android.net.Uri
import android.os.Bundle
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@Route(path = "/common/viewVideo")
class ViewVideoFragment: BaseFragment() {

    lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun getLayoutId(): Int {
        return R.layout.fragment_view_video
    }

    override fun initView(savedInstanceState: Bundle?) {
        val playerView = findViewById<PlayerView>(R.id.playerView)
        simpleExoPlayer = SimpleExoPlayer.Builder(mActivity).build()
        val uris = arguments?.getParcelableArrayList<Uri>("uris")
        uris?.let {
            playerView.player = simpleExoPlayer
            for (uri in uris) {
                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                simpleExoPlayer.addMediaItem(mediaItem)
            }
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