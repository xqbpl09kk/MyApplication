package fast.information

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.TickerListItem
import kotlinx.android.synthetic.main.activity_coin_detail.*
import kotlinx.android.synthetic.main.list_item_muilt_cardboard.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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
        window.setBackgroundDrawableResource(R.color.half_trans)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.coin_detail , menu)
        val sharedPreferences = getSharedPreferences("settings" , Context.MODE_PRIVATE)
        val collectionCoins = sharedPreferences.getStringSet("collection_coins" , HashSet<String>())
        val symbol = tickerItem?.symbol
        if(collectionCoins.contains(symbol)){
            menu?.findItem(R.id.collection)?.setIcon(R.drawable.ic_star_black_24dp)
        }  else{
            menu?.findItem(R.id.collection)?.setIcon(R.drawable.ic_star_white_24dp)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.refresh){
            refresh()
        }else if(item?.itemId == R.id.collection){
            val sharedPreferences = getSharedPreferences("settings" , Context.MODE_PRIVATE)
            val collectionCoins = sharedPreferences.getStringSet("collection_coins" , HashSet<String>())
            val symbol = tickerItem?.symbol
            if(collectionCoins.contains(symbol)){
                collectionCoins.remove(symbol)
                item.setIcon(R.drawable.ic_star_white_24dp)
            }else{
                collectionCoins.add(symbol)
                item.setIcon(R.drawable.ic_star_black_24dp)
            }
            sharedPreferences.edit().putStringSet("collection_coins" , collectionCoins).apply()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun registerViews() {
        super.registerViews()
        val bundle: Bundle = intent.getBundleExtra("data")
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

//        val pattern = Pattern.compile("<[A-Z1-9a-z_\\W\\s\\r\":;',()-= \n]*>")
//        val matcher: Matcher = pattern.matcher(tickerItem!!.introduction)
//        introduction.text = matcher.replaceAll("").trim()
        introduction.text = Html.fromHtml(tickerItem!!.introduction ).trim()

        used_amount.text = insertDot(StringBuilder(tickerItem!!.available_supply))
        total_amount.text = insertDot(StringBuilder(tickerItem!!.total_supply))
        max_amount.text = insertDot(StringBuilder(tickerItem!!.max_supply))
        market_cap.text = insertDot(StringBuilder(tickerItem!!.market_cap_usd))

        for (site: String in tickerItem!!.official_sites ?: ArrayList()) {
            val spanString = SpannableString(site)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(site)), "sss"))
                }
            }
            spanString.setSpan(clickableSpan, 0, site.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            office_sites.append(spanString)
            if (tickerItem!!.official_sites!!.indexOf(site) != tickerItem!!.official_sites!!.size - 1) {
                office_sites.append(" ;\n")
            }
        }
        office_sites.movementMethod = LinkMovementMethod.getInstance()
        for (block: String in tickerItem!!.block_sites ?: ArrayList()) {
            val spanString = SpannableString(block)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(block)), "sss"))
                }
            }
            spanString.setSpan(clickableSpan, 0, block.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            block_sites.append(spanString)
            if (tickerItem!!.block_sites!!.indexOf(block) != tickerItem!!.block_sites!!.size - 1) {
                block_sites.append(" ;\n")
            }
        }
        block_sites.movementMethod = LinkMovementMethod.getInstance()

        if (!TextUtils.isEmpty(tickerItem!!.white_paper)) {
            val spanString = SpannableString(tickerItem!!.white_paper)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View?) {
                    startActivity(Intent.createChooser(Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse(tickerItem!!.white_paper)), "sss"))
                }
            }
            spanString.setSpan(clickableSpan, 0, tickerItem!!.white_paper!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            white_paper.append(spanString)
            white_paper.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else
            finish()
    }

    private fun insertDot(str: StringBuilder): String {
        var i: Int = str.length - 3
        while (i > 0) {
            str.insert(i, ",")
            i -= 3
        }
        return str.toString()
    }

    private fun refresh(){
        Toast.makeText(MyApplication.instance , "Refreshing " , Toast.LENGTH_LONG).show()
//     RetrofitHelper.instance.tickerList()
    }
}