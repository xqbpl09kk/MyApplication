package fast.information.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import fast.information.ConcernActivity
import fast.information.MyApplication
import fast.information.R
import fast.information.SettingsActivity

import kotlinx.android.synthetic.main.fragment_more.*

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
        return inflater.inflate(R.layout.fragment_more ,container , false )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        concern.setOnClickListener({ startActivity(Intent(context , ConcernActivity::class.java)) })
        settings.setOnClickListener({ startActivity(Intent(context , SettingsActivity::class.java)) })
        comment.setOnClickListener({
            try{
                startActivity(Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("market://details?id="
                                .plus(MyApplication.instance.packageName))))
            }catch (e:Exception){
                Toast.makeText(MyApplication.instance , R.string.no_market_app,Toast.LENGTH_SHORT).show()
            }
        })
    }

}