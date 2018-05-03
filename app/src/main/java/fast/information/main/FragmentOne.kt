package fast.information.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import fast.information.common.MyApplication
import fast.information.R
import fast.information.common.BaseFragment
import fast.information.main.adapter.HomeAdapter
import fast.information.network.bean.MessageItem
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import fast.information.network.RetrofitHelper
import kotlinx.android.synthetic.main.fragment_one.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * MyApplication
 * Created by xiaqibo on 2018/3/1-0:19.
 */
class FragmentOne : BaseFragment() , HomeAdapter.OnItemClick {
    override fun onItemClicked(position : Int ) {
        recycler_view.smoothScrollToPosition(position)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_one
    }

    private var cursor: Int = 0
    private val size: Int = 20
    private var loading: Boolean = false
    private lateinit var adapter: HomeAdapter
    private val layoutManager = LinearLayoutManager(MyApplication.instance)

    companion object {

        fun createInstance(argBundle: Bundle): FragmentOne {
            val instance = FragmentOne()
            instance.arguments = argBundle
            return instance
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeAdapter(context!!)
        adapter.setClickListener(this@FragmentOne)

        recycler_view.adapter = adapter
        recycler_view.layoutManager = layoutManager
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (adapter.showStar()) return
                val visibleCount: Int = layoutManager.childCount
                val totalCount: Int = layoutManager.itemCount
                val pastCount: Int = layoutManager.findFirstVisibleItemPosition()
                if (!loading && pastCount + visibleCount >= totalCount) {
                    loading = true
                    netStep(true)
                }
            }
        })
        refresh_layout.setOnRefreshListener({
            if (adapter.showStar()) {
                loadStarData()
            } else {
                netStep(false)
            }
        })
        Handler().postDelayed({ netStep(false) }, 200)
    }

    private fun netStep(loadMore: Boolean) {
        if (!loadMore) {
            cursor = 0
            loadStarData()
            refresh_layout.isRefreshing = true
        }
        loading = true
        RetrofitHelper.instance.getMessage(cursor, size
                , object : ResultCallback<ResultListBundle<MessageItem>> {
            override fun onSuccess(t: ResultListBundle<MessageItem>?) {
                if (activity?.isFinishing == true) return
                refresh_layout.isRefreshing = false
                if (adapter.showStar())
                    adapter.switchContent()
                cursor = t?.nextCursor?:cursor
                if (loadMore) adapter.addItems(deduplication( t?.items ?: return))
                else adapter.update(deduplication( t?.items ?: return))
                loading = false
            }

            override fun onFailure(message: String, errorCode: Int) {
                if (activity?.isFinishing == true) return
                refresh_layout.isRefreshing = false
                loading = false
                Toast.makeText(MyApplication.instance, "Error:".plus(message)
                        .plus("\t Code :")
                        .plus(Integer.toString(errorCode))
                        , Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun deduplication(input : ArrayList<MessageItem>) : List<MessageItem>{
        for(index in input.indices)
            when {
                index >0 -> input[index].unique = !match(input[index - 1] ,input[index])
                adapter.itemCount != 0 -> input[0].unique = !match( input[0] , adapter.getLast())
                else -> input[0].unique = true
            }
        return input.filter { it.unique }
    }


    private fun match(a :MessageItem , b : MessageItem ) :Boolean{
//        val lengthA = a.content.length
//        val lengthB = b.content.length
//        if(lengthA <10 ) {
//            a.unique = false
//            return true
//        }
//        if(lengthB <10){
//            b.unique = false
//            return true
//        }
//        if((lengthA / lengthB) >1.3 && lengthA / lengthB <0.7) return false
//        val pattern = Pattern.compile(a.content.substring(0 , lengthA/))
//        val matcher :Matcher
        //TODO Realize the matching algorithm
        return a.content == b.content
    }

    private fun loadStarData() {
        val list = Gson().fromJson<Array<MessageItem>>(
                MyApplication.instance.getSharedPreferences("stared", Context.MODE_PRIVATE)
                        ?.getString("star", "")
                , Array<MessageItem>::class.java)
        val stared = ArrayList<MessageItem>()
        if (list != null)
            stared.addAll(list)
        adapter.update(stared)
        refresh_layout.isRefreshing = false
    }

    fun scrollToTop() {
        if (layoutManager.findFirstVisibleItemPosition() < 50) {
            recycler_view.smoothScrollToPosition(0)
        } else {
            recycler_view.scrollToPosition(0)
        }
    }

    fun switchContent() {
        adapter.switchContent()
        if (adapter.showStar()) {
            loadStarData()
        }
    }

    @StringRes
    fun getTitle(): Int {
        return if (adapter.showStar()) {
            R.string.collection
        } else {
            R.string.title_home
        }
    }

}