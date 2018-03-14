package fast.information.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MessageItem :Serializable{
    @SerializedName("id")
    var id :String ?= null
    @SerializedName("content")
    var content :String =""
    @SerializedName("link")
    var link :String ?= null
    @SerializedName("created_at")
    var createdAt:String ? =null
}