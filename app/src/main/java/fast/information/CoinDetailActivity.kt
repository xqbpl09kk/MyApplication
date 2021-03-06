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
import com.bumptech.glide.Glide
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.network.RetrofitHelper
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import kotlinx.android.synthetic.main.activity_coin_detail.*
import kotlinx.android.synthetic.main.list_item_muilt_cardboard.*
import kotlinx.android.synthetic.main.list_item_muilt_cardboard.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by xiaqibo on 2018/4/10.
 */
class CoinDetailActivity : BaseActivity() , TimerHandler.Timer{


    override fun getLayoutRes(): Int {
        return R.layout.activity_coin_detail
    }
    //detail


    private var tickerItem: TickerListItem? = null
    private var timerHandler : TimerHandler?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(R.color.half_trans)
    }

    //change2 
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.coin_detail, menu)
        val sharedPreferences = getSharedPreferences("cache", Context.MODE_PRIVATE)
        val collectionCoins = sharedPreferences.getStringSet("collection_coins", HashSet<String>())
        val symbol = tickerItem?.symbol
        if (collectionCoins.contains(symbol)) {
            menu?.findItem(R.id.collection)?.setIcon(R.drawable.ic_star_black_24dp)
        } else {
            menu?.findItem(R.id.collection)?.setIcon(R.drawable.ic_star_white_24dp)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.refresh) {
            refresh()
        } else if (item?.itemId == R.id.collection) {
            val sharedPreferences = getSharedPreferences("cache", Context.MODE_PRIVATE)
            val collectionCoins = sharedPreferences.getStringSet("collection_coins", HashSet<String>())
            val symbol = tickerItem?.symbol
            if (collectionCoins.contains(symbol)) {
                collectionCoins.remove(symbol)
                item.setIcon(R.drawable.ic_star_white_24dp)
            } else {
                collectionCoins.add(symbol)
                item.setIcon(R.drawable.ic_star_black_24dp)
            }
            sharedPreferences.edit().clear().putStringSet("collection_coins", collectionCoins).apply()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun registerViews() {
        super.registerViews()

        if (tickerItem == null) {
            val bundle: Bundle = intent.getBundleExtra("data")
            tickerItem = bundle.get("ticker_item") as TickerListItem?
        }
        if (tickerItem == null) {
            Toast.makeText(MyApplication.instance, R.string.error_data, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (TextUtils.isEmpty(tickerItem?.icon)) {
            icon?.visibility = View.GONE
        } else {
            icon?.visibility = View.VISIBLE
            Glide.with(MyApplication.instance).load(tickerItem?.icon).into(icon)
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

        change_group.setOnClickListener({refresh()})

        introduction.text = Html.fromHtml(tickerItem!!.introduction).trim().toString()

        used_amount.text = insertDot(StringBuilder(tickerItem!!.available_supply ?: ""))
        total_amount.text = insertDot(StringBuilder(tickerItem!!.total_supply ?: ""))
        max_amount.text = insertDot(StringBuilder(tickerItem!!.max_supply ?: ""))
        market_cap.text = insertDot(StringBuilder(tickerItem!!.market_cap_usd ?: ""))

        office_sites.text = ""
        for (site: String in tickerItem!!.official_sites ?: ArrayList()) {
            if (TextUtils.isEmpty(site)) continue
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


        block_sites.text = ""
        for (block: String in tickerItem!!.block_sites ?: ArrayList()) {
            if (TextUtils.isEmpty(block)) continue
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

        white_paper.text = ""
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

//    override fun onBackPressed() {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            finishAfterTransition()
//        } else
//            finish()
//    }


    override fun onResume() {
        super.onResume()
        if(timerHandler == null)
            timerHandler = TimerHandler(this)
        timerHandler!!.sendEmptyMessageDelayed(TimerHandler.move , TimerHandler.delayMillis)
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.removeMessages(TimerHandler.move)
    }


    override fun onTime() {
        refresh()
    }


    private fun insertDot(str: StringBuilder): String {
        if (TextUtils.isEmpty(str)) return str.toString()
        var i: Int = str.length - 3
        while (i > 0) {
            str.insert(i, ",")
            i -= 3
        }
        return str.toString()
    }

    private fun refresh() {
        RetrofitHelper.instance.tickerItem(tickerItem!!.symbol!!, object : ResultCallback<ResultBundle<TickerListItem>> {
            override fun onSuccess(t: ResultBundle<TickerListItem>?) {
                Toast.makeText(MyApplication.instance, R.string.refresh_success, Toast.LENGTH_SHORT).show()
                tickerItem = t?.item
                registerViews()
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance, message, Toast.LENGTH_SHORT).show()
            }

        })


    }
}
