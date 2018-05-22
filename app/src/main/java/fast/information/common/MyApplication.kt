package fast.information.common

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import fast.information.BuildConfig
import fast.information.R
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.mezu.MeizuRegister
import org.android.agoo.xiaomi.MiPushRegistar
import java.util.*


/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){

    companion object {
        lateinit var instance: MyApplication

    }


    @ColorInt var colorGreen : Int ?=null
    @ColorInt var colorRed : Int ?=null

    private val activityTaskList : LinkedList<BaseActivity> = LinkedList()


    var downloadStatusReceiver : BroadcastReceiver ? = null

    private val miPushId : String= "miPushId"
    private val miPushSecret :String = "miPushSecret"
    private val meiZuId :String = "meizuId"
    private val meizuKey : String = "MeizuKey"

    override fun onCreate(){
        super.onCreate()
        System.out.print("App created ! ")
        instance = this
        colorGreen = ContextCompat.getColor(this , R.color.change_green)
        colorRed = ContextCompat.getColor(this , R.color.change_red)
        Thread.setDefaultUncaughtExceptionHandler(UnCaughtException())
        initUmengPush()
    }



    private fun initUmengPush(){
        if(BuildConfig.DEBUG) return
        UMConfigure.init(this@MyApplication , UMConfigure.DEVICE_TYPE_PHONE , "88f7edf62d3a92c69092d60a77b73729")
        val mPushAgent = PushAgent.getInstance(this)

        mPushAgent.register(object : IUmengRegisterCallback {

            override fun onSuccess(deviceToken: String) {
                Log.i("MyApplication" , "init umeng success . device token is $deviceToken")
            }

            override fun onFailure(s: String, s1: String) {
                Log.i("MyApplication" , "error message is $s and $s1")
            }
        })

        HuaWeiRegister.register(this@MyApplication)
        MiPushRegistar.register(this@MyApplication , miPushId , miPushSecret)
        MeizuRegister.register(this@MyApplication , meiZuId , meizuKey)

    }


    fun <T:BaseActivity>jumpActivity(clazz : Class<T> ,  bundle : Bundle?){
        if(activityTaskList.size == 0) {
            startActivity(Intent(this , clazz).putExtra("data" ,bundle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }else{
            activityTaskList.last.startActivity(Intent(this , clazz).putExtra("data" ,bundle))
        }
    }

    fun onActivityDestroy(activity : BaseActivity ){
        if(activityTaskList.size == 0) return
        activityTaskList.remove(activity)
    }

    fun onActivityCreate(activity : BaseActivity ){
        activityTaskList.add(activity)
    }

    fun getLastActivity():BaseActivity ?{
        return if(activityTaskList.size == 0) null else activityTaskList.last
    }

    fun registerDownloadReceiver(downloadId : Long){
        downloadStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Toast.makeText(context, R.string.download_complete_notify, Toast.LENGTH_LONG).show()
                    unregisterReceiver(downloadStatusReceiver)
                    downloadStatusReceiver = null
                }
            }
        }
        registerReceiver(downloadStatusReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

}