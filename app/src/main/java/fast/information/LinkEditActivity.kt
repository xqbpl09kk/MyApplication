package fast.information

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.gson.Gson
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import kotlinx.android.synthetic.main.activity_link_edit.*
import kotlinx.android.synthetic.main.more_link_item.view.*
import com.google.gson.reflect.TypeToken



/**
 * Created by xiaqibo on 2018/4/28.
 */
class LinkEditActivity  : BaseActivity(){

    val data : ArrayList<Item> = ArrayList()

    override fun getLayoutRes(): Int {
        return R.layout.activity_link_edit
    }


    override fun registerViews() {
        super.registerViews()
        recycler_view.adapter = Adapter(data  , MyApplication.instance)
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        val urls = getSharedPreferences("links" , Context.MODE_PRIVATE).getString("data" , "")
        if(!TextUtils.isEmpty(urls)){
            setUp(urls)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.link_edit , menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.edit){
            createDialog()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setUp(urls : String){
        val gson  = Gson()
        data.addAll(gson.fromJson<ArrayList<Item>>(urls , object : TypeToken<ArrayList<Item>>() {

        }.type))
        recycler_view.adapter.notifyDataSetChanged()
    }


    private fun createDialog(){
        val builder :AlertDialog.Builder = AlertDialog.Builder(this@LinkEditActivity)
        builder.setTitle("添加")
        builder.setView(R.layout.create_link_content)
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.cancel() }
                .setPositiveButton(R.string.save , object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(this@LinkEditActivity , "111111" , Toast.LENGTH_SHORT).show()
                    }

                })
        builder.create().show()
    }


    companion object {

        class Item{
            var name:String?= null
            var url:String ?= null
        }

        class Adapter(val data : List<Item>? , val context :Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

            override fun getItemCount(): Int {
                return data?.size?:0
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.name.text = data?.get(position)?.name ?: ""
                holder.itemView.link.text = data?.get(position)?.url ?:""
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object: RecyclerView.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.more_link_item , parent , false)){}
            }

        }
    }


}