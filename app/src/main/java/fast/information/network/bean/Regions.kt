package fast.information.network.bean

/**
 * Created by xiaqibo on 2018/5/28.
 */
class Regions {

    var countries: ArrayList<Item> ?= null
    var states :ArrayList<Item> ?= null
    var cities :ArrayList<Item> ?=null
    var districts :ArrayList<Item> ?= null

    class Item{
        var id :Int  ?= null
        var country_id :Int ?= null
        var state_id : Int ?= null
        var city_id :Int ?= null
        var name :String ?=null
        var code :String ?= null
        var pinyin :String ?= null
    }
}