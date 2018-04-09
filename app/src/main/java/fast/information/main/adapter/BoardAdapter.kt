package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fast.information.R
import fast.information.network.bean.TickerListItem
import kotlinx.android.synthetic.main.list_item_cardboard.view.*

/**
 * Created by xiaqibo on 2018/4/3.
 */
class BoardAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data : ArrayList<TickerListItem> = ArrayList()
    var currentSortMode = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val contentView  = layoutInflater.inflate(R.layout.list_item_cardboard , parent , false)
        return object : RecyclerView.ViewHolder(contentView) {}
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = data[position]
        holder.itemView.name.text = Integer.toString(position +1).plus("、").plus(itemData.symbol)
        holder.itemView.volume_24h.text = itemData.price.plus("/USDT")
        var change = itemData.percent_change_24h?.toFloat() ?: Float.MIN_VALUE
        var unit = "/24h"
        when (currentSortMode) {
            1 -> {
                change = itemData.percent_change_1h?.toFloat() ?: Float.MIN_VALUE
                unit = "/1h"
            }
            3 ->{
                change = itemData.percent_change_7d?.toFloat()?:Float.MIN_VALUE
                unit = "/7d"
            }
        }
        when {
            change > 0 -> {
                holder.itemView.textView3.text = "+".plus(change).plus("%").plus(unit)
                holder.itemView.textView3.setTextColor(Color.GREEN)
            }
            change < 0 -> {
                holder.itemView.textView3.text = "-".plus(change).plus("%").plus(unit)
                holder.itemView.textView3.setTextColor(Color.RED)
            }
            else -> {
                holder.itemView.textView3.text = change.toString().plus("%").plus(unit)
                holder.itemView.textView3.setTextColor(ContextCompat.getColor(context , R.color.text_normal))
            }
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

}