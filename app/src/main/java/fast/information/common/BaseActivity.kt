package fast.information.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by xiaqibo on 2018/4/10.
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        registerViews()
        MyApplication.instance.onActivityCreate(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.instance.onActivityDestroy(this)
    }

    open fun displayHomeAsUpEnabled (): Boolean{
        return true
    }


    open fun registerViews(){
        supportActionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled())
    }

    @LayoutRes
    abstract fun getLayoutRes():Int

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}