package fast.information

import android.annotation.SuppressLint
import android.app.Activity
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.AssertGroup
import fast.information.network.bean.AuthItem
import fast.information.network.bean.Regions
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by xiaqibo on 2018/5/24.
 */
class LoginActivity :BaseActivity() {


    private var emailLogin :Boolean = false
    private var accountPaddingRight :Int = 0
    private var accountPaddingTop :Int = 0
    private var accountPaddingBottom :Int = 0



    override fun getLayoutRes(): Int {
        return R.layout.activity_login
    }


    override fun registerViews() {
        super.registerViews()
        accountPaddingRight = account_editor.paddingRight
        accountPaddingBottom = account_editor.paddingBottom
        accountPaddingTop = account_editor.paddingTop
        login_btn.setOnClickListener({ if(accountIsEmail()) loginEmail()
        else Toast.makeText(MyApplication.instance , R.string.illegal_email , Toast.LENGTH_SHORT).show()})
        switch_email.setOnClickListener({ switch() })
        text_area.setOnClickListener({showAreaDialog()})
        area_code.setOnClickListener({captureCode()})
    }


    private fun showAreaDialog(){
        RetrofitHelper.instance.regions(object : ResultCallback <ResultBundle<Regions>>{
            override fun onSuccess(t: ResultBundle<Regions>?) {
                if( t?.item?.countries != null ){
                    Toast.makeText(MyApplication.instance , "success!!" , Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , "failed!!" , Toast.LENGTH_SHORT).show()
            }

        })
//
    }

    private fun accountIsEmail():Boolean{
        val pattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
        val matcher: Matcher = pattern.matcher(account_editor.text.toString())
        return matcher.matches()
    }

    private fun captureCode(){
        if(TextUtils.isEmpty(account_editor.text.toString())){
            Toast.makeText(MyApplication.instance , R.string.complete_infor , Toast.LENGTH_SHORT).show()
            return
        }
        RetrofitHelper.instance.captureCode("+86" ,account_editor.text.toString()
                , object : ResultCallback <ResultBundle<String>>{
            override fun onSuccess(t: ResultBundle<String>?) {
               Toast.makeText(MyApplication.instance , "success" ,Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , "failed" ,Toast.LENGTH_SHORT).show()
            }

        })
    }

    @SuppressLint("HardwareIds")
    private fun loginPhone(){
        if(TextUtils.isEmpty(account_editor.text.toString())
                || TextUtils.isEmpty(psd_editor.text.toString())){
            Toast.makeText(MyApplication.instance , R.string.complete_infor , Toast.LENGTH_SHORT).show()
            return
        }
        RetrofitHelper.instance.phoneAuth("+86"
                , account_editor.text.toString() , psd_editor.text.toString()
                , Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                , object : ResultCallback<ResultBundle<AuthItem>>{
            override fun onSuccess(t: ResultBundle<AuthItem>?) {
                onLoginSuccess(t?.item)
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , "failed " , Toast.LENGTH_SHORT).show()
            }

        })
    }



    @SuppressLint("HardwareIds")
    private fun loginEmail(){
        if(TextUtils.isEmpty(account_editor.text.toString())
                || TextUtils.isEmpty(psd_editor.text.toString())){
            Toast.makeText(MyApplication.instance , R.string.complete_infor , Toast.LENGTH_SHORT).show()
            return
        }
        RetrofitHelper.instance.emailAuth("+86"
                , account_editor.text.toString() , psd_editor.text.toString()
                , Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                , object : ResultCallback<ResultBundle<AuthItem>>{
            override fun onSuccess(t: ResultBundle<AuthItem>?) {
               onLoginSuccess(t?.item)
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , "failed " , Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun onLoginSuccess(authItem :AuthItem?){
        RetrofitHelper.auth = authItem
        when {
            authItem == null -> Toast.makeText(MyApplication.instance , "login failed " , Toast.LENGTH_SHORT).show()
            authItem.is_new == true -> RetrofitHelper.instance.createAssertGroup("default" , object : ResultCallback<ResultBundle<AssertGroup>> {
                override fun onSuccess(t: ResultBundle<AssertGroup>?) {
                    Log.i("LoginActivity" , "create default group success !")
                    MyApplication.instance.saveAuth(RetrofitHelper.auth)
                    setResult(Activity.RESULT_OK)
                    Toast.makeText(MyApplication.instance , "login success " , Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onFailure(message: String, errorCode: Int) {
                    Toast.makeText(MyApplication.instance , "failed " , Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity" , "create default group failed !!!!!!!")
                }
            })
            else -> Toast.makeText(MyApplication.instance , "login success " , Toast.LENGTH_SHORT).show()
        }

    }

    private fun switch(){
        if(emailLogin){
            text_area.visibility = View.VISIBLE
            area_code.visibility = View.VISIBLE
            account_editor.hint = getString(R.string.telephone)
            account_editor.setPadding((resources.displayMetrics.density * 50).toInt()
                    , accountPaddingTop , accountPaddingRight , accountPaddingBottom )
            account_editor.inputType = InputType.TYPE_CLASS_PHONE

        }else{
            text_area.visibility = View.GONE
            area_code.visibility = View.GONE
            account_editor.hint = getString(R.string.email)
            account_editor.setPadding((resources.displayMetrics.density * 5).toInt()
                    , accountPaddingTop , accountPaddingRight , accountPaddingBottom )
            account_editor.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        emailLogin = !emailLogin
        switch_email.setText(if(emailLogin) R.string.login_phone else R.string.login_email)
    }
}