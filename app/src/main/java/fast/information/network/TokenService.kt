package fast.information.network

import fast.information.network.bean.Assert
import fast.information.network.bean.AssertGroup
import fast.information.network.bean.MessageItem
import fast.information.network.bean.Regions
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultListBundle
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by xiaqibo on 2018/5/22.
 */
interface TokenService {

    @GET("/v1/coin/coins")
    fun getCoins() : Call<ResultListBundle<String>>

    @GET("/v1/asset/groups")
    fun assertGroups(@Header("Authorization") token :String)
            : Call<ResultListBundle<AssertGroup>>

    @FormUrlEncoded
    @POST("/v1/asset/group/add")
    fun createAssertGroup(@Header("Authorization") token :String
                           , @Field("name")name :String)
            : Call<ResultBundle<AssertGroup>>

    @GET("/v1/tool/regions")
    fun createAssertGroup() : Call<ResultBundle<Regions>>

    @FormUrlEncoded
    @POST("/v1/asset/add")
    fun createAssert(@Header("Authorization") token :String
                     ,@Field("coin" ) coin:String
                     ,@Field("currency") currency :String
                     ,@Field("amount") amount:String
                     ,@Field("cost_price") cost_price:String
                     ,@Field("position")position:String
                     ,@Field("exchange")exchange:String
                     ,@Field("wallet_address")wallet_address:String
                     ,@Field("operate_date")operate_date:String
                     ,@Field("group_id") groupId :String
                    ,@Field("note") note:String)
            : Call<ResultBundle<Assert>>
    @FormUrlEncoded
    @POST("/v1/asset/{asset_id}/update")
    fun editAssert(@Header("Authorization") token :String
                    ,@Path("asset_id") assertId :String
                     ,@Field("coin" ) coin:String
                     ,@Field("currency") currency :String
                     ,@Field("amount") amount:String
                     ,@Field("cost_price") cost_price:String
                     ,@Field("position")position:String
                     ,@Field("exchange")exchange:String
                     ,@Field("wallet_address")wallet_address:String
                     ,@Field("operate_date")operate_date:String
                   ,@Field("group_id") groupId :String
                   ,@Field("note") note:String)
            : Call<ResultBundle<Assert>>

    @GET("/v1/asset/assets")
    fun getAssert(@Header("Authorization") token :String)
            : Call<ResultListBundle<Assert>>

    @POST("/v1/asset/{asset_id}/delete")
    fun deleteAssert(@Header("Authorization") token :String
                    ,@Path("asset_id") assertId :String)
            : Call<ResultListBundle<Assert>>
}