package fast.information.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import fast.information.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager  : FragmentManager = supportFragmentManager
    private val fragmentList: ArrayList<Fragment> = ArrayList()
    private var fragmentOne  : FragmentOne ? = null
    private var fragmentTwo : FragmentTwo ?= null
    private var fragmentThree  : FragmentThree ?= null
    private var currentFragment : Fragment  ?= null
    private var isStarChecked :Boolean = false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("tag", "onCreate")
        setContentView(R.layout.activity_main)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        initFragments()
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
}
