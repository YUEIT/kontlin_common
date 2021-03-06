package cn.yue.base.middle.net.upload

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Description :
 * Created by yue on 2019/6/18
 */
interface UploadServer {

    @POST("/img/upload/{uploadUrl}/multifile/multipart")
    @Multipart
    fun upload(@Path("uploadUrl") uploadUrl: String, @Part file: List<MultipartBody.Part>): Single<ImageResultListData>
}