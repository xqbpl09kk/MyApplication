package fast.information.network

import fast.information.network.bean.*
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultListBundle
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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

    @GET("/v1/ticker/{key}/search")
    fun search(@Path("key") key:String )
            :Call<ResultListBundle<TickerListItem>>

    @GET("/v1/ticker/{symbol}")
    fun getTickerItem(@Path("symbol") symbol :String)
            :Call<ResultBundle<TickerListItem>>

    @FormUrlEncoded
    @POST("/v1/auth/email/register")
    fun emailAuth(@Field("client_id") clientId :String
                  , @Field("client_secret") clientSecret:String
                  , @Field("grant_type") grantType :String
                  , @Field("country_code") countryCode :String
                  , @Field("email") email:String
                  , @Field("password") password:String
                  , @Field("device_Token" )deviceToken:String)
            :Call<ResultBundle<AuthItem>>

    @FormUrlEncoded
    @POST("/v1/auth/login/cell/captcha")
    fun phoneAuth(@Field("client_id") clientId :String
                  , @Field("client_secret") clientSecret:String
                  , @Field("grant_type") grantType :String
                  , @Field("country_code") countryCode :String
                  , @Field("cell") cell:String
                  , @Field("code") code:String
                  , @Field("device_Token" )deviceToken:String)
            :Call<ResultBundle<AuthItem>>


    @FormUrlEncoded
    @POST("/v1/tool/captcha/cell/create")
    fun captureCode(@Field("country_code")  countryCode :String
                    ,@Field("cell") cell :String
                    ,@Field("type") type:String)
            :Call<ResultBundle<String>>
}