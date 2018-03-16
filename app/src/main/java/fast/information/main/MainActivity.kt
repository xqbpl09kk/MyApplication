package fast.information.main

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import fast.information.R
import fast.information.network.ResultBundle
import fast.information.network.ResultCallback
import fast.information.network.RetrofitHelper
import fast.information.network.UpdateInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val fragmentManager  : FragmentManager = supportFragmentManager
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private var fragmentOne  : FragmentOne ? = null
    private var fragmentTwo : FragmentTwo ?= null
    private var fragmentThree  : FragmentThree ?= null
    private var currentFragment : Fragment  ?= null
    private var isStarChecked :Boolean = false
    private var downloadStatusReceiver :BroadcastReceiver ?= null
    private val bottomMenuIds : IntArray = intArrayOf(R.id.navigation_home  , R.id.navigation_dashboard , R.id.navigation_notifications)
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchPage(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchPage(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                switchPage(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    val testUrl ="http://imtt.dd.qq.com/16891/03A5F05B75B14F38A36755BBDF3B035B.apk?fsname=com.speedsoftware.rootexplorer_4.7.4_999474.apk&csr=97c2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("tag", "onCreate")
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initFragments()
        checkUpdate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId ?: 0 == R.id.scroll){
            if(currentFragment == fragmentOne){
                fragmentOne?.scrollToTop()
            }
        }else if(item?.itemId ?: 0 == R.id.star){
            navigation.selectedItemId = bottomMenuIds[0]
//            switchPage(0)
            isStarChecked = ! isStarChecked
            if(isStarChecked){
                item?.icon = ContextCompat.getDrawable(this , R.drawable.ic_home_black_24dp)
                setTitle(R.string.star)
            }else{
                item?.icon = ContextCompat.getDrawable(this , R.drawable.ic_star_black_24dp)
                setTitle(R.string.fast_information)
            }
            fragmentOne?.switchContent()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt("position", fragmentList.indexOf(currentFragment))
        Log.e("tag", "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.e("tag", "onRestoreInstanceState")
        val position = savedInstanceState.getInt("position")
        switchPage(position)
        navigation.selectedItemId = bottomMenuIds[position]
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initFragments(){

        val bundle1 = Bundle()
        fragmentOne = FragmentOne.createInstance(bundle1)
        fragmentList.add(fragmentOne!!)

        val bundle2 = Bundle()
        fragmentTwo = FragmentTwo.createInstance(bundle2)
        fragmentList.add(fragmentTwo !!)

        val bundle3 = Bundle()
        fragmentThree = FragmentThree.createInstance(bundle3)
        fragmentList.add(fragmentThree!!)

        switchPage(0)
    }

    private fun switchPage(pageIndex : Int){
        assert(pageIndex < 3)
        val ft = fragmentManager.beginTransaction()
        currentFragment = if(currentFragment == null ){
            ft.add(R.id.main_fragment_container ,fragmentOne)
            fragmentOne
        }else{
            ft.hide(currentFragment)
            val targetFragment : Fragment = fragmentList[pageIndex]
            if(targetFragment.isAdded){
                ft.show(targetFragment)
            }else{
                ft.add(R.id.main_fragment_container , targetFragment)
            }
            targetFragment
        }
        ft.commit()
    }


    private fun checkUpdate(){
        RetrofitHelper.instance.checkUpdate(object : ResultCallback<ResultBundle<UpdateInfo>> {
            override fun onSuccess(t: ResultBundle<UpdateInfo>?) {
                val updateInfo = t?.t ?: return
                if(updateInfo.latest_app_version
                        != packageManager.getPackageInfo(packageName , 0).versionName)
                    showUpdateDialog(updateInfo)
            }

            override fun onFailure(message: String, errorCode: Int) {
                //do nothing
            }
        })
    }


    private fun showUpdateDialog( updateInfo :UpdateInfo ?){
        if (isFinishing) return
        val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.update)
                .setMessage(updateInfo?.version_info?:"test message")
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }.setPositiveButton(R.string.update){dialog , _ ->
                    run {
                        dialog.cancel()
                        download(updateInfo?.android_url?:testUrl)
                    }
                }
        dialogBuilder.create().show()
    }

    private fun download(downloadUrl : String){
        val downloadManager :DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(
                DownloadManager.Request(Uri.parse(downloadUrl))
                        .setDestinationUri(Uri.fromFile(File(applicationContext.externalCacheDir.absolutePath , "bzhi.apk")))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        )
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        downloadStatusReceiver =  object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == downloadId){
                    Toast.makeText(context , R.string.download_complete_notify ,Toast.LENGTH_LONG).show()
                    unregisterReceiver(downloadStatusReceiver)
                }
            }
        }
        registerReceiver(downloadStatusReceiver , intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(downloadStatusReceiver != null)
            unregisterReceiver(downloadStatusReceiver)
    }
}
