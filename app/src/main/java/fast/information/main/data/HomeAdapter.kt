package fast.information.main.data

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fast.information.R
import fast.information.network.MessageItem
import kotlinx.android.synthetic.main.list_item_main.view.*
import android.support.v4.content.ContextCompat.startActivity
import com.google.gson.Gson
import fast.information.ShareActivity


/**
 * Created by xiaqibo on 2018/3/6.
 */
class HomeAdapter (private val context:Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    private val data : ArrayList<MessageItem> = ArrayList()
    private val staredId : ArrayList<Int> = ArrayList()
    private val stared : ArrayList<MessageItem> = ArrayList()
    private var showData = stared

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val contentView : View = layoutInflater.inflate(R.layout.list_item_main , parent , false)
        return object :RecyclerView.ViewHolder(contentView){

        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData : MessageItem = showData[position]
        holder.itemView.time_text.text = itemData.created_at
        holder.itemView.content_text.text = Integer.toString(position).plus(itemData.content)
        holder.itemView.action1_text.isSelected = staredId.contains(itemData.content.hashCode())
        holder.itemView.action2_text.setOnClickListener({
            startActivity(context ,
                    Intent(context ,ShareActivity::class.java)
                            .putExtra("message_item" , itemData)
                    ,null)
        })
        holder.itemView.setOnClickListener({
            startActivity(context ,
                    Intent(context ,ShareActivity::class.java)
                            .putExtra("message_item" , itemData)
                            .putExtra("share" ,false)
                    ,null)
        })
        holder.itemView.action1_text.setOnClickListener({
            if(it.isSelected){
                staredId.remove(itemData.content.hashCode())
                removeStar(itemData)
            }else{
                staredId.add(itemData.content.hashCode())
                stared.add(itemData)
            }
            context.getSharedPreferences("stared" , Context.MODE_PRIVATE )
                    .edit()
                    .putString("star" , Gson().toJson(stared))
                    .apply()
            it.isSelected = !it.isSelected
        })
    }


    fun update(items : ArrayList<MessageItem>){
        if(showData == data){
            data.clear()
            data.addAll(items)
        }else{
            stared.clear()
            stared.addAll(items)
            staredId.clear()
            for(star in stared){
                staredId.add(star.content.hashCode())
            }
        }
        notifyDataSetChanged()
    }

    fun addItems(items : ArrayList<MessageItem>){
        if(showData == data){
            data.addAll(items)
        }else{
            stared.addAll(items)
            for(star in stared){
                staredId.add(star.content.hashCode())
            }
        }
        notifyDataSetChanged()
    }


    private fun removeStar(messageItem : MessageItem){
        for(message in stared){
            if(messageItem.content.hashCode()
                    ==(message.content.hashCode())){
                stared.remove(message)
                break
            }
        }
    }

    fun switchContent() {
        showData = if(showData == data){
            stared
        }else{
            data
        }
        notifyDataSetChanged()
    }

    fun showStar():Boolean {
        return showData == stared
    }


}