package fast.information.network


import android.util.Log
import com.google.gson.GsonBuilder
import fast.information.BuildConfig
import fast.information.common.MyApplication
import fast.information.network.bean.MessageItem
import fast.information.network.bean.UpdateInfo
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
* MyApplication
* Created by xiaqibo on 2018/3/12-0:20.
*/
class RetrofitHelper private constructor(){

    private val baseUrl: String = BuildConfig.Base_Url
    private val TAG = "RetrofitTracker"
    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(
                    GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                            .create()))
            .build()

    private val service: ZhiService = retrofit.create(ZhiService::class.java)


    companion object {
        val instance: RetrofitHelper = RetrofitHelper()
    }

    fun getMessage(cursor: Int, size: Int, result: ResultCallback<ResultListBundle<MessageItem>>) {
        val call : Call<ResultListBundle<MessageItem>> = service.getMessage(cursor , size )
        handleRequest(call , result)
//        call.enqueue(object :Callback<ResultListBundle<MessageItem>>{
//            override fun onResponse(call: Call<ResultListBundle<MessageItem>>?
//                                    , response: Response<ResultListBundle<MessageItem>>?) {
//                val resultBundle: ResultListBundle<MessageItem>?= response?.body()
//                result.onSuccess(resultBundle)
//            }
//
//            override fun onFailure(call: Call<ResultListBundle<MessageItem>>?, t: Throwable?) {
//                result.onFailure("inner error" , 101)
//            }
//
//        })
    }


    fun checkUpdate(result : ResultCallback<ResultBundle<UpdateInfo>>){
        val call : Call<ResultBundle<UpdateInfo>> = service.checkUpdate()
        handleRequest(call , result)
//        call.enqueue(object :Callback<ResultBundle<UpdateInfo>>{
//            override fun onResponse(call: Call<ResultBundle<UpdateInfo>>?
//                                    , response: Response<ResultBundle<UpdateInfo>>?) {
//                val resultBundle: ResultBundle<UpdateInfo>?= response?.body()
//                result.onSuccess(resultBundle)
//            }
//
//            override fun onFailure(call: Call<ResultBundle<UpdateInfo>>?, t: Throwable?) {
//                result.onFailure("inner error" , 101)
//            }
//
//        })
    }


    fun downloadApkFile(downloadUrl :String , result : ResultCallback<Int>){
        val call = service.downloadApkFile(downloadUrl)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
               if(response?.isSuccessful == true
                       && response.body()!=null){
                   saveApk(response.body()!!, result)
               }else{
                   result.onFailure("下载失败" ,101)
               }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                result.onFailure("下载失败" ,101)
            }

        })
    }


    private fun<T > handleRequest(call :Call<T>  ,result:ResultCallback<T> ){

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                result.onSuccess(response?.body())
                Log.i(TAG.plus("-success"),response?.body().toString())
            }

            override fun onFailure(call: Call<T>?, t: Throwable?) {
                t?.message?.let { result.onFailure(it,500 ) }
                Log.i(TAG.plus("-error"), t?.message)
            }

        })
    }


    private fun saveApk(responseBody : ResponseBody , result: ResultCallback<Int>){
        var outputStream: OutputStream ?= null
        var inputStream :InputStream ? = null
        try {
            val outputFile = File(MyApplication.instance.externalCacheDir , "update.apk")
            inputStream  = responseBody.byteStream()
            outputStream  = FileOutputStream(outputFile)
            val fileReader = ByteArray(4096)
            val fileSize :Long = responseBody.contentLength()
            var downloadedLength = 0
            while (true){
                val readLength :Int = inputStream.read(fileReader)
                if(readLength == -1) {
                    result.onSuccess(-1)
                    break
                }
                outputStream.write(fileReader , 0 , readLength)
                downloadedLength += readLength
                result.onSuccess((downloadedLength * 100 / fileSize).toInt())
            }
            outputStream.flush()
        }catch (e:Exception){
            result.onFailure("读取文件出错" ,102)
        }finally {
            if(inputStream!= null)
                inputStream.close()
            if(outputStream != null)
                outputStream.close()
        }
    }

}

