package fast.information

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import fast.information.network.MessageItem
import kotlinx.android.synthetic.main.activity_share.*
import java.io.File
import java.io.FileOutputStream

/**
 * Created by xiaqibo on 2018/3/14.
 */
class ShareActivity :Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        val messageItem : MessageItem = intent.getSerializableExtra("message_item") as MessageItem
        val share :Boolean = intent.getBooleanExtra("share" , true)
        time_text.text = messageItem.created_at
        content_text.text = messageItem.content
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
        content.buildDrawingCache()
        val bitmap :Bitmap  = content.drawingCache
        val cacheFile : File = File.createTempFile("prefix" , ".jpg" , externalCacheDir)
        val os = FileOutputStream(cacheFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG ,90 , os)
        return  FileProvider.getUriForFile( this, "$packageName.provider", cacheFile)
    }

    private fun clearTmpFile(){
        for(file in externalCacheDir.listFiles()){
            file.delete()
        }
    }

}