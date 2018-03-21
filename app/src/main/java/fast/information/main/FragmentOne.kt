package fast.information.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import fast.information.R
import fast.information.main.data.HomeAdapter
import fast.information.network.bean.MessageItem
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import fast.information.network.RetrofitHelper
import kotlinx.android.synthetic.main.fragment_one.*

/**
* MyApplication
* Created by xiaqibo on 2018/3/1-0:19.
*/
class FragmentOne : Fragment() {

    private var cursor : Int = 0
    private val size : Int = 20
    private var loading : Boolean = false
    private lateinit var  adapter :HomeAdapter

    companion object {

        fun createInstance(argBundle : Bundle) : FragmentOne {
            val instance = FragmentOne()
            instance.arguments = argBundle
            return instance
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_one, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomeAdapter(context!!)
        recycler_view.adapter = adapter
        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if(adapter.showStar()) return
                val visibleCount : Int = layoutManager.childCount
                val totalCount : Int = layoutManager.itemCount
                val pastCount : Int =  layoutManager.findFirstVisibleItemPosition()
                if(!loading && pastCount + visibleCount >= totalCount){
                    loading = true
                    netStep(true)
                }
            }
        })
        refresh_layout.setOnRefreshListener({
            if(adapter.showStar()){
                loadStarData()
            }else{
                netStep(false)
            } })
        Handler().postDelayed({ netStep(false)} , 200)
    }

    private fun netStep (loadMore:Boolean){
        if(!loadMore){
            cursor = 0
            loadStarData()
        }
        loading = true
        RetrofitHelper.instance.getMessage(cursor , size , object : ResultCallback<ResultListBundle<MessageItem>> {
            override fun onSuccess(t : ResultListBundle<MessageItem>?) {
                if(context == null || activity?.isFinishing == true) return
                if(adapter.showStar())
                    adapter.switchContent()
                val items : ArrayList<MessageItem> = (t ?: return).items ?: return
                cursor = t.nextCursor
                if(loadMore) adapter.addItems(items)
                else adapter.update(items)
                refresh_layout.isRefreshing = false
                loading= false
            }

            override fun onFailure(message: String, errorCode: Int) {
                if(context == null || activity?.isFinishing == true) return
                refresh_layout.isRefreshing = false
                loading = false
                Toast.makeText(context , "Error:".plus(message)
                        .plus("\t Code :")
                        .plus(Integer.toString(errorCode))
                        ,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadStarData(){
        val list =  Gson().fromJson<Array<MessageItem>>(
                context?.getSharedPreferences("stared" , Context.MODE_PRIVATE)
                        ?.getString("star" , "")
                , Array<MessageItem>::class.java)
        val stared = ArrayList<MessageItem>()
        if(list != null)
            stared.addAll(list)
        adapter.update(stared)
        refresh_layout.isRefreshing = false
    }

    fun scrollToTop() {
        recycler_view.smoothScrollToPosition( 0 )
    }

    fun switchContent(){
        adapter.switchContent()
        if(adapter.showStar()){
            loadStarData()
        }
    }

}