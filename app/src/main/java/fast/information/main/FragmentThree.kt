package fast.information.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fast.information.R

/**
* MyApplication
* Created by xiaqibo on 2018/3/1-0:18.
*/
class FragmentThree : Fragment() {

    companion object {

        fun createInstance(argBundle : Bundle) : FragmentThree {
            val instance = FragmentThree()
            instance.arguments = argBundle
            return instance
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_empty ,container , false )
    }
}