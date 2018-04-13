package fast.information.common

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fast.information.network.bean.base.ResultBundle
import retrofit2.Call

/**
 * Created by xiaqibo on 2018/4/13.
 */
abstract class BaseFragment: Fragment(){

    protected var contentView :View ?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(contentView != null){
            val parent: ViewGroup = contentView?.parent as ViewGroup
            parent.removeView(contentView)
        }else
            contentView = inflater.inflate(getLayoutRes() , container , false)
        return contentView
    }

    @LayoutRes abstract fun getLayoutRes():Int

    override fun onDestroy() {
        super.onDestroy()

    }
}