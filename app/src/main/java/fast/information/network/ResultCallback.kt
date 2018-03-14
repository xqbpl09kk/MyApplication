package fast.information.network

/**
 * Created by xiaqibo on 2018/3/13.
 */
interface ResultCallback <in T >{

    fun onSuccess(t: T ?)
    fun onFailure(message : String , errorCode : Int )

}