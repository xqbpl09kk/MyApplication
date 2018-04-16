package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import fast.information.CoinDetailActivity
import fast.information.R
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.SearchResult
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import kotlinx.android.synthetic.main.list_item_search.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by xiaqibo on 2018/4/3.
 */
class SearchAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val data : ArrayList<SearchResult> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val contentView  = layoutInflater.inflate(R.layout.list_item_search , parent , false)
        return object : RecyclerView.ViewHolder(contentView) {}
    }

    override fun getItemCount(): Int {
        return data.size
    }
    val pattern = Pattern.compile("<[A-Z1-9a-z_\\W\\s\\r\":;',()-= \n]*>")

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = data[position]
        holder.itemView.index.text = (position + 1) .toString()
        holder.itemView.name.text =  itemData.symbol
        if(TextUtils.isEmpty(itemData.icon)){
        }else{
            Glide.with(context).load(itemData.icon).into(holder.itemView.icon)
        }
//        holder.itemView.description.text = itemData.introduction
        val matcher: Matcher = pattern.matcher(itemData.introduction)
        holder.itemView.description.text = matcher.replaceAll("").trim()
        holder.itemView.setOnClickListener({
            RetrofitHelper.instance.tickerItem(itemData.symbol!! , object:ResultCallback<ResultBundle<TickerListItem>>{
                override fun onSuccess(t: ResultBundle<TickerListItem>?) {
                    val bundle  = Bundle()
                    bundle.putSerializable("ticker_item" , t?.item)
                    MyApplication.instance.jumpActivity(CoinDetailActivity::class.java , bundle)
                }

                override fun onFailure(message: String, errorCode: Int) {
                    Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
                }
            })

        })
    }

    fun update(d: ArrayList<SearchResult>){
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }

    fun add(d: ArrayList<SearchResult>){
        data.addAll(d)
        notifyDataSetChanged()
    }




}