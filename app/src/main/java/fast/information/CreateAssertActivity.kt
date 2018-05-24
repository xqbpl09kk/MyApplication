package fast.information

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.media.DrmInitData
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import kotlinx.android.synthetic.main.activity_create_assert.* ;
import kotlinx.android.synthetic.main.fragment_assert.*
import java.util.*

/**
 * Created by xiaqibo on 2018/5/23.
 */
class CreateAssertActivity : BaseActivity() , PopupMenu.OnMenuItemClickListener{


    private var selectedPriceUnit :String = "USDT"
    private var selectedPriceMode :String = "all"
    private var selectedYear :Int = 0
    private var selectedMonth : Int = 0
    private var selectedDay : Int = 0
    private var selectedCoin :String = ""

    override fun getLayoutRes(): Int {
        return R.layout.activity_create_assert
    }


    @SuppressLint("SetTextI18n")
    override fun registerViews() {
        super.registerViews()
        coin_text.setOnClickListener({ startActivityForResult(Intent(this , CoinSelectActivity::class.java) , 1000) })
        price_unit.setOnClickListener({ createMenu(it , R.menu.price_unit) })
        price_mode.setOnClickListener({ createMenu(it , R.menu.price_mode) })
        bought_date.setOnClickListener({createDatePickerDialog() })
        bought_date.text = initDate()
        label_text.setOnClickListener({startActivityForResult(Intent(this , LabelActivity::class.java) , 1001)})
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == RESULT_OK){
            selectedCoin = data?.getStringExtra("coin") ?:""
            coin_text.text = selectedCoin
        }else if(requestCode == 1001 && resultCode == RESULT_OK){

        }
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item1 -> selectedPriceUnit = "USDT"
            R.id.item2 -> selectedPriceUnit = "BTC"
            R.id.item3 -> selectedPriceUnit = "ETH"
            R.id.item4 -> selectedPriceMode = "All"
            R.id.item5 -> selectedPriceMode = "Unit"
        }
        return true
    }


    private fun initDate():String {
        val today : Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
        selectedYear = today.get(Calendar.YEAR)
        selectedMonth = today.get(Calendar.MONTH)
        selectedDay = today.get(Calendar.DAY_OF_MONTH)
        return "%4d - %02d - %02d".format(selectedYear , selectedMonth + 1 , selectedDay)
    }




    @SuppressLint("SetTextI18n")
    private fun createDatePickerDialog(){
        var dialog = DatePickerDialog(this@CreateAssertActivity ,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth
                    bought_date.text = "%4d - %02d - %02d".format(selectedYear , selectedMonth + 1 , selectedDay)
                }
                ,selectedYear,selectedMonth,selectedDay)
        dialog.show()
    }

    private fun createMenu(view : View , menuRes : Int){
        val popupMenu = PopupMenu(MyApplication.instance, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(menuRes, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(this@CreateAssertActivity)
    }
}