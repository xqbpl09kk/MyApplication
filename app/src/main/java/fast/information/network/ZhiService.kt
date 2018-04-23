package fast.information.network

import fast.information.network.bean.MessageItem
import fast.information.network.bean.SearchResult
import fast.information.network.bean.TickerListItem
import fast.information.network.bean.UpdateInfo
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultListBundle
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
* MyApplication
* Created by xiaqibo on 2018/3/12-0:20.
*/
interface ZhiService {

    @GET("/v1/news/list")
    fun getMessage(@Query("cursor") cursor  : Int
                   ,@Query("size") size : Int )
            :Call<ResultListBundle<MessageItem>>

    @GET("/v1/version/info")
    fun checkUpdate() : Call<ResultBundle<UpdateInfo>>


    @GET("")
    fun downloadApkFile(@Url downloadUrl :String) :Call<ResponseBody>

    @GET("/v1/ticker/list")
    fun tickerList(@Query("cursor") cursor :Int
                   ,@Query("size") size :Int)
            :Call<ResultListBundle<TickerListItem>>

    @GET(" v1/ticker/{key}/search")
    fun search(@Path("key") key:String )
            :Call<ResultListBundle<TickerListItem>>

    @GET("/v1/ticker/{symbol}")
    fun getTickerItem(@Path("symbol") symbol :String)
            :Call<ResultBundle<TickerListItem>>
}