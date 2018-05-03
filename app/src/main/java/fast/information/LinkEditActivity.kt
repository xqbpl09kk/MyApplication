package fast.information

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import com.google.gson.Gson
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import kotlinx.android.synthetic.main.activity_link_edit.*
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.create_link_content.view.*
import kotlinx.android.synthetic.main.list_item_link.view.*
import java.util.*


/**
 * Created by xiaqibo on 2018/4/28.
 */
class LinkEditActivity : BaseActivity(), PopupMenu.OnMenuItemClickListener {

    val data: ArrayList<Item> = ArrayList()
    var adapter: Adapter? = null

    var changed: Boolean = false

    override fun getLayoutRes(): Int {
        return R.layout.activity_link_edit
    }


    override fun registerViews() {
        super.registerViews()
        adapter = Adapter(data, MyApplication.instance, this@LinkEditActivity)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        val touchHelper = ItemTouchHelper(object :ItemTouchHelper. Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN//拖拽
                val swipeFlags = ItemTouchHelper.ACTION_STATE_SWIPE//侧滑删除
                return makeMovementFlags(dragFlags , swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                Collections.swap(data,viewHolder?.adapterPosition ?:0,target?.adapterPosition ?:0)
                adapter?.notifyItemMoved(viewHolder?.adapterPosition ?:0,target?.adapterPosition ?:0)
                getSharedPreferences("links", Context.MODE_PRIVATE).edit().putString("data", Gson().toJson(data)).apply()
                changed = true

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

        })
        touchHelper.attachToRecyclerView(recycler_view)
        val urls = getSharedPreferences("links", Context.MODE_PRIVATE).getString("data", "")
        if (!TextUtils.isEmpty(urls)) {
            val gson = Gson()
            data.addAll(gson.fromJson<ArrayList<Item>>(urls, object : TypeToken<ArrayList<Item>>() {

            }.type))
            recycler_view.adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.link_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == R.id.create -> createDialog("", "", -1)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.edit -> {
                val editItem = data[adapter?.clickPosition ?: 0]
                createDialog(editItem.name, editItem.url, adapter?.clickPosition ?: 0)
            }
            R.id.delete -> {
                data.removeAt(adapter?.clickPosition ?: 0)
                getSharedPreferences("links", Context.MODE_PRIVATE).edit().putString("data", Gson().toJson(data)).apply()
                adapter?.notifyItemRemoved(adapter?.clickPosition ?: 0)
                changed = true
            }
        }
        return true
    }

    private fun createDialog(title: String?, url: String?, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@LinkEditActivity)
        builder.setTitle("添加")
        val contentView = LayoutInflater.from(MyApplication.instance)
                .inflate(R.layout.create_link_content, this@LinkEditActivity.window.decorView as ViewGroup, false)
        contentView.name_edit.setText(title)
        contentView.url_edit.setText(url)
        builder.setView(contentView)
        builder.setNegativeButton(R.string.cancel) { _, _ -> }
                .setPositiveButton(R.string.save) { _, _ -> }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener({
            if (TextUtils.isEmpty(contentView.url_edit.text)) {
                Toast.makeText(MyApplication.instance, R.string.url_none_empty, Toast.LENGTH_SHORT).show()
            } else if (!contentView.url_edit.text.startsWith("http:")) {
                Toast.makeText(MyApplication.instance, R.string.url_invalidate, Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                if (TextUtils.isEmpty(url)) {
                    data.add(0, Item(contentView.name_edit.text.trim().toString(), contentView.url_edit.text.trim().toString()))
                    getSharedPreferences("links", Context.MODE_PRIVATE).edit().putString("data", Gson().toJson(data)).apply()
                    recycler_view.adapter.notifyItemInserted(0)
                } else {
                    data[position].name = contentView.name_edit.text.trim().toString()
                    data[position].url = contentView.url_edit.text.trim().toString()
                    getSharedPreferences("links", Context.MODE_PRIVATE).edit().putString("data", Gson().toJson(data)).apply()
                    recycler_view.adapter.notifyItemChanged(position)
                }
                changed = true
            }
        })

        val inputMethod: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        contentView.name_edit.requestFocus()
        inputMethod.showSoftInput(contentView.name_edit, InputMethodManager.SHOW_FORCED)
    }

    override fun onBackPressed() {
        if (changed) {
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            super.onBackPressed()
        }
    }


    companion object {

        class Item(n: String, u: String) {
            var name: String? = n
            var url: String? = u
        }

        class Adapter(val data: List<Item>?, val context: Context, val achor: LinkEditActivity) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            var clickPosition = -1


            override fun getItemCount(): Int {
                return data?.size ?: 0
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.name.text = data?.get(position)?.name ?: ""
                holder.itemView.link.text = data?.get(position)?.url ?: ""
                holder.itemView.icon.setImageResource(R.drawable.ic_more_vert_black_24dp)
                holder.itemView.icon.setOnClickListener({
                    val popupMenu = PopupMenu(context, it)
                    val inflater = popupMenu.menuInflater
                    inflater.inflate(R.menu.link_options, popupMenu.menu)
                    popupMenu.show()
                    popupMenu.setOnMenuItemClickListener(achor)
                    clickPosition = holder.adapterPosition
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.list_item_link, parent, false)) {}
            }


        }
    }


}