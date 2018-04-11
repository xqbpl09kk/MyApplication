package fast.information

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton
import android.widget.Toast
import com.umeng.message.IUmengCallback
import com.umeng.message.PushAgent
import fast.information.common.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by xiaqibo on 2018/3/23.
 */
class SettingsActivity :BaseActivity() {

    private var push_enabled_at_activity_create = false

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
}