package fast.information.network.bean.base

import com.google.gson.annotations.SerializedName

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:20.
*/
class ResultListBundle <T>{

    @SerializedName("version")
    var version :String ?= null
    @SerializedName("time")
    var time : String ?= null
    @SerializedName("current_count")
    var currentCount:Int = 0
    @SerializedName("next_cursor")
    var nextCursor:Int = 0
    @SerializedName("items")
    var items :ArrayList<T> ?= null

}