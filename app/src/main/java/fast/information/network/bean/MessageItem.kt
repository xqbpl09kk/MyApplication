package fast.information.network.bean

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
open class MessageItem :Serializable{

    companion object {
        @SuppressLint("SimpleDateFormat")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val todayCalendar :Calendar = Calendar.getInstance()
        init{
            todayCalendar.set(Calendar.HOUR_OF_DAY , 0)
            todayCalendar.set(Calendar.MINUTE , 0)
            todayCalendar.set(Calendar.SECOND , 0)
        }
    }

    @SerializedName("id")
    var id:String ?= null
    @SerializedName("content")
    var content:String =""
    @SerializedName("link")
    var link:String ?= null
    @SerializedName("is_red")
    var isRed:Int = 0
    @SerializedName("title")
    var title:String ?= null

    @SerializedName("created_at")
    private var createdAt:String ?= null


    fun getDate(showAll :Boolean):String{
        dateFormat.parse(createdAt)
        val calendar : Calendar = dateFormat.calendar
        return when {
            showAll ->
                "%04d年%02d月%02d日 %02d:%02d".format(
                        calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH) + 1
                        ,calendar.get(Calendar.DAY_OF_MONTH)
                        ,calendar.get(Calendar.HOUR_OF_DAY)
                        ,calendar.get(Calendar.MINUTE))
            todayCalendar.before(calendar) ->
                "%02d:%02d".format(
                        calendar.get(Calendar.HOUR_OF_DAY)
                        ,calendar.get(Calendar.MINUTE))
            else ->
                "%02d月%02d日 %02d:%02d".format(
                        calendar.get(Calendar.MONTH) + 1
                        ,calendar.get(Calendar.DAY_OF_MONTH)
                        ,calendar.get(Calendar.HOUR_OF_DAY)
                        ,calendar.get(Calendar.MINUTE))
        }
    }
}