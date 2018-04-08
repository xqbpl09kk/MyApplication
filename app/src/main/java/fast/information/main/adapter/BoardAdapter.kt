package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IntegerRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fast.information.R
import fast.information.network.bean.TickerListItem
import kotlinx.android.synthetic.main.list_item_cardboard.*
import kotlinx.android.synthetic.main.list_item_cardboard.view.*

/**
 * Created by xiaqibo on 2018/4/3.
 */
class BoardAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data : ArrayList<TickerListItem> = ArrayList()


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
        holder.itemView.textView1.text = Integer.toString(position).plus("、").plus(itemData.coin)
        holder.itemView.textView2.text = itemData.price
        holder.itemView.textView3.text = itemData.percent_change_1h
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