package cn.yue.base.common.photo

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.image.ImageLoader.Companion.loadImage
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.common.photo.data.MediaFolderVO
import cn.yue.base.common.photo.data.MediaType
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import java.util.concurrent.Executors

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class SelectPhotoFolderFragment : BaseFragment() {
    private var commonAdapter: CommonAdapter<MediaFolderVO>? = null

    override fun initTopBar(topBar: TopBar) {}

    override fun getLayoutId(): Int {
        return R.layout.fragment_select_photo
    }

    override fun initView(savedInstanceState: Bundle?) {
        val rv = findViewById<RecyclerView>(R.id.photoRV)
        rv.layoutManager = LinearLayoutManager(mActivity)
        rv.adapter = object : CommonAdapter<MediaFolderVO>(mActivity, allFolder) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_select_photo_folder
            }

            override fun bindData(holder: CommonViewHolder, position: Int, mediaFolderVO: MediaFolderVO) {
                holder.setText(R.id.folderTV, mediaFolderVO.name + "(" + mediaFolderVO.count + ")")
                holder.getView<ImageView>(R.id.folderIV)?.loadImage(mediaFolderVO.coverUri)
                holder.setOnItemClickListener{
                    (mActivity as SelectPhotoActivity).changeToSelectPhotoFragment(mediaFolderVO.id, mediaFolderVO.name)
                }
            }
        }.also { commonAdapter = it }
        getAllPhotoFolder()
    }

    private var allFolder: MutableList<MediaFolderVO> = ArrayList()
    private fun getAllPhotoFolder() {
            val threadPoolUtils = Executors.newSingleThreadExecutor()
            threadPoolUtils.execute {
                val allFolder = PhotoUtils.getAllPhotosFolder(mActivity)
                val lastPhotos: List<MediaData> = PhotoUtils.getTheLastMedias(mActivity, 100, getMediaType())
                if (lastPhotos.isNotEmpty()) {
                    val lastMediaFolderVO = MediaFolderVO()
                    lastMediaFolderVO.id = ""
                    lastMediaFolderVO.coverUri = lastPhotos[0].uri
                    lastMediaFolderVO.count = lastPhotos.size
                    lastMediaFolderVO.name = R.string.app_photos_folder_nearly.getString()
                    allFolder.add(0, lastMediaFolderVO)
                }
                handler.sendMessage(Message.obtain(handler, 101, allFolder))
            }
        }

    private var handler = Handler(Handler.Callback { msg ->
        if (msg.what == 101) {
            allFolder.clear()
            if (msg.obj is MutableList<*>) {
                allFolder.addAll(msg.obj as ArrayList<MediaFolderVO>)
            }
            commonAdapter?.setList(allFolder)
        }
        false
    })

    private fun getMediaType(): MediaType {
        return (mActivity as SelectPhotoActivity).getMediaType()
    }
}