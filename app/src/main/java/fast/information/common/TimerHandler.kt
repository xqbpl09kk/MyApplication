package fast.information.common

import android.app.Activity
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference


/**
 * Created by xiaqibo on 2018/4/11.
 *
 */
class TimerHandler(activity: Activity) : Handler() {

    companion object {
        val start: Int = 0
        val stop: Int = 1
    }

    private var activityHolder: WeakReference<Activity> = WeakReference(activity)

    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)
        when (msg?.what) {
            start -> {

            }
            stop -> {

            }
            else -> {

            }
        }
    }
}



