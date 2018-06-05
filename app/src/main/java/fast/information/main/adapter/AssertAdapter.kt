package fast.information.main.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.TextView
import com.bumptech.glide.Glide
import fast.information.CreateAssertActivity
import fast.information.R
import fast.information.common.MyApplication
import fast.information.network.bean.Assert
import kotlinx.android.synthetic.main.list_item_assert.view.*
import kotlinx.android.synthetic.main.list_item_assert_header.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by xiaqibo on 2018/5/31.
 */
class AssertAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: ArrayList<Assert> = ArrayList()

    private var paddingVertical: Float = 0f
    private var headHeight: Float = 0f
    private var tailHeight: Float = 0f

    private var current_value: Double = 0.0 //总利润
    private var totalCost: Double = 0.0 //成本
    private var _24h_Profile: Double = 0.0 //24小时涨跌额度
    private var _24_change_rate: Float = 0.00f
    private var simpleDateFormat :SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:SS" , Locale.CHINA)
    init {
        paddingVertical = context.resources.displayMetrics.density * 20 + 0.5f
        headHeight = context.resources.displayMetrics.density * 70 + 0.5f
        tailHeight = context.resources.displayMetrics.density * 210 + 0.5f
    }

    companion object {
        val ORDER_DATE :Int= 0
        val ORDER_PERCENT: Int = 1
        val ORDER_PROFIT: Int = 2
        val ORDER_RATE: Int = 3
        val ORDER_TODAY_RATE: Int = 4
        val ORDER_TODAY_PROFIT: Int = 5
        val ORDER_COST: Int = 6
    }

    private var currentOrderType = ORDER_DATE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0)
            object : RecyclerView.ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.list_item_assert, parent, false)) {}
        else
            object : RecyclerView.ViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.list_item_assert_header, parent, false)) {}
    }


    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return 1
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    fun update(d: List<Assert>?) {
        data.clear()
        if (d != null)
            data.addAll(d)
        totalCost = 0.0
        current_value = 0.0
        _24h_Profile = 0.0
        data.iterator().forEach { it ->
            run {
                totalCost += it.cost_price!!.toDouble() * it.amount!!.toDouble()
                current_value += it.amount!!.toDouble() * it.current_price_usdt!!.toDouble()
                _24h_Profile += it.amount!!.toDouble() * it.current_price_usdt!!.toDouble() * it.today_change_rate!!.toFloat() / (100)
            }
        }
        data.iterator().forEach { it ->
            run {
                it.percent = ((it.amount!!.toDouble() * it.current_price_usdt!!.toDouble()) / current_value).toFloat()
                val createDate = simpleDateFormat.parse(it.created_at)
                it.create_time = createDate.time
                it.created_at = Date(it.create_time!! + 8* 3600 * 1000L).toLocaleString()
                Log.e("created_at" , it.create_time.toString())
            }
        }

        currentOrderType = ORDER_DATE
        orderBy(currentOrderType)
        _24_change_rate = (_24h_Profile * 100 / current_value).toFloat()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            showTotalData(holder)
        } else {
            val item: Assert = data[position - 1]
            holder.itemView.coin.text = item.coin
            holder.itemView.amount.text = "x ".plus(item.amount)
            Glide.with(context).load(item.icon).into(holder.itemView.icon)
            holder.itemView.today_rate.setTextColor(
                    if (item.today_change_rate?.toFloat() ?: 0f < 0)
                        ContextCompat.getColor(context, R.color.change_red)
                    else
                        ContextCompat.getColor(context, R.color.change_green))
            holder.itemView.today_rate.text = "%.2f".format(item.today_change_rate?.toFloat()).plus("%/24h")

            val worth = item.amount?.toDouble()!! * item.current_price_usdt?.toDouble()!!
            holder.itemView.current_worth.text = ""
                    .plus("%.2f".format(worth))


            val cost = item.amount?.toDouble()!! * item.cost_price?.toDouble()!!
            holder.itemView.cost.text = "成本(USDT)："
                    .plus("%.2f".format(cost))

            holder.itemView.total_rate.text = "%.2f".format((worth / cost - 1) * 100)
                    .plus("%")

            holder.itemView.total_rate.setTextColor(
                    if ((worth / cost - 1) < 0)
                        ContextCompat.getColor(context, R.color.change_red)
                    else
                        ContextCompat.getColor(context, R.color.change_green))

            holder.itemView.edit.setOnClickListener({
                val bundle = Bundle()
                bundle.putSerializable("assert", item)
                MyApplication.instance.jumpActivity(CreateAssertActivity::class.java, bundle)
            })

            holder.itemView.current_price.text = item.current_price_usdt
            holder.itemView.cost_price.text = item.cost_price
            holder.itemView.total_profit.text = "%.2f".format(worth - cost)
            holder.itemView.percent.text = "%.2f".format(item.percent * 100).plus("%")
            holder.itemView.create_date.text = item.created_at
            holder.itemView.note.text = item.operate_date

            if (item.iiii) {
                holder.itemView.layoutParams.height = (headHeight + tailHeight + paddingVertical).toInt()
            } else {
                holder.itemView.layoutParams.height = (headHeight + paddingVertical).toInt()
            }

            holder.itemView.setOnClickListener({
                //            val headHeight = holder.itemView.head.height
//            val tailHeight = holder.itemView.tail.height
                Log.e("AssertAdapter", "headHeight is ".plus(headHeight).plus(" tailHeight is ").plus(tailHeight))
                var valueAnimator: ValueAnimator? = null
                val layoutParams = it.layoutParams
                valueAnimator = if (item.iiii) {
                    ValueAnimator.ofFloat(headHeight + tailHeight + paddingVertical, headHeight + paddingVertical)
                } else { // Gone
                    ValueAnimator.ofFloat(headHeight + paddingVertical, headHeight + tailHeight + paddingVertical)
                }
                valueAnimator.duration = 300
                valueAnimator.interpolator = DecelerateInterpolator( )
                valueAnimator.addUpdateListener { animation ->
                    val value = animation?.animatedValue as Float
                    layoutParams.height = value.toInt()
                    it.layoutParams = layoutParams
                }
                valueAnimator.start()
                item.iiii = !item.iiii

            })

        }
    }


    private fun showTotalData(holder: RecyclerView.ViewHolder) {
        holder.itemView.tamount.text = "%.02f".format(current_value)
        holder.itemView.rate_today.text = "今日盈亏："
                .plus("%.02f".format(_24h_Profile))
                .plus("          ")
                .plus("%.02f".format(_24_change_rate))
                .plus("%")
        holder.itemView.rate_total.text = "总盈亏："
                .plus("%.02f".format(current_value - totalCost))
                .plus("          ")
                .plus("%.02f".format((current_value - totalCost) * 100 / totalCost))
                .plus("%")

        holder.itemView.sort_mode.setOnClickListener({
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(MyApplication.instance.getLastActivity() as Context)
            dialogBuilder.setSingleChoiceItems(R.array.assert_sort_name, currentOrderType) { dialog, which ->
                orderBy(which)
                notifyDataSetChanged()
                (it as TextView).text = context.resources.getStringArray(R.array.assert_sort_name)[which]
                dialog?.dismiss()
            }
            dialogBuilder.create().show()
        })
        holder.itemView.sort_mode.text = context.resources.getStringArray(R.array.assert_sort_name)[currentOrderType]
    }


    private fun orderBy(type: Int) {
        var temp :List<Assert>?= null
        currentOrderType = type
        when (type) {
            ORDER_DATE ->{
                temp = data.sortedBy { it.created_at }
            }
            ORDER_PERCENT -> {
                temp = data.sortedByDescending { it.percent }
            }
            ORDER_PROFIT -> {
                temp = data.sortedByDescending {
                    it.amount?.toDouble()!! * (it.current_price_usdt?.toDouble()!! -  it.cost_price?.toDouble()!!)}
            }
            ORDER_RATE -> {
                temp = data.sortedByDescending { it.current_price_usdt?.toDouble()!! / it.cost_price?.toDouble()!!}
            }
            ORDER_TODAY_RATE -> {
                temp = data.sortedByDescending { it.today_change_rate }
            }
            ORDER_TODAY_PROFIT -> {
                temp = data.sortedByDescending {
                    it.current_price_usdt?.toDouble()!! * it.amount?.toDouble()!! * it.today_change_rate?.toDouble()!!  }
            }
            ORDER_COST -> {
                temp = data.sortedByDescending { it.cost_price?.toDouble()!! * it.amount?.toDouble()!! }
            }
        }
        if(temp != null){
            data.clear()
            data.addAll(temp)
        }
    }


    private fun badAnimation(view: View, holder: RecyclerView.ViewHolder) {
        if (holder.itemView.tail.visibility == View.VISIBLE) {
            val animSet: AnimationSet = AnimationSet(true)
//                    (float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
            val transAnim = TranslateAnimation(0f, 0f, holder.itemView.tail.height.toFloat(), 0f)
            transAnim.duration = 400
            animSet.addAnimation(transAnim)
//                    (float fromX, float toX, float fromY, float toY)
            val scaleAnim = ScaleAnimation(1.0f, 1.0f, 1.0f, 0f)
            scaleAnim.duration = 400
            animSet.addAnimation(scaleAnim)

            val interpolator: Interpolator = AccelerateInterpolator()
            animSet.interpolator = interpolator
            animSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    holder.itemView.tail.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {
                }

            })
            holder.itemView.tail.startAnimation(animSet)
        } else {
            holder.itemView.tail.visibility = View.VISIBLE
        }
    }
}