package fast.information.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fast.information.ConcernActivity
import fast.information.LinkEditActivity
import fast.information.common.MyApplication
import fast.information.R
import fast.information.SettingsActivity
import fast.information.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.more_link_item.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * MyApplication
 * Created by xiaqibo on 2018/3/1-0:18.
 */
class FragmentMore : BaseFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_more
    }

    companion object {

        fun createInstance(argBundle: Bundle): FragmentMore {
            val instance = FragmentMore()
            instance.arguments = argBundle
            return instance
        }

    }

    private val nameList  = Arrays.asList("EtherFans", "CoinMarketCap")
    private val linkList = Arrays.asList("https://ethfans.org/", "https://coinmarketcap.com/zh/")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        concern.setOnClickListener { startActivity(Intent(context, ConcernActivity::class.java)) }
        settings.setOnClickListener { startActivity(Intent(context, SettingsActivity::class.java)) }
        comment.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("market://details?id="
                                .plus(MyApplication.instance.packageName))))
            }catch (e: Exception){
                Toast.makeText(MyApplication.instance, R.string.no_market_app, Toast.LENGTH_SHORT).show()
            }
        }
        share.setOnClickListener { shareApp() }
        for (i in nameList.indices) {
            addItem(nameList[i] , linkList[i] , true)
        }
        initStoreItems()
        link_title.setOnClickListener {startActivityForResult(Intent(context , LinkEditActivity::class.java) , 1001)}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1001 && resultCode == Activity.RESULT_OK){
            Handler().postDelayed({ initStoreItems() }, 500)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun shareApp(){
//        val imageUri = makeImageFile()
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
//        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
//        shareIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        shareIntent.putExtra(Intent.EXTRA_TEXT , getString(R.string.share_text))
        shareIntent.type = "text/*"
        startActivity(Intent.createChooser(shareIntent, "分享到"))
    }


    private fun initStoreItems(){
        link_layout.removeViews(4 , link_layout.childCount -4)
        val urls = MyApplication.instance.getSharedPreferences("links" , Context.MODE_PRIVATE).getString("data" , "")
        if(!TextUtils.isEmpty(urls)){
            val gson  = Gson()
            val data = gson.fromJson<ArrayList<LinkEditActivity.Companion.Item>>(urls
                    , object : TypeToken<ArrayList<LinkEditActivity.Companion.Item>>() {}.type)
            for(item  in data){
                addItem(item.name , item.url , false)
            }
        }
    }

    private fun addItem(name :String ? , url :String ? , showPin:Boolean ){
        val contentView = LayoutInflater.from(MyApplication.instance).inflate(R.layout.more_link_item, link_layout, false)
        contentView.name.text = name
        contentView.link.text = url
        contentView.pin.visibility = if(showPin) View.VISIBLE else View.GONE
        link_layout.addView(contentView)
        contentView.setOnClickListener {
            try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(MyApplication.instance, R.string.no_browser_client_and_copy_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }
    }

}