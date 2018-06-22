package fast.information.common

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import fast.information.BuildConfig
import fast.information.R
import fast.information.network.RetrofitHelper
import fast.information.network.bean.base.ResultCallback
import retrofit2.Call
import java.io.File
import java.util.*

/**
 * Created by xiaqibo on 2018/4/10.
 */
abstract class BaseActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        registerViews()
        MyApplication.instance.onActivityCreate(this)
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

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.instance.onActivityDestroy(this)
    }

    open fun displayHomeAsUpEnabled (): Boolean{
        return true
    }


    open fun registerViews(){
        supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled())
    }

    @LayoutRes
    abstract fun getLayoutRes():Int

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.e("BaseActivity" , newConfig!!.locale.language)
    }

    protected fun download(downloadUrl: String? ,newVersion:String?) {
//        val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val preDownloadId = getSharedPreferences("temp" , Context.MODE_PRIVATE).getLong("download_id".plus(newVersion) , 0)
//        val cursor  = downloadManager.query(DownloadManager.Query().setFilterById(preDownloadId))
//        if(!cursor.moveToFirst()){
//            val downloadId = downloadManager.enqueue(
//                    DownloadManager.Request(Uri.parse(
////                            if(BuildConfig.DEBUG) "http://img.bishijie.com/Bishijie_1.7_release.apk" else
//                        downloadUrl))
//                            .setDestinationUri(Uri.fromFile(
//                                    File(applicationContext.externalCacheDir.absolutePath, "bzhi.apk")))
//                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
//                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            )
//            getSharedPreferences("temp" , Context.MODE_PRIVATE).edit().putLong("download_id".plus(newVersion) ,downloadId).apply()
//            MyApplication.instance.registerDownloadReceiver(downloadId)
//        }else{
//            Toast.makeText(MyApplication.instance , R.string.already_enqueued , Toast.LENGTH_LONG).show()
//        }
        var firstNotify = true
        Log.e("download_URL" , downloadUrl)
        downloadUrl?.let {
            RetrofitHelper.instance.downloadApkFile(it, object : ResultCallback<Int> {
                override fun onSuccess(t: Int?) {
                    if(firstNotify){
                        createNotify(t ?:0 )
                        firstNotify = false
                    }
//                    if(t == 100){
//                        Toast.makeText(MyApplication.instance , "download success  " , Toast.LENGTH_SHORT).show()
//                    }else{
//                        Toast.makeText(MyApplication.instance , "downloading  ".plus(t) , Toast.LENGTH_SHORT).show()
//                    }
                }

                override fun onFailure(message: String, errorCode: Int) {
                    Toast.makeText(MyApplication.instance , "download failed " , Toast.LENGTH_SHORT).show()
                }

            })
        }

    }
    private fun createNotify(  progress :Int){
        val notifyManager:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId :String = "101"
        val notifyBuilder:  NotificationCompat.Builder = NotificationCompat.Builder(MyApplication.instance , channelId)
        notifyBuilder.setProgress(100 , progress , true)
        val notification : Notification = notifyBuilder.build()
         notifyManager.notify(1001 , notification)
    }
}