package fast.information.network

/**
 * Created by xiaqibo on 2018/3/13.
 */
class ResultListBundle <T>{

    var version :String ?= null
    var time : String ?= null
    var current_count :Int = 0
    var next_cursor :Int = 0
    var items :ArrayList<T> ?= null


}