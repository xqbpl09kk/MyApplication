package fast.information.common

import android.app.Application
import android.util.Log
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.mezu.MeizuRegister
import org.android.agoo.xiaomi.MiPushRegistar


/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){

    companion object {
        lateinit var instance: MyApplication
    }

    val miPushId : String= "miPushId"
    val miPushSecret :String = "miPushSecret"
    val meiZuId :String = "meizuId"
    val meizuKey : String = "MeizuKey"





    override fun onCreate(){
        super.onCreate()
        System.out.print("App created ! ")
        instance = this
        Thread.setDefaultUncaughtExceptionHandler(UnCaughtException())
        initUmengPush()
    }



    private fun initUmengPush(){
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



}