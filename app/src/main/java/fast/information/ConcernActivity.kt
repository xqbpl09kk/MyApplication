package fast.information

import android.app.DownloadManager
import android.content.*
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import fast.information.common.BaseActivity
import kotlinx.android.synthetic.main.activity_concern.*
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.text.Spannable
import android.util.Log
import android.widget.Toast
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.UpdateInfo
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import java.io.File


/**
 * Created by xiaqibo on 2018/3/23.
 */
class ConcernActivity :BaseActivity() {
    override fun getLayoutRes(): Int {
         return R.layout.activity_concern
    }


    override fun registerViews() {
        super.registerViews()
        val emailAddress = "bzhionline@163.com"
        val spanString  = SpannableString(emailAddress)
        val clickSpan :ClickableSpan  =object :ClickableSpan(){
            override fun onClick(widget: View?) {
                try{
                    val uri = Uri.parse("mailto: ".plus(emailAddress))
                    val intent = Intent(ACTION_SENDTO, uri)
                    startActivity(intent)
                }catch (e:Exception){
                    Toast.makeText(MyApplication.instance , R.string.no_email_client_and_copy_to_clipboard , Toast.LENGTH_SHORT).show()
                    val clipboardManager : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.text = emailAddress
                }

            }
        }

        spanString.setSpan(clickSpan , 0 , spanString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        email.append(spanString)
        email.movementMethod =  LinkMovementMethod.getInstance()

        val websiteAddress = "bzhi.online"
        val spanString1  = SpannableString(websiteAddress)
        val clickSpan1 :ClickableSpan  =object :ClickableSpan(){
            override fun onClick(widget: View?) {
                try{
                    val uri = Uri.parse("http://".plus(websiteAddress))
                    val intent = Intent(ACTION_VIEW, uri)
                    startActivity(intent)
                }catch (e:Exception){
                    Toast.makeText(MyApplication.instance , R.string.no_email_client_and_copy_to_clipboard , Toast.LENGTH_SHORT).show()
                    val clipboardManager : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.text = websiteAddress
                }

            }
        }

        spanString1.setSpan(clickSpan1 , 0 , spanString1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
        website.append(spanString1)
        website.movementMethod =  LinkMovementMethod.getInstance()


        try {
            val manager = packageManager
            val info = manager.getPackageInfo(packageName, 0)
            val versionName = info.versionName

            val spanString2  = SpannableString(versionName)
            val clickSpan2 :ClickableSpan  =object :ClickableSpan(){
                override fun onClick(widget: View?) {
                    checkUpdate()
                }
            }
            spanString2.setSpan(clickSpan2 , 0 , spanString2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
            version.append(spanString2)
            version.movementMethod =  LinkMovementMethod.getInstance()
        } catch (e :Exception) {
            e.printStackTrace()
            version.visibility = View.GONE
        }

    }

    private fun checkUpdate() {
        RetrofitHelper.instance.checkUpdate(object : ResultCallback<ResultBundle<UpdateInfo>> {
            override fun onSuccess(t: ResultBundle<UpdateInfo>?) {
                val updateInfo = t?.item ?: return
                if (updateInfo.latest_app_version
                        != packageManager.getPackageInfo(packageName, 0).versionName) {
                    if(getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("update" , false)){
                        download(updateInfo.android_url ,updateInfo.latest_app_version)
                        Toast.makeText(MyApplication.instance , R.string.auto_update_on_go , Toast.LENGTH_SHORT).show()
                    }else{
                        showUpdateDialog(updateInfo)
                    }
                }else{
                    Toast.makeText(MyApplication.instance , R.string.already_new_version , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        if (isFinishing) return
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@ConcernActivity)
                .setTitle(R.string.update)
                .setMessage(updateInfo.version_info ?: "test message")
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }.setPositiveButton(R.string.update) { dialog, _ ->
                    run {
                        dialog.cancel()
                        download(updateInfo.android_url ,updateInfo.latest_app_version)
                    }
                }
        dialogBuilder.create().show()
    }
}