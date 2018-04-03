package fast.information.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fast.information.common.MyApplication
import fast.information.R
import fast.information.main.adapter.BoardAdapter
import kotlinx.android.synthetic.main.fragment_second.*

/**
* MyApplication
* Created by xiaqibo on 2018/3/1-0:19.
*/
class FragmentTwo : Fragment() {

    companion object {

        fun createInstance(argBundle : Bundle) : FragmentTwo {
            val instance = FragmentTwo()
            instance.arguments = argBundle
            return instance
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_second ,container , false )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("FragmentTwo" , "onViewCreated")
        recycler_view.adapter = BoardAdapter(MyApplication.instance)
        recycler_view.layoutManager = LinearLayoutManager(MyApplication.instance)
    }

}