package fast.information.network.bean.base

import com.google.gson.annotations.SerializedName

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class ResultBundle <T>{

    @SerializedName("version")
    var version :String ?= null
    @SerializedName("time")
    var time : String ?= null
    @SerializedName("item")
    var item:T ?= null

}