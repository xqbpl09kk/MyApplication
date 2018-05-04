package fast.information.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.IntegerRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import fast.information.common.MyApplication
import fast.information.R
import fast.information.ShareActivity
import fast.information.network.bean.MessageItem
import kotlinx.android.synthetic.main.list_item_main.view.*
import android.support.v4.app.ActivityOptionsCompat
import android.text.TextUtils


/**
* MyApplication
* Created by xiaqibo on 2018/3/6-0:19.
*/
class HomeAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: ArrayList<MessageItem> = ArrayList()
    private val staredId: ArrayList<Int> = ArrayList()
    private val stared: ArrayList<MessageItem> = ArrayList()
    private var showData = stared

    private var showStar = true
    private var focusItemIndex :Int = -1
    private var listener : OnItemClick ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val contentView: View = layoutInflater.inflate(R.layout.list_item_main, parent, false)
        return object : RecyclerView.ViewHolder(contentView) {

        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData: MessageItem = showData[position]
        holder.itemView.time_text.text = itemData.getDate(false)
//        if(itemData.isExpended){
        if(focusItemIndex == position){
            holder.itemView.content_text.maxLines = Integer.MAX_VALUE
        }else{
            holder.itemView.content_text.maxLines = 5
        }
        holder.itemView.content_text.text = Integer.toString(position +1 )
                .plus("、").plus(itemData.title?.removePrefix("】")).plus(itemData.content)
                .replace("\n", "")
        if(TextUtils.isEmpty(itemData.link) || !itemData.link!!.startsWith("http")){
            holder.itemView.action0_text .visibility = View.INVISIBLE
        }else{
            holder.itemView.action0_text.visibility = View.VISIBLE
            holder.itemView.action0_text.setOnClickListener({
                MyApplication.instance.getLastActivity()
                        ?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itemData.link)))
            })
        }

        holder.itemView.action1_text.isSelected = staredId.contains(itemData.content.hashCode())

        holder.itemView.content_text.setTextColor(
                if(itemData.isRed ==1)
                    ContextCompat.getColor(context,  R.color.red)
                else
                    ContextCompat.getColor(context , R.color.text_normal))

        holder.itemView.action2_text.setOnClickListener({
            MyApplication.instance
                    .startActivity(Intent(context, ShareActivity::class.java)
                            .putExtra("message_item", itemData)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    , null)
        })
        holder.itemView.setOnClickListener({
//            MyApplication.instance
//                    .startActivity(Intent(context, ShareActivity::class.java)
//                            .putExtra("message_item", itemData)
//                            .putExtra("share", false)
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            , null)
//            val intent = Intent(MyApplication.instance ,  ShareActivity::class.java)
//            intent.putExtra("message_item" , itemData) .putExtra("share", false)
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(MyApplication.instance.getLastActivity()!!,
//                    holder.itemView.content_text,
////                    ViewCompat.getTransitionName("simple_activity_transition"))
//                    "simple_activity_transition")
//            MyApplication.instance.getLastActivity()!!.startActivity(intent, options.toBundle())
//            notifyItemChanged(focusItemIndex)
//            itemData.isExpended = !itemData.isExpended
            focusItemIndex = position
//            notifyDataSetChanged()
            notifyItemChanged(position)

            listener?.onItemClicked(position)
        })
        holder.itemView.action1_text.setOnClickListener({
            if (it.isSelected) {
                staredId.remove(itemData.content.hashCode())
                removeStar(itemData)
            } else {
                staredId.add(itemData.content.hashCode())
                stared.add(0 , itemData)
            }
            context.getSharedPreferences("stared", Context.MODE_PRIVATE)
                    .edit()
                    .putString("star", Gson().toJson(stared))
                    .apply()
            it.isSelected = !it.isSelected
        })
    }

    fun update(items: List<MessageItem>) {
        if (!showStar) {
            data.clear()
            data.addAll(items)
        } else {
            stared.clear()
            staredId.clear()
            stared.addAll(items)
            stared.mapTo(staredId) { it.content.hashCode() }
        }
        notifyDataSetChanged()
    }

    fun addItems(items: List<MessageItem>) {
        if (!showStar) {
            data.addAll(items)
        } else {
            stared.addAll(items)
            stared.mapTo(staredId) { it.content.hashCode() }
        }
        notifyDataSetChanged()
    }

    private fun removeStar(messageItem: MessageItem) {
        for (message in stared) {
            if (messageItem.content.hashCode()
                    == (message.content.hashCode())) {
                stared.remove(message)
                break
            }
        }
    }

    fun getLast():MessageItem{
        return data[data.lastIndex]
    }

    fun switchContent() {
        showStar = ! showStar
        showData = if (showStar) {
            stared
        } else {
            data
        }
        notifyDataSetChanged()
    }

    fun showStar(): Boolean {
        return showStar
    }



    fun setClickListener(l:OnItemClick){
        listener = l
    }


    interface OnItemClick{
        fun onItemClicked(p:Int)
    }
}