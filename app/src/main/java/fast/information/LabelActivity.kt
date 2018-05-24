package fast.information

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.AssertGroup
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import junit.framework.Assert
import kotlinx.android.synthetic.main.activity_label.*
import retrofit2.Retrofit

/**
 * Created by xiaqibo on 2018/5/23.
 */
class LabelActivity :BaseActivity() {
    private var data :List<AssertGroup> ?= null
    private var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder> = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(MyApplication.instance)
                    .inflate(R.layout.list_item_coin_select , parent  ,false )) { }
        }

        override fun getItemCount(): Int {
            return data?.size?:0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView as TextView).text = data?.get(position)?.name ?: ""
            holder.itemView.setOnClickListener({
                setResult(Activity.RESULT_OK , Intent().putExtra("group_id" , data?.get(position)?.id))
                finish()
            })
        }

    }


    override fun getLayoutRes(): Int {
        return R.layout.activity_label
    }

    override fun registerViews() {
        super.registerViews()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        RetrofitHelper.instance.assertGroup(object : ResultCallback<ResultListBundle<AssertGroup>>{
            override fun onSuccess(t: ResultListBundle<AssertGroup>?) {
                data = t?.items
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
                finish()
            }
        })
        save_btn.setOnClickListener({
            if(!TextUtils.isEmpty(filter_editor.text.toString()))
                RetrofitHelper.instance.createAssertGroup(filter_editor.text.toString() , object : ResultCallback<ResultBundle<AssertGroup>> {
                    override fun onSuccess(t: ResultBundle<AssertGroup>?) {
                        setResult(Activity.RESULT_OK , Intent().putExtra("assert_group" , t?.item))
                        finish()
                    }

                    override fun onFailure(message: String, errorCode: Int) {
                        Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
                    }

                })
        })

    }
}