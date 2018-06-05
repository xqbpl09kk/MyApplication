package fast.information.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import fast.information.CreateAssertActivity
import fast.information.LoginActivity
import fast.information.R
import fast.information.common.BaseFragment
import fast.information.common.MyApplication
import fast.information.main.adapter.AssertAdapter
import fast.information.network.RetrofitHelper
import fast.information.network.bean.Assert
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.fragment_assert.*

/**
 * Created by xiaqibo on 2018/5/22.
 */
class FragmentAssert: BaseFragment() {

    companion object {



        fun createInstance(argBundle : Bundle) : FragmentAssert {
            val instance = FragmentAssert()
            instance.arguments = argBundle
            return instance
        }
    }

    private var data :List<Assert> ?= null
    private val unit = arrayOf("USDT" , "BTC" , "ETH")
    private var BTC_price_in_usdt : TickerListItem ?= null
    private var ETH_price_in_usdt :TickerListItem ?=null

    private var adapter : AssertAdapter ?= null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_assert
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_btn.setOnClickListener({
            startActivityForResult(Intent(MyApplication.instance , LoginActivity::class.java) ,1000)
        })
        fab.setOnClickListener({
            startActivity(Intent(MyApplication.instance.getLastActivity() , CreateAssertActivity::class.java))
        })
        refresh_layout.setOnRefreshListener { loadData() }
        adapter = AssertAdapter(MyApplication.instance)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if(dy > 0){
                    fab.hide()
                }else{
                    fab.show()
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if(RetrofitHelper.auth == null ){
            onLogout()
        }else{
            onLoginSuccess()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK){
            onLoginSuccess()
        }
    }

    private fun onLoginSuccess(){
        refresh_layout.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
        login_btn.visibility = View.GONE
        loadData()
    }

    private fun onLogout(){
        refresh_layout.visibility = View.GONE
        fab.visibility = View.GONE
        login_btn.visibility = View.VISIBLE
        adapter?.update(null)
        data = null
    }

    private fun loadData(){
        RetrofitHelper.instance.getAssert(object : ResultCallback<ResultListBundle<Assert>> {
            override fun onSuccess(t: ResultListBundle<Assert>?) {
                refresh_layout.isRefreshing = false
                data = t?.items
                getCurrentPriceAsync()
            }

            override fun onFailure(message: String, errorCode: Int) {
                refresh_layout.isRefreshing = false
                Toast.makeText(MyApplication.instance , "get assert failed !" ,Toast.LENGTH_SHORT).show()
            }

        })
    }



    private fun showItems(){
        if(data == null) return
        adapter?.update(data)

    }





    private fun getCurrentPriceAsync(){
        val thread : Thread = object : Thread() {
            override fun run() {
                data?.iterator()?.forEach { coin : Assert -> run { getCoin(coin) } }
//                unit.iterator().forEach { it:String ->run{getUnit(it)} }
                activity?.runOnUiThread { showItems() }
            }
        }
        thread.start()
    }

    private fun getCoin(coin : Assert){
        RetrofitHelper.instance.tickerItemSync(coin.coin!! , object : ResultCallback<ResultBundle<TickerListItem>> {
            override fun onSuccess(t: ResultBundle<TickerListItem>?) {
                t?.item?.let {
                    coin.current_price_usdt = t.item?.price
                    coin.today_change_rate = t.item?.percent_change_24h
                    coin.refresh_data_set = t.item != null
                    coin.icon = t.item?.icon
                }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance, message , Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getUnit(unit : String){
        RetrofitHelper.instance.tickerItemSync(unit , object : ResultCallback<ResultBundle<TickerListItem>> {
            override fun onSuccess(t: ResultBundle<TickerListItem>?) {
                t?.item?.let {
                    when(unit){
                        "BTC" -> BTC_price_in_usdt = t.item
                        "ETH" -> ETH_price_in_usdt = t.item
                    }
                }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance, message , Toast.LENGTH_SHORT).show()
            }
        })
    }
}