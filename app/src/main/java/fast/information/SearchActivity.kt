package fast.information

import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.SearchView
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.main.adapter.BoardAdapter
import fast.information.main.adapter.SearchAdapter
import fast.information.network.RetrofitHelper
import fast.information.network.bean.SearchResult
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.activity_search.*

/**
 * Created by xiaqibo on 2018/4/16.
 */
class SearchActivity : BaseActivity() {


    private val adapter = SearchAdapter(MyApplication.instance)
    private var key :String  ? = null

    override fun getLayoutRes(): Int {
        return R.layout.activity_search
    }


    override fun registerViews() {
        super.registerViews()
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
        key= intent.getBundleExtra("data")?.getString("key")
        if(!TextUtils.isEmpty(key)){
            title = getString(R.string.search).plus(key?.toUpperCase())
            search()
        }else{
            supportActionBar?.setCustomView(SearchView(this) , ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT , ActionBar.LayoutParams.WRAP_CONTENT))
        }
    }


    private fun search(){
        if(TextUtils.isEmpty(key)) return
        RetrofitHelper.instance.search(key!! , object : ResultCallback<ResultListBundle<TickerListItem>> {
            override fun onSuccess(t: ResultListBundle<TickerListItem>?) {
                t?.items?.let { adapter.update(it) }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
            }
        })
    }
}