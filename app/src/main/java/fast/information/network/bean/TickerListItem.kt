package fast.information.network.bean

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import java.io.Serializable

/**
 * Created by xiaqibo on 2018/4/8.
 */
class TickerListItem : Serializable {

    var id : String ?= null
    var coin :String ?= null
    var symbol :String ?= null
    var currency :String ?= null
    var site_key :String ?= null
    var price :String ?= null
    @SerializedName("24h_volume_usd")
    var __h_volume_usd :String ?= null
    var market_cap_usd :String ?= null
    var available_supply :String ?= null
    var total_supply :String ?= null
    var max_supply :String ?= null
    var percent_change_1h:String ?= null
    var percent_change_24h:String ?= null
    var percent_change_7d :String ? = null
    var office_sites :ArrayList<String> ? = null
    var block_sites :ArrayList<String> ?= null
    var white_paper :String ?= null
    var introduction :String ?= null


}