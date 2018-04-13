package fast.information

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.common.TimerHandler
import fast.information.main.adapter.BoardAdapter
import fast.information.network.RetrofitHelper
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_market.*
import java.util.*

/**
 * Created by xiaqibo on 2018/4/10.
 */
class MarketActivity : BaseActivity(), TimerHandler.Timer {


    override fun getLayoutRes(): Int {
        return (R.layout.activity_market)
    }

    val adapter = BoardAdapter(MyApplication.instance)
    private val layoutManager = LinearLayoutManager(MyApplication.instance)

    private var inSearch: Boolean = false
    private var searchView: SearchView? = null

    private val size = 20
    private var cursor = 0
    private var loading = false

    private var timerHandler: TimerHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = layoutManager
        recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.set(0, 0, 0, 2)
            }
        })
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val visibleCount: Int = layoutManager.childCount
                val totalCount: Int = layoutManager.itemCount
                val pastCount: Int = layoutManager.findFirstVisibleItemPosition()
                if (!loading && pastCount + visibleCount >= totalCount) {
                    netStep(true)
                }
            }
        })
        refresh_layout.setOnRefreshListener({
            netStep(false)
        })
        netStep(false)
    }


    override fun onTime() {
        netStep(false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.sort)?.isVisible = !inSearch
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.market, menu)
        val searchMenu = menu?.findItem(R.id.search)
        searchView = searchMenu?.actionView as SearchView
        setupSearchView(searchView)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sort -> {
                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this@MarketActivity)
                dialogBuilder.setSingleChoiceItems(R.array.sort_name, adapter.currentSortMode) { dialog, which ->
                    sort(which)
                    dialog?.dismiss()
                }
                dialogBuilder.create().show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (timerHandler == null)
            timerHandler = TimerHandler(this)
        timerHandler!!.sendEmptyMessageDelayed(TimerHandler.move, TimerHandler.delayMillis)
    }

    override fun onStop() {
        super.onStop()
        timerHandler?.sendEmptyMessage(TimerHandler.stop)
        shrinkSearchView()
    }

    override fun onBackPressed() {
        if (searchView?.isIconified == false) {
            searchView?.findViewById<TextView>(R.id.search_src_text)?.text = ""
            searchView?.isIconified = true
        } else
            super.onBackPressed()
    }

    private fun netStep(loadMore: Boolean) {
        if (!loadMore) {
            cursor = 0
            refresh_layout.isRefreshing = true
        }
        loading = true
        RetrofitHelper.instance.tickerList(cursor, size, object : ResultCallback<ResultListBundle<TickerListItem>> {
            override fun onSuccess(t: ResultListBundle<TickerListItem>?) {
                if (this@MarketActivity.isFinishing) return
                if (loadMore) {
                    t?.items?.let {
                        adapter.add(it)
                        Toast.makeText(MyApplication.instance, String.format(getString(R.string.format_update_items), it.size), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    adapter.currentSortMode = 0
                    t?.items?.let { adapter.update(it) }
                    Toast.makeText(MyApplication.instance, R.string.data_updated, Toast.LENGTH_SHORT).show()
                }
                cursor = t?.nextCursor ?: 0
                refresh_layout.isRefreshing = false
                loading = false
            }

            override fun onFailure(message: String, errorCode: Int) {
                if (isFinishing) return
                Toast.makeText(MyApplication.instance, message, Toast.LENGTH_SHORT).show()
                refresh_layout.isRefreshing = false
                loading = false
            }
        })
    }

    private fun sort(mode: Int) {
        if (adapter.itemCount == 0) return
        when (mode) {
            0 -> { //默认市值
                adapter.data.sortWith(Comparator { o1, o2 ->
                    val result: Long = (o1?.market_cap_usd?.toLong()
                            ?: 0) - (o2?.market_cap_usd?.toLong() ?: 0)
                    when {
                        result > 0 -> -1
                        result < 0 -> 1
                        else -> 0
                    }
                })
            }
            1 -> { //1小时增幅
                adapter.data.sortWith(Comparator { o1, o2 ->
                    val result: Float = (o1.percent_change_1h?.toFloat()
                            ?: Float.MIN_VALUE) - (o2.percent_change_1h?.toFloat()
                            ?: Float.MIN_VALUE)
                    when {
                        result > 0 -> -1
                        result < 0 -> 1
                        else -> 0
                    }
                })
            }
            2 -> { //24h增幅
                adapter.data.sortWith(Comparator { o1, o2 ->
                    val result: Float = (o1.percent_change_24h?.toFloat()
                            ?: Float.MIN_VALUE) - (o2.percent_change_24h?.toFloat()
                            ?: Float.MIN_VALUE)
                    when {
                        result > 0 -> -1
                        result < 0 -> 1
                        else -> 0
                    }
                })
            }
            3 -> { // 7d 增幅
                adapter.data.sortWith(Comparator { o1, o2 ->
                    val result: Float = (o1.percent_change_7d?.toFloat()
                            ?: Float.MIN_VALUE) - (o2.percent_change_7d?.toFloat()
                            ?: Float.MIN_VALUE)
                    when {
                        result > 0 -> -1
                        result < 0 -> 1
                        else -> 0
                    }
                })
            }
            4 -> { //交易量
                adapter.data.sortWith(Comparator { o1, o2 ->
                    val result: Long = (o1?.__h_volume_usd?.toLong()
                            ?: 0) - (o2?.__h_volume_usd?.toLong() ?: 0)
                    when {
                        result > 0 -> -1
                        result < 0 -> 1
                        else -> 0
                    }
                })
            }
        }
        adapter.currentSortMode = mode
        adapter.notifyDataSetChanged()
    }


    private fun setupSearchView(searchView: SearchView?) {
        if (searchView == null) return
        searchView.queryHint = getString(R.string.coin_symbol)
        searchView.setOnCloseListener({ !inSearch })
        searchView.setOnSearchClickListener({ inSearch = true })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return doSearch(query) }
            override fun onQueryTextChange(newText: String?): Boolean { return false }
        })
    }

    private fun shrinkSearchView() {
        searchView?.findViewById<TextView>(R.id.search_src_text)?.text = ""
        searchView?.isIconified = true
    }

    private fun doSearch(query: String?) :Boolean {
        Toast.makeText(MyApplication.instance, "in Search ", Toast.LENGTH_LONG).show()
        //TODO search
        return false
    }
}