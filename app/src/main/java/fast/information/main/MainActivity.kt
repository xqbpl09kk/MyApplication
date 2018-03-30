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
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.RetrofitHelper
import fast.information.network.bean.UpdateInfo
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
                switchPageByFragment(fragmentOne)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchPageByFragment(fragmentTwo)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                switchPageByFragment(fragmentThree)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("tag", "onCreate")
        setContentView(R.layout.activity_main)
        navigation.menu.findItem(R.id.navigation_dashboard).isVisible   = false
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.menu.findItem(R.id.navigation_dashboard).isVisible   = false
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
//            switchPageByIndex(0)
            isStarChecked = ! isStarChecked
            if(isStarChecked){
                item?.icon = ContextCompat.getDrawable(this , R.drawable.ic_home_white_24dp)
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
        switchPageByIndex(position)
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

        switchPageByIndex(0)
    }

    private fun switchPageByIndex(pageIndex : Int){
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



    private fun switchPageByFragment(targetFragment:Fragment ?){
        val ft = fragmentManager.beginTransaction()
        currentFragment = if(currentFragment == null ){
            ft.add(R.id.main_fragment_container ,fragmentOne)
            fragmentOne
        }else{
            ft.hide(currentFragment)
            if(targetFragment?.isAdded == true){
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
                val updateInfo = t?.item ?: return
                if(updateInfo.latest_app_version
                        != packageManager.getPackageInfo(packageName , 0).versionName)
                    showUpdateDialog(updateInfo)
            }

            override fun onFailure(message: String, errorCode: Int) {
                //do nothing
                Log.i("CheckUpdate"  ,message)
            }
        })
    }


    private fun showUpdateDialog( updateInfo : UpdateInfo?){
        if (isFinishing) return
        val dialogBuilder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.update)
                .setMessage(updateInfo?.version_info?:"test message")
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }.setPositiveButton(R.string.update){dialog , _ ->
                    run {
                        dialog.cancel()
                        download(updateInfo?.android_url?:"")
                    }
                }
        dialogBuilder.create().show()
    }

    private fun download(downloadUrl : String ?){
        val downloadManager :DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(
                DownloadManager.Request(Uri.parse(downloadUrl))
                        .setDestinationUri(Uri.fromFile(
                                        File(applicationContext.externalCacheDir.absolutePath , "bzhi.apk")))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        )
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        downloadStatusReceiver =  object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if(id == downloadId){
                    Toast.makeText(context , R.string.download_complete_notify,Toast.LENGTH_LONG).show()
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
