package fast.information.common

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference


@Suppress("NAME_SHADOWING")
/**
 * Created by xiaqibo on 2018/4/11.
 *
 */
class TimerHandler(activity: Activity) : Handler() {

    interface Timer{
        fun onTime()
    }

    companion object {
        var delayMillis = MyApplication.instance.getSharedPreferences("settings" , Context.MODE_PRIVATE)
                .getInt("refresh_rate" , 2* 60)* 1000L //Fetch last data every 2 minutes

        const val move : Int = 2

        fun resetDelay(){
            delayMillis = MyApplication.instance.getSharedPreferences("settings" , Context.MODE_PRIVATE)
                    .getInt("refresh_rate" , 2* 60)* 1000L
        }
    }


    private var activityHolder: WeakReference<Activity> = WeakReference(activity)

    private val runnable = Runnable {
        val activity = activityHolder.get()
        if(activity!= null && !activity.isFinishing ){
            (activity as Timer).onTime()
        }
    }



    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)
        when (msg?.what) {
            move -> {
                runnable.run()
                sendEmptyMessageDelayed(move , delayMillis)
            }
        }
    }
}



