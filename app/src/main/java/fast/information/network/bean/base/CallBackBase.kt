package fast.information.network.bean.base

/**
 * Created by xiaqibo on 2018/4/3.
 */
class CallBackBase <T>: ResultCallback<T>{
    override fun onSuccess(t: T?) {

    }

    override fun onFailure(message: String, errorCode: Int) {

    }
}