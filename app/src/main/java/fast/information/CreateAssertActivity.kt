package fast.information

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.media.DrmInitData
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import fast.information.common.BaseActivity
import fast.information.common.MyApplication
import fast.information.network.RetrofitHelper
import fast.information.network.bean.Assert
import fast.information.network.bean.AssertGroup
import fast.information.network.bean.base.ResultBundle
import fast.information.network.bean.base.ResultCallback
import fast.information.network.bean.base.ResultListBundle
import kotlinx.android.synthetic.main.activity_create_assert.*;
import kotlinx.android.synthetic.main.fragment_assert.*
import java.util.*

/**
 * Created by xiaqibo on 2018/5/23.
 */
class CreateAssertActivity : BaseActivity(), PopupMenu.OnMenuItemClickListener {

    companion object {
        val PRICE_UNIT_USDT: String = "USDT"
        val PRICE_UNIT_BTC: String = "BTC"
        val PRICE_UNIT_ETH: String = "ETH"
        val PRICE_MODE_ALL: String = "ALL"
        val PRICE_MODE_UNIT: String = "UNIT"
    }


    private var selectedPriceUnit: String = PRICE_UNIT_USDT
    private var selectedPriceMode: String = PRICE_MODE_UNIT
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    private var selectedCoin: String = ""
    private var selectedGroup: AssertGroup? = null


    private var editAssert :Assert ?= null
    override fun getLayoutRes(): Int {
        return R.layout.activity_create_assert
    }

    //test


    @SuppressLint("SetTextI18n")
    override fun registerViews() {
        super.registerViews()
        coin_text.setOnClickListener({ startActivityForResult(Intent(this, CoinSelectActivity::class.java), 1000) })
        price_unit.setOnClickListener({ createMenu(it, R.menu.price_unit) })
        price_mode.setOnClickListener({ createMenu(it, R.menu.price_mode) })
        bought_date.setOnClickListener({ createDatePickerDialog() })
        bought_date.text = initDate()
        label_text.setOnClickListener({ startActivityForResult(Intent(this, LabelActivity::class.java), 1001) })
        val data  = intent.getBundleExtra("data")?.getSerializable("assert")
        if(data == null){
            Handler().post({coin_text.performClick()})
            delete.visibility = View.GONE
        }else{
            editAssert = data as Assert
            delete.visibility = View.VISIBLE
            amount_editor.setText(editAssert!!.amount)
            coin_text.text  = editAssert?.coin
            coin_text.isEnabled = false


            selectedCoin = editAssert?.coin?:""
            price_editor.setText(editAssert!!.cost_price)
            delete.setOnClickListener({
                RetrofitHelper.instance.deleteAssert(editAssert?.id!! , object : ResultCallback<ResultListBundle<Assert>> {
                    override fun onSuccess(t: ResultListBundle<Assert>?) {
                        Toast.makeText(MyApplication.instance , "delete assert success " , Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(message: String, errorCode: Int) {
                        Toast.makeText(MyApplication.instance , message , Toast.LENGTH_SHORT).show()
                    }

                })
            })
        }
        RetrofitHelper.instance.assertGroup(object:ResultCallback<ResultListBundle<AssertGroup>>{
            override fun onSuccess(t: ResultListBundle<AssertGroup>?) {
                val groupId = editAssert?.group_id ?:"default"
                t?.items?.map { if(it.id.equals(groupId)){
                    selectedGroup = it
                    label_text.text = selectedGroup?.name
                } }
            }

            override fun onFailure(message: String, errorCode: Int) {
                Toast.makeText(MyApplication.instance , "获取分组信息失败" , Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save) {

            val amount = amount_editor.text.toString()
            var price = price_editor.text.toString()
            val unit_price = unit_price_text.text.toString()
            selectedCoin = coin_text.text.toString()
            if(TextUtils.isEmpty(amount)
                    ||TextUtils.isEmpty(price)
                    ||TextUtils.isEmpty(selectedCoin)){
                Toast.makeText(MyApplication.instance ,R.string.complete_assert_information , Toast.LENGTH_SHORT).show()
                return false
            }
            if(selectedGroup == null){
                Toast.makeText(MyApplication.instance , R.string.select_group_first , Toast.LENGTH_SHORT).show()
                return false
            }
            if (selectedPriceMode == PRICE_MODE_ALL) {
                price = (price.toDouble() / amount.toDouble()).toString()
            }
            if(selectedPriceUnit != PRICE_UNIT_USDT){
                price = (unit_price.toDouble() * price.toDouble()).toString()
            }
            val position = ""
            val exchange = ""
            val wallet_address = ""
            val operation_date = "%02d%02d%02d".format(selectedYear, selectedMonth + 1, selectedDay)
            if(editAssert == null ){
                RetrofitHelper.instance.createAssert(selectedCoin
                        , "USDT", amount, price
                        , position, exchange, wallet_address
                        , operation_date, selectedGroup ?.id?:"" ,note_editor.text.toString() ,object : ResultCallback<ResultBundle<Assert>> {
                    override fun onSuccess(t: ResultBundle<Assert>?) {
                        Toast.makeText(MyApplication.instance, "Assert create success !", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(message: String, errorCode: Int) {
                        Toast.makeText(MyApplication.instance, "Assert create failed !!! !", Toast.LENGTH_SHORT).show()
                    }

                })
            }else{
                RetrofitHelper.instance.editAssert(editAssert!!.id!!, selectedCoin
                        ,"USDT" , amount , price ,position
                        , exchange , wallet_address ,operation_date
                        ,selectedGroup?.id?:"" ,note_editor.text.toString()
                    , object : ResultCallback<ResultBundle<Assert>> {
                    override fun onSuccess(t: ResultBundle<Assert>?) {
                        Toast.makeText(MyApplication.instance, "Assert edit success !", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(message: String, errorCode: Int) {
                        Toast.makeText(MyApplication.instance, "Assert edit failed !!! !", Toast.LENGTH_SHORT).show()
                    }

                } )
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkParams() : Boolean {
        if(TextUtils.isEmpty(selectedCoin )){
            Toast.makeText(MyApplication.instance , R.string.input_coin_name , Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            selectedCoin = data?.getStringExtra("coin") ?: ""
            coin_text.text = selectedCoin
        } else if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedGroup = data?.getSerializableExtra("assert_group") as AssertGroup
            label_text.text = selectedGroup?.name
        }
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item1 -> {
                selectedPriceUnit = PRICE_UNIT_USDT
                price_unit.text = PRICE_UNIT_USDT
                unit_price_container.visibility = View.GONE
            }
            R.id.item2 -> {
                selectedPriceUnit = PRICE_UNIT_BTC
                price_unit.text = PRICE_UNIT_BTC
                unit_price_container.visibility = View.VISIBLE
            }
            R.id.item3 -> {
                selectedPriceUnit = PRICE_UNIT_ETH
                price_unit.text = PRICE_UNIT_ETH
                unit_price_container.visibility = View.VISIBLE
            }
            R.id.item4 -> {
                selectedPriceMode = PRICE_MODE_ALL
                price_mode.text = getString(R.string.total_price)
            }
            R.id.item5 -> {
                selectedPriceMode = PRICE_MODE_UNIT
                price_mode.text = getString(R.string.unit_price)
            }
        }
        return true
    }


    private fun initDate(): String {
        val today: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"))
        selectedYear = today.get(Calendar.YEAR)
        selectedMonth = today.get(Calendar.MONTH)
        selectedDay = today.get(Calendar.DAY_OF_MONTH)
        return "%4d - %02d - %02d".format(selectedYear, selectedMonth + 1, selectedDay)
    }


    @SuppressLint("SetTextI18n")
    private fun createDatePickerDialog() {
        val dialog = DatePickerDialog(this@CreateAssertActivity,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth
                    bought_date.text = "%4d - %02d - %02d".format(selectedYear, selectedMonth + 1, selectedDay)
                }
                , selectedYear, selectedMonth, selectedDay)
        dialog.show()
    }

    private fun createMenu(view: View, menuRes: Int) {
        val popupMenu = PopupMenu(MyApplication.instance, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(menuRes, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(this@CreateAssertActivity)
    }
}
