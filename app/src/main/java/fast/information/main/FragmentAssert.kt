package fast.information.main

import android.os.Bundle
import fast.information.R
import fast.information.common.BaseFragment

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
}