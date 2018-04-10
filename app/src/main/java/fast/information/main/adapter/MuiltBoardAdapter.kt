package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import fast.information.CoinDetailActivity
import fast.information.MarketActivity
import fast.information.R
import fast.information.common.MyApplication
import fast.information.network.bean.TickerListItem
import kotlinx.android.synthetic.main.list_item_muilt_cardboard.view.*
import java.util.*

/**
 * Created by xiaqibo on 2018/4/3.
 */
class MuiltBoardAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data : ArrayList<TickerListItem> = ArrayList()
    var currentSortMode = 0

    private val iconRes = Arrays.asList(R.drawable.btc, R.drawable.eth , R.drawable.xrp , R.drawable.bch
                ,R.drawable.ltc , R.drawable.eos , R.drawable.ada , R.drawable.xlm , R.drawable.neo
                ,R.drawable.miota , R.drawable.xmr , R.drawable.dash , R.drawable.trx , R.drawable.usdt
                ,R.drawable.xem, R.drawable.bnb , R.drawable.etc  , R.drawable.ven ,R.drawable.xvg, R.drawable.qtum)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val contentView  = when(viewType){
            1 -> layoutInflater.inflate(R.layout.list_more_text , parent , false )
            else ->  layoutInflater.inflate(R.layout.list_item_muilt_cardboard , parent , false)
        }
        return object : RecyclerView.ViewHolder(contentView) {}
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position < data.size) 0 else 1
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when{
            (position < data.size)->{
                val itemData = data[position]
                val drawable  = ContextCompat.getDrawable(context ,getIcon(itemData.symbol))
                drawable?.setBounds(0, 0, drawable.minimumWidth , drawable.minimumHeight )
                holder.itemView.name.setCompoundDrawables(drawable , null, null , null )
                holder.itemView.name.text = itemData.symbol
                holder.itemView.volume_24h.text ="24H:".plus(String.format("%.02f",(itemData.__h_volume_usd?.toLong() ?: 0) /1000000000f).plus("B"))
                holder.itemView.cap.text = "TAL:".plus(String.format("%.02f",(itemData.market_cap_usd?.toLong() ?: 0) /1000000000f).plus("B"))
                holder.itemView.price.text = itemData.price

                val change1h = itemData.percent_change_1h?.toFloat() ?: Float.MIN_VALUE
                when {
                    change1h > 0 -> holder.itemView.change_1h.text = "+".plus(itemData.percent_change_1h.plus("%/1h"))
                    else -> holder.itemView.change_1h.text = itemData.percent_change_1h.plus("%/1h")
                }

                val change24h = itemData.percent_change_24h?.toFloat() ?: Float.MIN_VALUE
                when {
                    change24h > 0 -> {
                        val spanStringBuilder = SpannableStringBuilder("+".plus(itemData.percent_change_24h).plus("%"))
                        spanStringBuilder.setSpan(ForegroundColorSpan(MyApplication.instance.colorGreen!!) ,0 , spanStringBuilder.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        holder.itemView.change_24h.text = spanStringBuilder.append("/24h")
                    }
                    change24h <0 -> {
                        val spanStringBuilder = SpannableStringBuilder(itemData.percent_change_24h.plus("%"))
                        spanStringBuilder.setSpan(ForegroundColorSpan(MyApplication.instance.colorRed!!) ,0 , spanStringBuilder.length , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        holder.itemView.change_24h.text = spanStringBuilder.append("/24h")
                    }
                    else -> holder.itemView.change_24h.text = itemData.percent_change_24h.plus("%/24h")
                }

                val change7d = itemData.percent_change_7d?.toFloat() ?: Float.MIN_VALUE
                when {
                    change7d > 0 -> holder.itemView.change_7d.text = "+".plus(itemData.percent_change_7d.plus("%/7d"))
                    else -> holder.itemView.change_7d.text = itemData.percent_change_7d.plus("%/7d")
                }
                holder.itemView.setOnClickListener({
                    val bundle = Bundle()
                    bundle.putSerializable("ticker_item" , itemData)
                    MyApplication.instance.jumpActivity(CoinDetailActivity::class.java , bundle)
                })
            }
            else -> holder.itemView.setOnClickListener({MyApplication.instance.jumpActivity(MarketActivity::class.java , null) })
        }
    }

    fun update(d: ArrayList<TickerListItem>){
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }

    fun add(d: ArrayList<TickerListItem>){
        data.addAll(d)
        notifyDataSetChanged()
    }

    private fun getIcon(name : String ?):Int {
        when(name){
            "BTC" -> return iconRes[0]
            "ETH" -> return iconRes[1]
            "XRP" -> return iconRes[2]
            "BCH" -> return iconRes[3]
            "LTC" -> return iconRes[4]
            "EOS" -> return iconRes[5]
            "ADA" -> return iconRes[6]
            "XLM" -> return iconRes[7]
            "NEO" -> return iconRes[8]
            "MIOTA" -> return iconRes[9]
            "XMR" -> return iconRes[10]
            "DASH" -> return iconRes[11]
            "TRX" -> return iconRes[12]
            "USDT" -> return iconRes[13]
            "XEM" -> return iconRes[14]
            "BNB" -> return iconRes[15]
            "ETC" -> return iconRes[16]
            "VEN" -> return iconRes[17]
            "XVG" -> return iconRes[18]
            "QTUM" -> return iconRes[19]
        }
        return R.drawable.btc
    }

}