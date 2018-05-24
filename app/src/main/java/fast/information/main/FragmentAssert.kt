package fast.information.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import fast.information.CreateAssertActivity
import fast.information.R
import fast.information.common.BaseFragment
import fast.information.common.MyApplication
import kotlinx.android.synthetic.main.fragment_assert.*

/**
 * Created by xiaqibo on 2018/5/22.
 */
class FragmentAssert: BaseFragment() {

    companion object {

        fun createInstance(argBundle : Bundle) : FragmentAssert {
            val instance = FragmentAssert()
            instance.arguments = argBundle
            return instance
        }

    }
    override fun getLayoutRes(): Int {
        return R.layout.fragment_assert
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_btn.setOnClickListener({

        })
        fab.setOnClickListener({
            startActivity(Intent(MyApplication.instance.getLastActivity() , CreateAssertActivity::class.java))
        })

    }
}