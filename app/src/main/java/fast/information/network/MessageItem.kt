package fast.information.network

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by xiaqibo on 2018/3/13.
 */
class MessageItem :Serializable{
    @SerializedName("id")
    var id :String ?= null
    @SerializedName("content")
    var content :String =""
    @SerializedName("link")
    var link :String ?= null
    @SerializedName("created_at")
    var created_at:String ? =null
}