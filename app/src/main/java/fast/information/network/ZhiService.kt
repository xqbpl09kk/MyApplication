package fast.information.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
* MyApplication
* Created by xiaqibo on 2018/3/12-0:20.
*/
interface ZhiService {

    @GET("/v1/news/list")
    fun getMessage(@Query("cursor") cursor  : Int ,
                   @Query("size") size : Int ) : Call<ResultListBundle<MessageItem>>

}