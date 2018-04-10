package fast.information.main

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fast.information.common.MyApplication
import fast.information.R
import fast.information.main.adapter.BoardAdapter
import fast.information.main.adapter.MuiltBoardAdapter
import fast.information.network.RetrofitHelper
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.fragment_second.*
import java.util.*

/**
* MyApplication
* Created by xiaqibo on 2018/3/1-0:19.
*/
class FragmentTwo : Fragment() {

    private val size :Int = 20
    private var cursor :Int = 0
    val adapter = MuiltBoardAdapter(MyApplication.instance)
    private val layoutManager = LinearLayoutManager(MyApplication.instance)
    private var loading = false


    companion object {

        fun createInstance(argBundle : Bundle) : FragmentTwo {
            val instance = FragmentTwo()
            instance.arguments = argBundle
            return instance
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second ,container , false )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(object: RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.set(0 ,0 , 0, 2)
            }
        })
        refresh_layout.setOnRefreshListener ( {  netStep(false)  })
        netStep(false)
    }



    private fun netStep(loadMore: Boolean){
        if(!loadMore){
            cursor = 0
            refresh_layout.isRefreshing = true
        }
        loading = true
        RetrofitHelper.instance.tickerList(cursor , size , object : ResultCallback<ResultListBundle<TickerListItem>> {
            override fun onSuccess(t: ResultListBundle<TickerListItem>?) {
                if(loadMore){
                    t?.items?.let { adapter.add(it) }
                }else{
                    t?.items?.let { adapter.update(it) }
                }
                cursor = t?.nextCursor ?: 0
                refresh_layout.isRefreshing = false
                loading = false
                adapter.currentSortMode = 0
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
                refresh_layout.isRefreshing = false
                loading = false
            }
        })
    }




}