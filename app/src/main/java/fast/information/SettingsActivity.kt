package fast.information

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.MenuRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.PopupMenu
import android.widget.Toast
import com.google.gson.Gson
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.main.MainActivity
import fast.information.network.RetrofitHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

/**
 * Created by xiaqibo on 2018/3/23.
 */
class SettingsActivity :BaseActivity() , PopupMenu.OnMenuItemClickListener {


    private var push_enabled_at_activity_create = false
    private val rateArray = Arrays.asList(2 * 60 , 5 * 60 , 10 * 60 , 15 *60 )
    override fun getLayoutRes(): Int {
        return (R.layout.activity_settings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreference = getSharedPreferences("settings" ,Context.MODE_PRIVATE)
        push_enabled_at_activity_create =sharedPreference .getBoolean("push" , true)
        push_switch.isChecked = push_enabled_at_activity_create
        push_switch.setOnCheckedChangeListener { _, isChecked -> sharedPreference.edit().putBoolean("push" , isChecked).apply() }
        auto_update.isChecked = sharedPreference.getBoolean("update" , false)
        auto_update.setOnCheckedChangeListener { _, isChecked -> sharedPreference.edit().putBoolean("update" , isChecked).apply() }
        rate_container.setOnClickListener({ createMenu(refresh_rate_selector ,R.menu.refresh_rate_items) })
        val refreshRate = getSharedPreferences("settings" , Context.MODE_PRIVATE).getInt("refresh_rate" , 2*60)
        when(refreshRate){
            rateArray[0] -> refresh_rate_selector.setText(R.string.two_second)
            rateArray[1] -> refresh_rate_selector.setText(R.string.five_second)
            rateArray[2] -> refresh_rate_selector.setText(R.string.ten_second)
            rateArray[3] -> refresh_rate_selector.setText(R.string.fiveteen_second)
        }
        login_out.visibility = if(RetrofitHelper.auth == null) View.GONE else View.VISIBLE
        login_out.setOnClickListener({logout()})
        language_container.setOnClickListener({createMenu(language_selector ,R.menu.language_items) })
        val language = getSharedPreferences("settings", Context.MODE_PRIVATE).getString("language" , "auto")
        when(language){
            "english" ->{
                language_selector.text = getString(R.string.english)
            }
            "china" ->{
                language_selector.text = getString(R.string.chinese)
            }
            "auto" ->{
                language_selector.text = getString(R.string.auto)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val push :Boolean = getSharedPreferences("settings" ,Context.MODE_PRIVATE).getBoolean("push" , true)
//        if(push != push_enabled_at_activity_create){
//            val mPushAgent = PushAgent.getInstance(this)
//            if(push){
//                mPushAgent.enable(object : IUmengCallback {
//                    override fun onSuccess() { }
//                    override fun onFailure(p0: String?, p1: String?) { }
//                })
//            }else{
//                mPushAgent.disable(object : IUmengCallback {
//                    override fun onSuccess() { }
//                    override fun onFailure(p0: String?, p1: String?) { }
//                })
//            }
//        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item1 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[0]).apply()
                refresh_rate_selector.setText(R.string.two_second)
                TimerHandler.resetDelay()
            }
            R.id.item2 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[1]).apply()
                refresh_rate_selector.setText(R.string.five_second)
                TimerHandler.resetDelay()
            }
            R.id.item3 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[2]).apply()
                refresh_rate_selector.setText(R.string.ten_second)
                TimerHandler.resetDelay()
            }
            R.id.item4 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[3]).apply()
                refresh_rate_selector.setText(R.string.fiveteen_second)
                TimerHandler.resetDelay()
            }
            R.id.item5 -> {
                val configuration : Configuration = resources.configuration
                configuration.locale = Locale.CHINA
                resources.updateConfiguration(configuration , resources.displayMetrics)
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putString("language" , "china").apply()
                val intent = Intent(this@SettingsActivity , MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.item6 -> {
                val configuration : Configuration = resources.configuration
                configuration.locale = Locale.ENGLISH
                resources.updateConfiguration(configuration , resources.displayMetrics)
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putString("language" , "english").apply()
                val intent = Intent(this@SettingsActivity , MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                view.invalidate()
                finish()
            }
            R.id.item7 -> {
                val configuration : Configuration = resources.configuration
//                configuration.locale = Locale.getDefault()
                configuration.locale =  resources.configuration.locale
                resources.updateConfiguration(configuration , resources.displayMetrics)
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putString("language" , "auto").apply()
                val intent = Intent(this@SettingsActivity , MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        return true
    }


    private fun logout(){
        RetrofitHelper.auth = null
        MyApplication.instance.saveAuth(null)
        Toast.makeText(MyApplication.instance , R.string.logout_success , Toast.LENGTH_SHORT).show()
        login_out.visibility = View.GONE
    }

    private fun createMenu(view : View ,@MenuRes menuId :Int ){
        val popupMenu = PopupMenu(MyApplication.instance, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(menuId, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(this@SettingsActivity)
    }
}