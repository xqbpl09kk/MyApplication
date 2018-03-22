package fast.information

import android.app.Application



/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){


    val googlePlay :String = "GooglePlay"
    val newItems :String = "newItem"

    companion object {
        lateinit var instance: MyApplication
    }



    override fun onCreate(){
        super.onCreate()
        System.out.print("App created ! ")
        instance = this

    }





}