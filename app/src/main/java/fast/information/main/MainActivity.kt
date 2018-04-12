package fast.information.main

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
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
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.RetrofitHelper
import fast.information.network.bean.UpdateInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity() ,TimerHandler.Timer{


    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    private val fragmentManager: FragmentManager = supportFragmentManager
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private var fragmentOne: FragmentOne? = null
    private var fragmentTwo: FragmentTwo? = null
    private var fragmentThree: FragmentThree? = null
    private var currentFragment: Fragment? = null
    private var isStarChecked: Boolean = false
    private val bottomMenuIds: IntArray = intArrayOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)

    private var timerHandler :TimerHandler ?=null
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchPageByFragment(fragmentOne)
                setTitle(fragmentOne?.getTitle()!!)
            }
            R.id.navigation_dashboard -> {
                switchPageByFragment(fragmentTwo)
                setTitle(R.string.title_dashboard)
            }
            R.id.navigation_notifications -> {
                switchPageByFragment(fragmentThree)
                setTitle(R.string.settings)
            }
        }
        invalidateOptionsMenu()
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("tag", "onCreate")
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initFragments()
        checkUpdate()

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (navigation.selectedItemId == R.id.navigation_home) {
            menu?.findItem(R.id.scroll)?.isVisible = true;
            menu?.findItem(R.id.scroll)?.isVisible = true;
        } else {
            menu?.findItem(R.id.scroll)?.isVisible = false;
            menu?.findItem(R.id.scroll)?.isVisible = false;
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId ?: 0 == R.id.scroll) {
            if (currentFragment == fragmentOne) {
                fragmentOne?.scrollToTop()
            }
        } else if (item?.itemId ?: 0 == R.id.star) {
            navigation.selectedItemId = bottomMenuIds[0]
//            switchPageByIndex(0)
            isStarChecked = !isStarChecked
            if (isStarChecked) {
                item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_home_white_24dp)
                setTitle(R.string.star)
            } else {
                item?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_black_24dp)
                setTitle(R.string.fast_information)
            }
            invalidateOptionsMenu()
            fragmentOne?.switchContent()
        }
        return super.onOptionsItemSelected(item)
    }


    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt("position", fragmentList.indexOf(currentFragment))
        Log.e("tag", "onSaveInstanceState")
    }

    override fun onResume() {
        super.onResume()
        if(timerHandler == null)
            timerHandler = TimerHandler(this)
        timerHandler!!.sendEmptyMessageDelayed(TimerHandler.move , TimerHandler.delayMillis)
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.sendEmptyMessage(TimerHandler.stop)
    }

    override fun onTime() {
        if(fragmentTwo?.isVisible == true)
            fragmentTwo?.netStep(false)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.e("tag", "onRestoreInstanceState")
        val position = savedInstanceState.getInt("position")
        switchPageByIndex(position)
        navigation.selectedItemId = bottomMenuIds[position]
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initFragments() {

        val bundle1 = Bundle()
        fragmentOne = FragmentOne.createInstance(bundle1)
        fragmentList.add(fragmentOne!!)

        val bundle2 = Bundle()
        fragmentTwo = FragmentTwo.createInstance(bundle2)
        fragmentList.add(fragmentTwo!!)

        val bundle3 = Bundle()
        fragmentThree = FragmentThree.createInstance(bundle3)
        fragmentList.add(fragmentThree!!)
        switchPageByIndex(0)
    }

    private fun switchPageByIndex(pageIndex: Int) {
        assert(pageIndex < 3)
        val ft = fragmentManager.beginTransaction()
        currentFragment = if (currentFragment == null) {
            ft.add(R.id.main_fragment_container, fragmentOne)
            fragmentOne
        } else {
            ft.hide(currentFragment)
            val targetFragment: Fragment = fragmentList[pageIndex]
            if (targetFragment.isAdded) {
                ft.show(targetFragment)
            } else {
                ft.add(R.id.main_fragment_container, targetFragment)
            }
            targetFragment
        }
        ft.commit()
    }


    private fun switchPageByFragment(targetFragment: Fragment?) {
        val ft = fragmentManager.beginTransaction()
        currentFragment = if (currentFragment == null) {
            ft.add(R.id.main_fragment_container, fragmentOne)
            fragmentOne
        } else {
            ft.hide(currentFragment)
            if (targetFragment?.isAdded == true) {
                ft.show(targetFragment)
            } else {
                ft.add(R.id.main_fragment_container, targetFragment)
            }
            targetFragment
        }
        ft.commit()
    }

    override fun displayHomeAsUpEnabled(): Boolean {
        return false
    }

    private fun checkUpdate() {
        RetrofitHelper.instance.checkUpdate(object : ResultCallback<ResultBundle<UpdateInfo>> {
            override fun onSuccess(t: ResultBundle<UpdateInfo>?) {
                val updateInfo = t?.item ?: return
                if (updateInfo.latest_app_version
                        != packageManager.getPackageInfo(packageName, 0).versionName) {
                    if(getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("update" , false)){
                        download(updateInfo.android_url ,updateInfo.latest_app_version)
                        Toast.makeText(MyApplication.instance , R.string.auto_update_on_go , Toast.LENGTH_LONG).show()
                    }else{
                        showUpdateDialog(updateInfo)
                    }
                }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Log.i("CheckUpdate", message)
            }
        })
    }


    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        if (isFinishing) return
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.update)
                .setMessage(updateInfo?.version_info ?: "test message")
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }.setPositiveButton(R.string.update) { dialog, _ ->
                    dialog.cancel()
                    download(updateInfo.android_url,updateInfo.latest_app_version)
                }
        dialogBuilder.create().show()
    }
}
