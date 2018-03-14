package fast.information

import android.app.Application

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
class MyApplication  : Application(){

    override fun onCreate(){
        super.onCreate()
        System.out.print("App created ! ")
    }
}