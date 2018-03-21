package fast.information.network.bean.base

/**
* MyApplication
* Created by xiaqibo on 2018/3/13-0:19.
*/
interface ResultCallback <in T >{

    fun onSuccess(t: T ?)
    fun onFailure(message : String , errorCode : Int )

}


