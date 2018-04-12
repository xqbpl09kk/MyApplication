package fast.information.common

import android.app.Activity
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

        const val stop: Int = 1
        const val move : Int = 2
        const val delayMillis = 2 * 60 * 1000L //Fetch last data every 2 minutes
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
            stop -> {
                removeMessages(2)
            }
        }
    }
}



