package cn.yue.base.middle.net.intercept

import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NoNetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!NetworkUtils.isConnected()) {
            //            //取  缓存(因为现在没有缓存 所以取到的一定是null)
            //            Request newRequest = chain.request().newBuilder()
            //                    .cacheControl(CacheControl.FORCE_CACHE)
            //                    .build();
            //            Response proceed = chain.proceed(newRequest);
            //            if ("Unsatisfiable Request (only-if-cached)".equals(proceed.message())) {
            //                BaseBean baseBean = new BaseBean();
            //                baseBean.setCode(NetworkConfig.ERROR_NO_NET);
            //                baseBean.setMessage("无网络");
            //                baseBean.setData(null);
            //                //交给gson的工厂去解析这个无网络错误
            //                return proceed.newBuilder()
            //                        .body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(baseBean)))
            //                        .build();
            //            } else {
            //                return proceed;
            //            }

            throw ResultException(ResponseCode.ERROR_NO_NET, "无网络: ${chain.request().url}")
        } else {
            chain.proceed(chain.request())
        }
    }
}
