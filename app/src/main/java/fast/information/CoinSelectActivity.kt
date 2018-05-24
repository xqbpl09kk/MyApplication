package fast.information

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.activity_coin_select.*

/**
 * Created by xiaqibo on 2018/4/16.
 */
class CoinSelectActivity : BaseActivity() {


    private var data : List<String> ?= null
    private var showData : List<String> ?= null
    private var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder> = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(MyApplication.instance)
                            .inflate(R.layout.list_item_coin_select , parent  ,false )) { }
        }

        override fun getItemCount(): Int {
            return showData?.size?:0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as TextView).text = showData?.get(position) ?: ""
            holder.itemView.setOnClickListener({
                setResult(Activity.RESULT_OK , Intent().putExtra("coin" , showData?.get(position)))
                finish()
            })
        }

    }


    private var filter :String  ?= null
    private val filterHandler: Handler  = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            showData = when {
                TextUtils.isEmpty(filter) -> data?.subList(0 , Math.min (data?.size?: 0 , 20 ))
                filter!!.length <= 6 -> data?.filter { it.contains(filter!!) || filter!!.contains(it) }
                else -> null
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_coin_select
    }


    override fun registerViews() {
        super.registerViews()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        RetrofitHelper.instance.getCoins(object :ResultCallback<ResultListBundle<String>>{
            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(this@CoinSelectActivity , errorCode , Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(t: ResultListBundle<String>?) {
                data = t?.items?.filter { !TextUtils.isEmpty(it.trim()) }
                showData = data?.subList(0 , Math.min (data?.size?: 0 , 20 ))
                adapter.notifyDataSetChanged()
            }
        })
        filter_editor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter = s.toString().toUpperCase()
                filterHandler.removeMessages(0)
                filterHandler.sendEmptyMessageDelayed(0 , 500)
            }

        })
        filter_editor.setOnClickListener({filter_editor.setText("")})
    }

    override fun onDestroy() {
        super.onDestroy()
        filterHandler.removeMessages(0)
    }



}