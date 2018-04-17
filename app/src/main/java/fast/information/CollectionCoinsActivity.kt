package fast.information

import android.content.Context
import android.content.SharedPreferences
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.main.adapter.BoardAdapter
import fast.information.network.RetrofitHelper
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import kotlinx.android.synthetic.main.activity_market.*

/**
 * Created by xiaqibo on 2018/4/17.
 */
class CollectionCoinsActivity : BaseActivity() {

    var collectionCoins : Set<String>  ?= null
    private val coinDetails : ArrayList<TickerListItem> = ArrayList()

    private var adapter : BoardAdapter= BoardAdapter(MyApplication.instance)

    private var sharedPreferences :SharedPreferences ? = null

    override fun getLayoutRes(): Int {
        return R.layout.activity_coin_collection
    }


    override fun registerViews() {
        super.registerViews()
        refresh_layout.setOnRefreshListener { netStep() }
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        sharedPreferences = getSharedPreferences("settings" , Context.MODE_PRIVATE)
        netStep()
    }


    private fun netStep(){
        coinDetails.clear()
        collectionCoins = sharedPreferences?.getStringSet("collection_coins" , HashSet<String>())
        refresh_layout.isRefreshing = true
        val thread : Thread = object : Thread() {
            override fun run() {
                collectionCoins?.iterator()?.forEach { coin : String -> run { getCoin(coin) } }
                runOnUiThread {
                    adapter.update(coinDetails)
                    refresh_layout.isRefreshing = false
                }
            }
        }
        thread.start()
    }

    private fun getCoin(symbol : String){
        RetrofitHelper.instance.tickerItemSync(symbol , object : ResultCallback<ResultBundle<TickerListItem>> {
            override fun onSuccess(t: ResultBundle<TickerListItem>?) {
                t?.item?.let { coinDetails.add(it) }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance, message , Toast.LENGTH_SHORT).show()
            }
        })
    }
}