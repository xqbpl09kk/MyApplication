package fast.information

import android.app.Application
import android.util.Log
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import fast.information.network.RetrofitHelper
import org.android.agoo.huawei.HuaWeiReceiver
import org.android.agoo.huawei.HuaWeiRegister


/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){

    companion object {
        lateinit var instance: MyApplication
    }



    override fun onCreate(){
        super.onCreate()
        System.out.print("App created ! ")
        instance = this
        initUmengPush()
//        UMConfigure.init(this, "5ab1c3a3b27b0a523f000062", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "d27fbc58ddef55f95d0638c18862f36f");
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
    }



}