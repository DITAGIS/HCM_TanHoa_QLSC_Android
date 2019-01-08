package vn.ditagis.com.tanhoa.qlsc.fragment.listtask

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain
import com.esri.arcgisruntime.data.Feature
import kotlinx.android.synthetic.main.activity_list_task.*

import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Comparator
import java.util.GregorianCalendar
import java.util.TimeZone

import vn.ditagis.com.tanhoa.qlsc.ListTaskActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter
import vn.ditagis.com.tanhoa.qlsc.async.QueryFeatureAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

//import kotlin.Comparator


@SuppressLint("ValidFragment")
class SearchFragment @SuppressLint("ValidFragment")
constructor(private val mActivity: ListTaskActivity, inflater: LayoutInflater) : Fragment(), View.OnClickListener {
    private val mRootView: View = inflater.inflate(R.layout.fragment_list_task_search,
            mActivity.clayout__list_task__root_view as CoordinatorLayout, false)
    private var mEtxtAddress: EditText? = null
    private var mSpinTrangThai: Spinner? = null
    private var mTxtThoiGian: TextView? = null
    private var mBtnSearch: Button? = null
    private var mLstKetQua: ListView? = null
    private var mLayoutKetQua: LinearLayout? = null

    private val mApplication: DApplication = mActivity.application as DApplication
    private var mCodeValues: List<CodedValue>? = null
    private var mFeaturesResult: List<Feature>? = null

    init {
        init()
    }

    private fun init() {
        mEtxtAddress = mRootView.findViewById(R.id.etxt_list_task_search_address)
        mSpinTrangThai = mRootView.findViewById(R.id.spin_list_task_search_trang_thai)
        mTxtThoiGian = mRootView.findViewById(R.id.txt_list_task_search_thoi_gian)
        mBtnSearch = mRootView.findViewById(R.id.btn_list_task_search)
        mLstKetQua = mRootView.findViewById(R.id.lst_list_task_search)
        mLayoutKetQua = mRootView.findViewById(R.id.llayout_list_task_search_ket_qua)

        mBtnSearch!!.setOnClickListener(this)
        mTxtThoiGian!!.setOnClickListener(this)
        initSpinTrangThai()
        initListViewKetQuaTraCuu()
    }

    private fun initSpinTrangThai() {
        val domain = mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin!!.getField(Constant.FieldSuCo.TRANG_THAI).domain
        if (domain != null) {
            mCodeValues = (domain as CodedValueDomain).codedValues
            if (mCodeValues != null) {
                val codes = ArrayList<String>()
                codes.add("Tất cả")
                for (codedValue in mCodeValues!!)
                    codes.add(codedValue.name)
                val adapter = ArrayAdapter(mRootView.context, android.R.layout.simple_list_item_1, codes)
                mSpinTrangThai!!.adapter = adapter
            }
        }
    }

    private fun initListViewKetQuaTraCuu() {
        mLstKetQua!!.setOnItemClickListener { adapterView, _, i, _ -> mActivity.itemClick(adapterView, i) }
    }

    private fun showDateTimePicker() {
        val dialogView = View.inflate(mRootView.context, R.layout.date_time_picker, null)
        val alertDialog = android.app.AlertDialog.Builder(mRootView.context).create()
        dialogView.findViewById<View>(R.id.date_time_set).setOnClickListener {
            val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
            val calendar = GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val displaytime = DateFormat.format(Constant.DateFormat.DATE_FORMAT_STRING, calendar.time) as String
            @SuppressLint("SimpleDateFormat") val dateFormatGmt = Constant.DateFormat.DATE_FORMAT_YEAR_FIRST
            dateFormatGmt.timeZone = TimeZone.getTimeZone("GMT")
            mTxtThoiGian!!.text = displaytime
            alertDialog.dismiss()
        }
        alertDialog.setView(dialogView)
        alertDialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun traCuu() {
        //        if (!mTxtThoiGian.getText().toString().equals(mRootView.getContext().getString(R.string.txt_chon_thoi_gian_tracuusuco))) {
        mLayoutKetQua!!.visibility = View.GONE
        var trangThai: Short = -1
        for (codedValue in mCodeValues!!) {
            if (codedValue.name == mSpinTrangThai!!.selectedItem.toString()) {
                trangThai = java.lang.Short.parseShort(codedValue.code.toString())
            }
        }
        QueryFeatureAsync(mActivity, trangThai.toInt(),
                mEtxtAddress!!.text.toString(),
                mTxtThoiGian!!.text.toString(), object : QueryFeatureAsync.AsyncResponse {
            override fun processFinish(output: List<Feature>?) {
                if (output != null && output.isNotEmpty()) {
                    mFeaturesResult = output
                    handlingTraCuuHoanTat()
                }
            }
        }).execute()
        //        } else
        //            Toast.makeText(mRootView.getContext(), "Vui lòng chọn thời gian", Toast.LENGTH_SHORT).show();
    }

    private fun handlingTraCuuHoanTat() {
        val items = ArrayList<TraCuuAdapter.Item>()
        try{   for (feature in mFeaturesResult!!) {
            val attributes = feature.attributes
            for (codedValue in mCodeValues!!) {
                if (java.lang.Short.parseShort(codedValue.code.toString()) == java.lang.Short.parseShort(attributes[Constant.FieldSuCoThongTin.TRANG_THAI].toString())) {
                    Constant.DateFormat.DATE_FORMAT_VIEW.timeZone = TimeZone.getTimeZone("UTC")
                    items.add(TraCuuAdapter.Item(Integer.parseInt(attributes[Constant.FieldSuCoThongTin.OBJECT_ID].toString()),
                            attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString(),
                            Integer.parseInt(attributes[Constant.FieldSuCoThongTin.TRANG_THAI].toString()),
                            Constant.DateFormat.DATE_FORMAT_VIEW.format((attributes[Constant.FieldSuCoThongTin.TG_GIAO_VIEC] as Calendar).time),
                            attributes[Constant.FieldSuCoThongTin.DIA_CHI].toString()))
                }
            }

        }}catch (ex: Exception){
            Toast.makeText(mRootView.context,"Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
        }
        val comparator = { o1: TraCuuAdapter.Item, o2: TraCuuAdapter.Item ->
            try {
                Constant.DateFormat.DATE_FORMAT_VIEW.timeZone = TimeZone.getTimeZone("UTC")
                val i = Constant.DateFormat.DATE_FORMAT_VIEW.parse(o2.ngayGiaoViec).time - Constant.DateFormat.DATE_FORMAT_VIEW.parse(o1.ngayGiaoViec).time
                when {
                    i > 0 -> 1
                    i == 0L -> 0
                    else -> -1
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }


        }
        items.sortWith(Comparator(comparator))
        val adapter = TraCuuAdapter(mRootView.context, items)
        mLstKetQua!!.adapter = adapter
        mLayoutKetQua!!.visibility = View.VISIBLE
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mRootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_list_task_search -> traCuu()
            R.id.txt_list_task_search_thoi_gian -> showDateTimePicker()
        }
    }
}
