package fast.information.main.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat.startActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import fast.information.MyApplication
import fast.information.R
import fast.information.ShareActivity
import fast.information.network.bean.MessageItem
import kotlinx.android.synthetic.main.list_item_main.view.*


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
        holder.itemView.content_text.text = Integer.toString(position +1 )
                .plus("、").plus(itemData.title).plus(itemData.content)
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
            MyApplication.instance
                    .startActivity(Intent(context, ShareActivity::class.java)
                            .putExtra("message_item", itemData)
                            .putExtra("share", false)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            , null)
        })
        holder.itemView.action1_text.setOnClickListener({
            if (it.isSelected) {
                staredId.remove(itemData.content.hashCode())
                removeStar(itemData)
            } else {
                staredId.add(itemData.content.hashCode())
                stared.add(itemData)
            }
            context.getSharedPreferences("stared", Context.MODE_PRIVATE)
                    .edit()
                    .putString("star", Gson().toJson(stared))
                    .apply()
            it.isSelected = !it.isSelected
        })
    }

    fun update(items: ArrayList<MessageItem>) {
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

    fun addItems(items: ArrayList<MessageItem>) {
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

}