package fast.information.chart

/**
 * Created by xiaqibo on 2018/3/30.
 */
class DataModel {


    var level  = Level.REAL_TIME

    var time :Long = Long.MIN_VALUE

    var max_value  : Float  = Float.MAX_VALUE

    var min_value : Float = Float.MIN_VALUE

    var value_arvg :Float = Float.NaN

    var value_upper :Float = Float.MAX_VALUE

    var value_down : Float = Float.MIN_VALUE

}