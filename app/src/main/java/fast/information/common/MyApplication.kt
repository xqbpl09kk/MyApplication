package fast.information.common

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.Nullable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.umeng.commonsdk.UMConfigure
import fast.information.BuildConfig
import fast.information.R
import fast.information.network.RetrofitHelper
import fast.information.network.bean.AuthItem
import java.util.*


/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){

    companion object {
        lateinit var instance: MyApplication
        var isCreate = false
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
        isCreate = true
        instance = this
        colorGreen = ContextCompat.getColor(this , R.color.change_green)
        colorRed = ContextCompat.getColor(this , R.color.change_red)
//        Thread.setDefaultUncaughtExceptionHandler(UnCaughtException())
//        initUmengPush()
        RetrofitHelper.auth = restoreAuth()
        initLanguage()
    }

    private fun initLanguage(){
        val language = getSharedPreferences("settings", Context.MODE_PRIVATE).getString("language" , "auto")
        if(language == "auto") return
        when(language){
            "english" ->{
                val configuration : Configuration = resources.configuration
                configuration.locale = Locale.ENGLISH
                resources.updateConfiguration(configuration , resources.displayMetrics)
            }
            "china" ->{
                val configuration : Configuration = resources.configuration
                configuration.locale = Locale.CHINA
                resources.updateConfiguration(configuration , resources.displayMetrics)
            }
        }
    }





    fun saveAuth(item :AuthItem ?){
        val preferenceEditor  = getSharedPreferences("auth" , Context.MODE_PRIVATE).edit()
        preferenceEditor.putString("access_token" , item?.access_token)
        preferenceEditor.putString("token_type" , item?.token_type)
        preferenceEditor.putString("expires_at" , item?.expires_at)
        preferenceEditor.putString("refresh_token" , item?.refresh_token)
        preferenceEditor.putBoolean("is_new" , item?.is_new?:false)
        preferenceEditor.putString("user_id" , item?.user?.id)
        preferenceEditor.putString("user_name" , item?.user?.name)
        preferenceEditor.putString("user_avatar" , item?.user?.avatar)
        preferenceEditor.putString("user_email" ,item?.user?.email)
        preferenceEditor.apply()
    }


    private fun restoreAuth() : AuthItem ?{
        val preference  = getSharedPreferences("auth" , Context.MODE_PRIVATE)
        if(TextUtils.isEmpty(preference.getString("access_token" ,"")))
            return null
        val item = AuthItem()
        item.user = AuthItem.Companion.User()
        item.access_token = preference.getString("access_token" ,"")
        item.token_type = preference.getString("token_type" ,"")
        item.expires_at = preference.getString("expires_at" ,"")
        item.refresh_token = preference.getString("refresh_token" ,"")
        item.is_new = preference.getBoolean("is_new" ,false)
        item.user!!.id = preference.getString("user_id" ,"")
        item.user!!.name = preference.getString("user_name" ,"")
        item.user!!.avatar = preference.getString("user_avatar" ,"")
        item.user!!.email = preference.getString("user_email" ,"")
        return item
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