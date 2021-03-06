package vn.ditagis.com.tanhoa.qlsc.fragment.listtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView

import com.esri.arcgisruntime.data.Feature
import kotlinx.android.synthetic.main.activity_list_task.*

import java.text.ParseException
import java.util.ArrayList
import java.util.Calendar
import java.util.Comparator
import java.util.TimeZone

import vn.ditagis.com.tanhoa.qlsc.ListTaskActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableGetListAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant

//import kotlin.Comparator

@SuppressLint("ValidFragment")
class ListTaskFragment @SuppressLint("ValidFragment")
constructor(private val mActivity: ListTaskActivity, inflater: LayoutInflater) : Fragment(), View.OnClickListener {
    private val mRootView: View = inflater.inflate(R.layout.fragment_list_task_list,
            mActivity.clayout__list_task__root_view as CoordinatorLayout, false)
    private lateinit var mLstChuaXuLy: ListView
    private lateinit var mLstDangXuLy: ListView
    private lateinit var mLstHoanThanh: ListView
    internal lateinit var mTxtChuaXuLy: TextView
    internal lateinit var mTxtDangXuLy: TextView
    internal lateinit var mTxtHoanThanh: TextView
    internal lateinit var mAdapterChuaXuLy: TraCuuAdapter
    internal lateinit var mAdapterDangXuLy: TraCuuAdapter
    internal lateinit var mAdapterHoanThanh: TraCuuAdapter

    init {

        init()
    }

    private fun init() {
        mLstChuaXuLy = mRootView.findViewById(R.id.lst_list_task_chua_xu_ly)
        mLstDangXuLy = mRootView.findViewById(R.id.lst_list_task_dang_xu_ly)
        mLstHoanThanh = mRootView.findViewById(R.id.lst_list_task_da_hoan_thanh)

        mTxtChuaXuLy = mRootView.findViewById(R.id.txt_list_task_chua_xu_ly)
        mTxtDangXuLy = mRootView.findViewById(R.id.txt_list_task_dang_xu_ly)
        mTxtHoanThanh = mRootView.findViewById(R.id.txt_list_task_hoan_thanh)
        mTxtChuaXuLy.setOnClickListener(this)
        mTxtDangXuLy.setOnClickListener(this)
        mTxtHoanThanh.setOnClickListener(this)

        mAdapterChuaXuLy = TraCuuAdapter(mActivity.applicationContext, ArrayList())
        mAdapterDangXuLy = TraCuuAdapter(mActivity.applicationContext, ArrayList())
        mAdapterHoanThanh = TraCuuAdapter(mActivity.applicationContext, ArrayList())

        mLstChuaXuLy.adapter = mAdapterChuaXuLy
        mLstDangXuLy.adapter = mAdapterDangXuLy
        mLstHoanThanh.adapter = mAdapterHoanThanh

        mLstChuaXuLy.setOnItemClickListener { adapterView, _, i, _ -> mActivity.itemClick(adapterView, i) }
        mLstDangXuLy.setOnItemClickListener { adapterView, _, i, _ -> mActivity.itemClick(adapterView, i) }
        mLstHoanThanh.setOnItemClickListener { adapterView, _, i, _ ->
            //            Toast.makeText(mActivity.getApplicationContext(), R.string.message_click_feature_complete,
            //                    Toast.LENGTH_SHORT).show();
            mActivity.itemClick(adapterView, i)
        }
        QueryServiceFeatureTableGetListAsync(mActivity, object : QueryServiceFeatureTableGetListAsync.AsyncResponse {
            override fun processFinish(output: List<Feature>?) {
                if (output != null && output.isNotEmpty()) {
                    handlingQuerySuccess(output)
                }
                mAdapterChuaXuLy.notifyDataSetChanged()
                mAdapterDangXuLy.notifyDataSetChanged()
                mAdapterHoanThanh.notifyDataSetChanged()

                mTxtChuaXuLy.text = mActivity.resources.getString(R.string.txt_list_task_chua_xu_ly, mAdapterChuaXuLy.count)
                mTxtDangXuLy.text = mActivity.resources.getString(R.string.txt_list_task_dang_xu_ly, mAdapterDangXuLy.count)
                mTxtHoanThanh.text = mActivity.resources.getString(R.string.txt_list_task_hoan_thanh, mAdapterHoanThanh.count)
            }

        }).execute()
    }


    private fun handlingQuerySuccess(output: List<Feature>?) {
        try {
            val chuaXuLyList = ArrayList<TraCuuAdapter.Item>()
            val dangXuLyList = ArrayList<TraCuuAdapter.Item>()
            val hoanThanhList = ArrayList<TraCuuAdapter.Item>()
            for (feature in output!!) {
                val attributes = feature.attributes
                Constant.DateFormat.DATE_FORMAT_VIEW.timeZone = TimeZone.getTimeZone("UTC")
                val item = TraCuuAdapter.Item(
                        Integer.parseInt(attributes[Constant.FieldSuCoThongTin.OBJECT_ID].toString()),
                        attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString(),
                        Integer.parseInt(attributes[Constant.FieldSuCoThongTin.TRANG_THAI].toString()),

                        Constant.DateFormat.DATE_FORMAT_VIEW.format((attributes[Constant.FieldSuCoThongTin.TG_GIAO_VIEC] as Calendar).time),
                        attributes[Constant.FieldSuCoThongTin.DIA_CHI].toString())
                val value = feature.attributes[Constant.FieldSuCoThongTin.TRANG_THAI]
                if (value == null) {
                    chuaXuLyList.add(item)
                } else {
                    val trangThai = java.lang.Short.parseShort(value.toString())
                    when (trangThai) {
                        Constant.TrangThaiSuCo.CHUA_XU_LY -> chuaXuLyList.add(item)
                        Constant.TrangThaiSuCo.DANG_XU_LY -> dangXuLyList.add(item)
                        Constant.TrangThaiSuCo.HOAN_THANH -> hoanThanhList.add(item)
                    }
                }
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
            chuaXuLyList.sortWith(Comparator(comparator))
            dangXuLyList.sortWith(Comparator(comparator))
            hoanThanhList.sortWith(Comparator(comparator))
            mAdapterChuaXuLy.addAll(chuaXuLyList)
            mAdapterDangXuLy.addAll(dangXuLyList)
            mAdapterHoanThanh.addAll(hoanThanhList)
        } catch (e: Exception) {
            Log.e("Lỗi lấy ds công việc", e.toString())
        }

    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.txt_list_task_chua_xu_ly -> if (mLstChuaXuLy.visibility == View.VISIBLE)
                mLstChuaXuLy.visibility = View.GONE
            else
                mLstChuaXuLy.visibility = View.VISIBLE
            R.id.txt_list_task_dang_xu_ly -> if (mLstDangXuLy.visibility == View.VISIBLE)
                mLstDangXuLy.visibility = View.GONE
            else
                mLstDangXuLy.visibility = View.VISIBLE
            R.id.txt_list_task_hoan_thanh -> if (mLstHoanThanh.visibility == View.VISIBLE)
                mLstHoanThanh.visibility = View.GONE
            else
                mLstHoanThanh.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mRootView
    }
}
