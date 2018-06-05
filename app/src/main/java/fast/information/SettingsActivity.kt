package fast.information

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.PopupMenu
import android.widget.Toast
import com.google.gson.Gson
import com.umeng.message.IUmengCallback
import com.umeng.message.PushAgent
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.network.RetrofitHelper
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
        rate_container.setOnClickListener({ createMenu(refresh_rate_selector) })
        val refreshRate = getSharedPreferences("settings" , Context.MODE_PRIVATE).getInt("refresh_rate" , 2*60)
        when(refreshRate){
            rateArray[0] -> refresh_rate_selector.setText(R.string.two_second)
            rateArray[1] -> refresh_rate_selector.setText(R.string.five_second)
            rateArray[2] -> refresh_rate_selector.setText(R.string.ten_second)
            rateArray[3] -> refresh_rate_selector.setText(R.string.fiveteen_second)
        }
        login_out.visibility = if(RetrofitHelper.auth == null) View.GONE else View.VISIBLE
        login_out.setOnClickListener({logout()})
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val push :Boolean = getSharedPreferences("settings" ,Context.MODE_PRIVATE).getBoolean("push" , true)
        if(push != push_enabled_at_activity_create){
            val mPushAgent = PushAgent.getInstance(this)
            if(push){
                mPushAgent.enable(object : IUmengCallback {
                    override fun onSuccess() { }
                    override fun onFailure(p0: String?, p1: String?) { }
                })
            }else{
                mPushAgent.disable(object : IUmengCallback {
                    override fun onSuccess() { }
                    override fun onFailure(p0: String?, p1: String?) { }
                })
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item1 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[0]).apply()
                refresh_rate_selector.setText(R.string.two_second)
            }
            R.id.item2 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[1]).apply()
                refresh_rate_selector.setText(R.string.five_second)
            }
            R.id.item3 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[2]).apply()
                refresh_rate_selector.setText(R.string.ten_second)
            }
            R.id.item4 -> {
                getSharedPreferences("settings" , Context.MODE_PRIVATE).edit().putInt("refresh_rate" , rateArray[3]).apply()
                refresh_rate_selector.setText(R.string.fiveteen_second)
            }
        }
        TimerHandler.resetDelay()
        return true
    }


    private fun logout(){
        RetrofitHelper.auth = null
        MyApplication.instance.saveAuth(null)
        Toast.makeText(MyApplication.instance , R.string.logout_success , Toast.LENGTH_SHORT).show()
        login_out.visibility = View.GONE
    }

    private fun createMenu(view : View){
        val popupMenu = PopupMenu(MyApplication.instance, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.refresh_rate_items, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(this@SettingsActivity)
    }
}