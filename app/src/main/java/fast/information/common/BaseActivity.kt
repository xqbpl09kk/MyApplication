package fast.information.common

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import fast.information.BuildConfig
import fast.information.R
import java.io.File

/**
 * Created by xiaqibo on 2018/4/10.
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        registerViews()
        MyApplication.instance.onActivityCreate(this)
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



    protected fun download(downloadUrl: String? ,newVersion:String?) {
        val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val preDownloadId = getSharedPreferences("temp" , Context.MODE_PRIVATE).getLong("download_id".plus(newVersion) , 0)
        val cursor  = downloadManager.query(DownloadManager.Query().setFilterById(preDownloadId))
        if(!cursor.moveToFirst()){
            val downloadId = downloadManager.enqueue(
                    DownloadManager.Request(Uri.parse(if(BuildConfig.DEBUG) "http://img.bishijie.com/Bishijie_1.7_release.apk" else downloadUrl))
                            .setDestinationUri(Uri.fromFile(
                                    File(applicationContext.externalCacheDir.absolutePath, "bzhi.apk")))
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            )
            getSharedPreferences("temp" , Context.MODE_PRIVATE).edit().putLong("download_id".plus(newVersion) ,downloadId).apply()
            MyApplication.instance.registerDownloadReceiver(downloadId)
        }else{
            Toast.makeText(MyApplication.instance , R.string.already_enqueued , Toast.LENGTH_LONG).show()
        }


    }
}