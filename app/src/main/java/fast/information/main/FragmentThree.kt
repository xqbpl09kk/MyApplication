package fast.information.main

import android.app.Activity
import android.arch.lifecycle.LifecycleObserver
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fast.information.ConcernActivity
import fast.information.LinkEditActivity
import fast.information.common.MyApplication
import fast.information.R
import fast.information.SettingsActivity
import fast.information.common.BaseFragment
import kotlinx.android.synthetic.main.activity_share.view.*

import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.more_link_item.view.*
import java.util.*
import java.util.EnumSet.range
import kotlin.collections.ArrayList

/**
 * MyApplication
 * Created by xiaqibo on 2018/3/1-0:18.
 */
class FragmentThree : BaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_more
    }

    companion object {

        fun createInstance(argBundle: Bundle): FragmentThree {
            val instance = FragmentThree()
            instance.arguments = argBundle
            return instance
        }

    }

    private val nameList  = Arrays.asList("以太坊爱好者", "coinmarketcap")
    private val linkList = Arrays.asList("https://ethfans.org/", "https://coinmarketcap.com/zh/")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        concern.setOnClickListener({ startActivity(Intent(context, ConcernActivity::class.java)) })
        settings.setOnClickListener({ startActivity(Intent(context, SettingsActivity::class.java)) })
        comment.setOnClickListener({
            try {
                startActivity(Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("market://details?id="
                                .plus(MyApplication.instance.packageName))))
            } catch (e: Exception) {
                Toast.makeText(MyApplication.instance, R.string.no_market_app, Toast.LENGTH_SHORT).show()
            }
        })

        for (i in nameList.indices) {
            addItem(nameList[i] , linkList[i])
        }
        initStoreItems()
        link_title.setOnClickListener({startActivityForResult(Intent(context , LinkEditActivity::class.java) , 1001)})
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1001 && resultCode == Activity.RESULT_OK){
            Handler().postDelayed({ initStoreItems() }, 500)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initStoreItems(){
        link_layout.removeViews(3 , link_layout.childCount -3)
        val urls = MyApplication.instance.getSharedPreferences("links" , Context.MODE_PRIVATE).getString("data" , "")
        if(!TextUtils.isEmpty(urls)){
            val gson  = Gson()
            val data = gson.fromJson<ArrayList<LinkEditActivity.Companion.Item>>(urls
                    , object : TypeToken<ArrayList<LinkEditActivity.Companion.Item>>() {}.type)
            for(item  in data){
                addItem(item.name , item.url)
            }
        }
    }

    private fun addItem(name :String ? , url :String ?){
        val contentView = LayoutInflater.from(MyApplication.instance).inflate(R.layout.more_link_item, link_layout, false)
        contentView.name.text = name
        contentView.link.text = url
        link_layout.addView(contentView)
        contentView.setOnClickListener({
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(MyApplication.instance, R.string.no_browser_client_and_copy_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        })
    }

}