package fast.information.main

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Created by xiaqibo on 2018/3/1.
 */
class FragmentThree : Fragment() {

    companion object {

        fun createInstance(argBundle : Bundle) : FragmentThree {
            val instance = FragmentThree()
            instance.arguments = argBundle
            return instance
        }

    }

}