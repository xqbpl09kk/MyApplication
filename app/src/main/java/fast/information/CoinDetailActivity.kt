package fast.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.bean.TickerListItem
import kotlinx.android.synthetic.main.activity_coin_detail.*
import kotlinx.android.synthetic.main.list_item_muilt_cardboard.*

/**
 * Created by xiaqibo on 2018/4/10.
 */
class CoinDetailActivity : BaseActivity() {
    override fun getLayoutRes(): Int {
        return R.layout.activity_coin_detail
    }

    private var tickerItem: TickerListItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun registerViews() {
        super.registerViews()
        val bundle: Bundle = intent.extras.get("data") as Bundle
        tickerItem = bundle.get("ticker_item") as TickerListItem?
        if (tickerItem == null) {
            Toast.makeText(MyApplication.instance, R.string.error_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        name.text = tickerItem!!.symbol
        volume_24h.text = "24H:".plus(String.format("%.02f", (tickerItem!!.__h_volume_usd?.toLong()
                ?: 0) / 1000000000f).plus("B"))
        cap.text = "TAL:".plus(String.format("%.02f", (tickerItem!!.market_cap_usd?.toLong()
                ?: 0) / 1000000000f).plus("B"))
        price.text = tickerItem!!.price

        val change1h = tickerItem!!.percent_change_1h?.toFloat() ?: Float.MIN_VALUE
        when {
            change1h > 0 -> change_1h.text = "+".plus(tickerItem!!.percent_change_1h.plus("%/1h"))
            else -> change_1h.text = tickerItem!!.percent_change_1h.plus("%/1h")
        }

        val change24h = tickerItem!!.percent_change_24h?.toFloat() ?: Float.MIN_VALUE
        when {
            change24h > 0 -> {
                val spanStringBuilder = SpannableStringBuilder("+".plus(tickerItem!!.percent_change_24h).plus("%"))
                spanStringBuilder.setSpan(ForegroundColorSpan(MyApplication.instance.colorGreen!!), 0, spanStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                change_24h.text = spanStringBuilder.append("/24h")
            }
            change24h < 0 -> {
                val spanStringBuilder = SpannableStringBuilder(tickerItem!!.percent_change_24h.plus("%"))
                spanStringBuilder.setSpan(ForegroundColorSpan(MyApplication.instance.colorRed!!), 0, spanStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                change_24h.text = spanStringBuilder.append("/24h")
            }
            else -> change_24h.text = tickerItem!!.percent_change_24h.plus("%/24h")
        }

        val change7d = tickerItem!!.percent_change_7d?.toFloat() ?: Float.MIN_VALUE
        when {
            change7d > 0 -> change_7d.text = "+".plus(tickerItem!!.percent_change_7d.plus("%/7d"))
            else -> change_7d.text = tickerItem!!.percent_change_7d.plus("%/7d")
        }

        introduction.text = tickerItem!!.introduction!!.replace("<p>" , "" ,false).replace("</p>" , "" ,false).trim()


        used_amount.text = insertDot(StringBuilder(tickerItem!!.available_supply))
        total_amount.text = insertDot(StringBuilder(tickerItem!!.total_supply))
        for (site :String in tickerItem!!.office_sites ?: ArrayList()){
            val spanString = SpannableString(site)
            val clickableSpan :ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW ) .setData(Uri.parse(site)) ,"sss"))
                }
            }
            spanString.setSpan(clickableSpan , 0 ,site.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            office_sites.append(spanString)
            if(tickerItem!!.office_sites!!.indexOf(site) != tickerItem!!.office_sites!!.size -1){
                block_sites.append(" ;\n")
            }
        }
        office_sites.movementMethod = LinkMovementMethod.getInstance()
        for (block :String in tickerItem!!.block_sites ?: ArrayList()){
            val spanString = SpannableString(block)
            val clickableSpan :ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW ) .setData(Uri.parse(block)) ,"sss"))
                }
            }
            spanString.setSpan(clickableSpan , 0 ,block.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            block_sites.append(spanString)
            if(tickerItem!!.block_sites!!.indexOf(block) != tickerItem!!.block_sites!!.size -1){
                block_sites.append(" ;\n")
            }
        }
        block_sites.movementMethod = LinkMovementMethod.getInstance()

        if(!TextUtils.isEmpty(tickerItem!!.white_paper)){
            val spanString = SpannableString(tickerItem!!.white_paper)
            val clickableSpan :ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW ).setData(Uri.parse(tickerItem!!.white_paper)) ,"sss"))
                }
            }
            spanString.setSpan(clickableSpan , 0 ,tickerItem!!.white_paper!!.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            white_paper.append(spanString)
            white_paper.movementMethod = LinkMovementMethod.getInstance()
        }
    }


    private fun insertDot(str:StringBuilder):String{
        var i : Int = str.length -3
        while(i > 0){
            str.insert(i , ",")
            i -= 3
        }
        return str.toString()
    }
}