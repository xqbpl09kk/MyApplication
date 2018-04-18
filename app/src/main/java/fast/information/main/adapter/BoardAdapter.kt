package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import fast.information.CoinDetailActivity
import fast.information.R
import fast.information.common.MyApplication
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
        holder.itemView.index.text = (position + 1) .toString()
        if(TextUtils.isEmpty(itemData.icon)){
            holder.itemView.icon.visibility = View.GONE
        }else{
            holder.itemView.icon.visibility = View.VISIBLE
            Glide.with(context).load(itemData.icon).into(holder.itemView.icon)
        }
        holder.itemView.name.text =  itemData.symbol
        holder.itemView.price.text = itemData.price
        var change = itemData.percent_change_24h?.toFloat() ?: Float.MIN_VALUE
        when (currentSortMode) {
            1 -> {
                change = itemData.percent_change_1h?.toFloat() ?: Float.MIN_VALUE
                holder.itemView.unit.text = "/1h"
            }
            3 ->{
                change = itemData.percent_change_7d?.toFloat()?:Float.MIN_VALUE
                holder.itemView.unit.text = "/7d"
            }
        }
        when {
            change > 0 -> {
                holder.itemView.change.text = "+".plus(change).plus("%")
                holder.itemView.change.setTextColor(MyApplication.instance.colorGreen!!)
            }
            change < 0 -> {
                holder.itemView.change.text = change.toString().plus("%")
                holder.itemView.change.setTextColor(MyApplication.instance.colorRed!!)
            }
            else -> {
                holder.itemView.change.text = change.toString().plus("%")
                holder.itemView.change.setTextColor(ContextCompat.getColor(context , R.color.text_normal))
            }
        }
        holder.itemView.setOnClickListener({
            val bundle = Bundle()
            bundle.putSerializable("ticker_item" , itemData)
            MyApplication.instance.jumpActivity(CoinDetailActivity::class.java , bundle)
        })
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