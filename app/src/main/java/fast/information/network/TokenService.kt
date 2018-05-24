package fast.information.network

import fast.information.network.bean.AssertGroup
import fast.information.network.bean.MessageItem
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultListBundle
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by xiaqibo on 2018/5/22.
 */
interface TokenService {

    @GET("/v1/coin/coins")
    fun getCoins() : Call<ResultListBundle<String>>

    @GET("/v1/asset/groups")
    fun assertGroups() : Call<ResultListBundle<AssertGroup>>

    @GET("/v1/asset/group/add")
    fun createAssertGroup(@Field("name")name :String) : Call<ResultBundle<AssertGroup>>
}