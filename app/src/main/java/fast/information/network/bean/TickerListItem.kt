package fast.information.network.bean

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field
import java.io.Serializable

/**
 * Created by xiaqibo on 2018/4/8.
 */
class TickerListItem : Serializable {
//    {
//            "symbol":"ETH",
//　　　　　　"official_sites":[
//　　　　　　　　"https://www.ethereum.org/"
//　　　　　　],
//　　　　　　"block_sites":[
//　　　　　　　　"https://etherscan.io/",
//　　　　　　　　"https://ethplorer.io/",
//　　　　　　　　"https://etherchain.org/"
//　　　　　　],
//　　　　　　"white_paper":"https://github.com/ethereum/wiki/wiki/%5BEnglish%5D-White-Paper",
//　　　　　　"introduction":"<p>
//　　　　　　"icon":"http://bzhionline.test.upcdn.net/image/icons/ethereum_icon.png"
//    }
    var id : String ?= null
    var coin :String ?= null
    var icon :String ?= null
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
    var official_sites :ArrayList<String> ? = null
    var block_sites :ArrayList<String> ?= null
    var white_paper :String ?= ""
    var introduction :String ?= ""


}