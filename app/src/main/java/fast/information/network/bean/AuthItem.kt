package fast.information.network.bean

/**
 * Created by xiaqibo on 2018/5/14.
 */
class AuthItem {

    var access_token : String ?= null
    var token_type : String ?= null
    var expires_at :String ?= null
    var refresh_token :String ?= null
    var is_new :Boolean ?= null
    var user:User ? = null

    companion object {
        public class User{
            var id:String ?= null
            var name:String ?= null
            var avatar:String ?=null
            var email :String ?= null
        }
    }

}