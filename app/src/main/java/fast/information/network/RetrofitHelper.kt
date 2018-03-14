package fast.information.network


import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import fast.information.network.ResultListBundle
/**
 * Created by xiaqibo on 2018/3/12.
 */
public class RetrofitHelper private constructor(){

    private val baseUrl: String = "http://btcapi.yaopic.com/"
    private val tag  : String = "RetrofitHelper"

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(
                    GsonBuilder()
                            .setDateFormat("yyyy-MM-dd hh:mm:ss")
                            .create()
            ))
            .build()

    private val service: ZhiService = retrofit.create(ZhiService::class.java)

    init{

    }

    companion object {
        val instance: RetrofitHelper = RetrofitHelper()
    }

    fun getMessage(cursor: Int, size: Int, result:ResultCallback<ResultListBundle<MessageItem>>) {
        val call : Call<ResultListBundle<MessageItem>> = service.getMessage(cursor , size )
        call.enqueue(object :Callback<ResultListBundle<MessageItem>>{
            override fun onResponse(call: Call<ResultListBundle<MessageItem>>?, response: Response<ResultListBundle<MessageItem>>?) {
                val resultBundle:ResultListBundle<MessageItem> ?= response?.body()
//                val messageList = resultBundle?.items
                result.onSuccess(resultBundle)
            }

            override fun onFailure(call: Call<ResultListBundle<MessageItem>>?, t: Throwable?) {
                result.onFailure("inner error" , 101)
            }

        })
    }

}

