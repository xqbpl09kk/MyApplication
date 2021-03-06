package fast.information

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import fast.information.common.MyApplication
import fast.information.main.MainActivity
import fast.information.network.bean.MessageItem
import kotlinx.android.synthetic.main.activity_share.*
//import org.android.agoo.service.SendMessage
import java.io.File
import java.io.FileOutputStream

/**
* MyApplication
* Created by xiaqibo on 2018/3/14-0:20.
*/
class ShareActivity :Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        window.decorView.background =ContextCompat.getDrawable(MyApplication.instance , R.drawable.share_bg)
        var message  = intent .getSerializableExtra("message_item")
        if(message == null){
            val messageString : String = intent.getStringExtra("message")
            message = Gson().fromJson(messageString , MessageItem::class.java)
        }
        val messageItem = message as MessageItem
        val share :Boolean = intent.getBooleanExtra("share" , true)
        time_text.text = messageItem.getDate(true)
        content_text.text = getString(R.string.bz_share).plus(messageItem.content)
        title_text.text = messageItem.title?.removePrefix("【")?.removeSuffix("】") ?: ""
        content_text.setOnClickListener({  imageShare()  })
        if(share) Handler().postDelayed({imageShare()} ,10)
    }

    private fun imageShare(){
        val imageUri = makeImageFile()
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        shareIntent.type = "image/*"
        startActivity(Intent.createChooser(shareIntent, "分享到"))
    }


    private fun makeImageFile() : Uri {
        clearTmpFile()
        val bitmap :Bitmap  = Bitmap.createBitmap(content.width , content.height , Bitmap.Config.RGB_565)
        val canvas  = Canvas(bitmap)
        content.draw(canvas)
        val cacheFile : File = File.createTempFile("prefix" , ".jpg" , externalCacheDir)
        val os = FileOutputStream(cacheFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG ,90 , os)
        return  FileProvider.getUriForFile( this, "$packageName.provider", cacheFile)
    }




    private fun createImageFile():String{
        clearTmpFile()
        val bitmap :Bitmap = Bitmap.createBitmap(content.width , content.height , Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        content.draw(canvas)
        val cacheFile : File = File.createTempFile("prefix" , ".jpg" , externalCacheDir)
        val os = FileOutputStream(cacheFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG ,90 , os)
        return cacheFile.absolutePath
    }

    private fun createBitmap():Bitmap{
        clearTmpFile()
        val bitmap : Bitmap = Bitmap.createBitmap(content.width , content.height , Bitmap.Config.RGB_565)
        val canvas :Canvas = Canvas(bitmap)
        content.draw(canvas)
        return bitmap
    }

    private fun clearTmpFile(){
        for(file in externalCacheDir.listFiles()){
            if(file.endsWith(".jpg"))
                file.delete()
        }
    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        startActivity(Intent(this@ShareActivity, MainActivity::class.java)
//                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
//    }

}