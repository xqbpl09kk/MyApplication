package fast.information

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.google.gson.Gson
import fast.information.main.MainActivity
import fast.information.network.bean.MessageItem
import kotlinx.android.synthetic.main.activity_share.*
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
        var message  = intent .getSerializableExtra("message_item")
        if(message == null){
            val messageString : String = intent.getStringExtra("message")
            message = Gson().fromJson(messageString , MessageItem::class.java)
        }
        val messageItem = message as MessageItem
        val share :Boolean = intent.getBooleanExtra("share" , true)
        time_text.text = messageItem.getDate(true)
        content_text.text = messageItem.content
        title_text.text = messageItem.title?.removePrefix("【")?.removeSuffix("】") ?: ""
        content_text.setOnClickListener({  imageShare()  })
        if(share)
            Handler().postDelayed({ imageShare() },200)
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

    private fun clearTmpFile(){
        for(file in externalCacheDir.listFiles()){
            if(file.endsWith(".jpg"))
                file.delete()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ShareActivity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

}