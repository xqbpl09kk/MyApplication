package fast.information

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.taobao.agoo.BaseNotifyClickActivity
import com.umeng.message.UmengNotifyClickActivity
import org.android.agoo.common.AgooConstants


/**
 * Created by xiaqibo on 2018/3/21.
 */
class NotificationRouteActivity : UmengNotifyClickActivity() {

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        setContentView(R.layout.fragment_empty)
    }

    override fun onMessage(intent: Intent) {
        super.onMessage(intent)  //此方法必须调用，否则无法统计打开数
        val body = intent.getStringExtra(AgooConstants.MESSAGE_BODY)
        Log.i("NotificationRoute", body)
    }

}