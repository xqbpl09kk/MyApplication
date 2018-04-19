package fast.information.main

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import fast.information.ConcernActivity
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

    private val nameList = Arrays.asList("巴比特", "以太坊爱好者"
            , "链世界", "区块链中文网", "coinmarketcap")
    private val linkList = Arrays.asList("http://www.8btc.com/"
            , "https://ethfans.org/", "https://www.7234.cn/"
            , "http://www.qukuainews.cn/", "https://coinmarketcap.com/zh/")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)
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

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.coin_detail , menu)
    }

}