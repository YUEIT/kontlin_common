package cn.yue.base.net.upload

import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/18
 */
data class ImageResult(var size: Int,
                       var width: String,
                       var url: String,
                       var error: Int,
                       var height: String) {

    companion object {
        fun toListString(list: List<ImageResult>) : MutableList<String>{
            val stringList = ArrayList<String>()
            if (list.isEmpty()) {
                return stringList
            }

            for (imageResult in list) {
                stringList.add(imageResult.url)
            }
            return stringList
        }

        fun parse(list: List<ImageResult>): Array<String> {
            val results = arrayOfNulls<String>(2)
            val url = StringBuilder()
            val size = StringBuilder()
            for (imageResult in list) {
                url.append(imageResult.url)
                url.append(";")


                size.append(imageResult.height)
                size.append("*")
                size.append(imageResult.width)
                size.append(";")
            }

            results[0] = url.toString()
            results[1] = size.toString()
            return results as Array<String>
        }
    }
}