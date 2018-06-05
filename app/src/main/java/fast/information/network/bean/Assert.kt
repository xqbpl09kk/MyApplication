package fast.information.network.bean

import java.io.Serializable

/**
 * Created by xiaqibo on 2018/5/30.
 */
class Assert :Serializable {
    var id :String ?= null
    var uid :String ? = null
    var coin:String ?= null
    var currency :String ?= null
    var type :String?= null
    var amount :String ?= null
    var cost_price :String ?= null
    var position:String ? = null
    var exchange :String ? = null
    var wallet_address :String ?= null
    var operate_date :String ? =null
    var updated_at :String ?= null
    var created_at :String ?= null



    var iiii : Boolean = false

    var percent:Float = 0f
    var icon :String ? = null
    var refresh_data_set :Boolean = false
    var current_price_usdt :String ?= null
    var today_change_rate :String ?= null
    var create_time :Long ?= null
}