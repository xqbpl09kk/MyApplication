package fast.information.main

import android.content.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import fast.information.CollectionCoinsActivity
import fast.information.R
import fast.information.SearchActivity
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.RetrofitHelper
import fast.information.network.bean.UpdateInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), TimerHandler.Timer {


    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    private val fragmentManager: FragmentManager = supportFragmentManager
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private var fragmentNews: FragmentNews? = null
    private var fragmentMarket: FragmentMarket? = null
    private var fragmentAssert :FragmentAssert ?= null
    private var fragmentMore: FragmentMore? = null
    private var currentFragment: Fragment? = null
    private var isStarChecked: Boolean = false
    private val bottomMenuIds: IntArray = intArrayOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)

    private var searchView: SearchView? = null
    private var timerHandler: TimerHandler? = null
    private var selectedTabId = R.id.tab1


    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.putParcelable("android:support:fragments", null)
        super.onCreate(savedInstanceState)
        Log.e("tag", "onCreate")

        initFragments()
//        tab1.setOnClickListener({onTabItemSelected(it)})
//        tab2.setOnClickListener({onTabItemSelected(it)})
//        tab3.setOnClickListener({onTabItemSelected(it)})
//        tab4.setOnClickListener({onTabItemSelected(it)})
        checkUpdate()
    }

     fun onTabItemSelected(targetView : View){
        when(targetView.id){
            R.id.tab1 ->{
                switchPageByFragment(fragmentNews)
                setTitle(fragmentNews?.getTitle()!!)
            }
            R.id.tab2 ->{
                switchPageByFragment(fragmentMarket)
                setTitle(R.string.title_dashboard)
            }
            R.id.tab3 ->{
                switchPageByFragment(fragmentAssert)
                setTitle(R.string.assert_title)
            }
            R.id.tab4 ->{
                switchPageByFragment(fragmentMore)
                setTitle(R.string.more)
            }
        }
        selectedTabId = targetView.id
        invalidateOptionsMenu()
        var index =0
        while(index < (targetView.parent as ViewGroup).childCount){
            (targetView.parent as ViewGroup).getChildAt(index).isSelected =
                    targetView == (targetView.parent as ViewGroup).getChildAt(index)
            index ++
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.scroll)?.isVisible =selectedTabId == R.id.tab1
        menu?.findItem(R.id.star)?.isVisible = selectedTabId== R.id.tab1
        menu?.findItem(R.id.search)?.isVisible = selectedTabId == R.id.tab2
        menu?.findItem(R.id.coin_collection)?.isVisible = selectedTabId == R.id.tab2
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val searchMenu = menu?.findItem(R.id.search)
        searchView = searchMenu?.actionView as SearchView
        setupSearchView(searchView)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId ?: 0 == R.id.scroll) {
            if (currentFragment == fragmentNews) {
                fragmentNews?.scrollToTop()
            }
        } else if (item?.itemId ?: 0 == R.id.star) {
            isStarChecked = !isStarChecked
            if (isStarChecked) {
                item?.setIcon(R.drawable.ic_home_white_24dp)
                setTitle(R.string.star)
            } else {
                item?.setIcon(R.drawable.ic_star_black_24dp)
                setTitle(R.string.fast_information)
            }
            fragmentNews?.switchContent()
        } else if (item?.itemId ?: 0 == android.R.id.home) {
            if (currentFragment == fragmentMarket) shrinkSearchView()
        } else if (item?.itemId == R.id.coin_collection) {
            MyApplication.instance.jumpActivity(CollectionCoinsActivity::class.java, null)
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        if (timerHandler == null)
            timerHandler = TimerHandler(this)
        timerHandler!!.sendEmptyMessageDelayed(TimerHandler.move, TimerHandler.delayMillis)
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.removeMessages(TimerHandler.move)
        shrinkSearchView()
        updateDialog?.cancel()
    }


    override fun onBackPressed() {
        if (searchView?.isIconified == false) {
            shrinkSearchView()
        } else
            moveTaskToBack(true)
    }

    override fun onTime() {
        if (fragmentMarket?.isVisible == true)
            fragmentMarket?.netStep(false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    private fun initFragments() {

        val bundle1 = Bundle()
        fragmentNews = FragmentNews.createInstance(bundle1)
        fragmentList.add(fragmentNews!!)

        val bundle2 = Bundle()
        fragmentMarket = FragmentMarket.createInstance(bundle2)
        fragmentList.add(fragmentMarket!!)

        val bundle3 = Bundle()
        fragmentAssert = FragmentAssert.createInstance(bundle3)
        fragmentList.add(fragmentAssert!!)

        val bundle4 = Bundle()
        fragmentMore = FragmentMore.createInstance(bundle4)
        fragmentList.add(fragmentMore!!)
        switchPageByIndex(0)
        tab1.isSelected = true

    }

    private fun switchPageByIndex(pageIndex: Int) {
        assert(pageIndex < 3)
        val ft = fragmentManager.beginTransaction()
        currentFragment = if (currentFragment == null) {
            ft.add(R.id.main_fragment_container, fragmentNews)
            fragmentNews
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
            ft.add(R.id.main_fragment_container, fragmentNews)
            fragmentNews
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
                    if (getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("update", false)
                            || updateInfo.force_update) {
                        download(updateInfo.android_url, updateInfo.latest_app_version)
                        Toast.makeText(MyApplication.instance, R.string.auto_update_on_go, Toast.LENGTH_LONG).show()
                    } else {
                        showUpdateDialog(updateInfo)
                    }
                }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Log.i("CheckUpdate", message)
            }
        })
    }

    private var updateDialog: AlertDialog? = null

    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        if (isFinishing) return
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.update)
                .setMessage(updateInfo?.version_info ?: "test message")
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                    updateDialog = null
                }.setPositiveButton(R.string.update) { dialog, _ ->
                    dialog.cancel()
                    updateDialog = null
                    download(updateInfo.android_url, updateInfo.latest_app_version)
                }.setOnCancelListener({ updateDialog = null })
        updateDialog = dialogBuilder.create()
        updateDialog?.show()
    }


    private fun setupSearchView(searchView: SearchView?) {
        if (searchView == null) return
        searchView.queryHint = getString(R.string.coin_symbol)
        searchView.setOnCloseListener({
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            false
        })
        searchView.setOnSearchClickListener({
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                doSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }


    private fun shrinkSearchView() {
        searchView?.findViewById<TextView>(R.id.search_src_text)?.text = ""
        searchView?.isIconified = true
    }


    private fun doSearch(query: String?) {

        if (TextUtils.isEmpty(query)) return
        val bundle = Bundle()
        bundle.putString("key", query)
        MyApplication.instance.jumpActivity(SearchActivity::class.java, bundle)
    }
}
