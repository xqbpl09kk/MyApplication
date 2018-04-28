package fast.information.main

import android.arch.lifecycle.LifecycleObserver
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fast.information.ConcernActivity
import fast.information.LinkEditActivity
import fast.information.common.MyApplication
import fast.information.R
import fast.information.SettingsActivity
import fast.information.common.BaseFragment

import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.more_link_item.*
import kotlinx.android.synthetic.main.more_link_item.view.*
import java.util.*

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

    private val nameList = Arrays.asList("以太坊爱好者", "coinmarketcap")
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
            val contentView = LayoutInflater.from(MyApplication.instance).inflate(R.layout.more_link_item, link_layout, false)
            contentView.name.text = nameList[i]
            contentView.link.text = linkList[i]
            link_layout.addView(contentView)
            contentView.setOnClickListener({
                try {
                    val uri = Uri.parse(linkList[i])
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(MyApplication.instance, R.string.no_browser_client_and_copy_to_clipboard, Toast.LENGTH_SHORT).show()
                }
            })
        }

        link_title.setOnClickListener({startActivity(Intent(context , LinkEditActivity::class.java))})

    }

}