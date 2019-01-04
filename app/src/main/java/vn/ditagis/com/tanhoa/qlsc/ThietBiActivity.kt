package vn.ditagis.com.tanhoa.qlsc

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter

import java.util.ArrayList
import java.util.Objects

import kotlinx.android.synthetic.main.activity_thiet_bi.*
import vn.ditagis.com.tanhoa.qlsc.adapter.ThietBiAdapter
import vn.ditagis.com.tanhoa.qlsc.async.HoSoThietBiSuCoAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB
import vn.ditagis.com.tanhoa.qlsc.utities.APICompleteAsync
import vn.ditagis.com.tanhoa.qlsc.utities.MySnackBar

class ThietBiActivity : AppCompatActivity() {
    private var mApplication: DApplication? = null
    private var mIDSuCoTT: String? = null
    private var mAdapter: ThietBiAdapter? = null
    private var mIsComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thiet_bi)

        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowHomeEnabled(true)
        mApplication = application as DApplication
        mIsComplete = java.lang.Short.parseShort(mApplication!!.arcGISFeature!!.attributes[Constant.FIELD_SUCOTHONGTIN.TRANG_THAI].toString()) == Constant.TRANG_THAI_SU_CO.HOAN_THANH
        mIDSuCoTT = mApplication!!.arcGISFeature!!.attributes[Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT].toString()
        //        QueryServiceFeatureTableAsync queryServiceFeatureTableAsync = new QueryServiceFeatureTableAsync(
        //                this, mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin(), output -> {
        init()
        loadThietBi()
        //            mIDSuCoTT = output.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT).toString();
        //        });
        //
        //        String queryClause = String.format("%s = '%s' and %s = '%s'",
        //                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
        //                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
        //        QueryParameters queryParameters = new QueryParameters();
        //        queryParameters.setWhereClause(queryClause);
        //        queryServiceFeatureTableAsync.execute(queryParameters);

        //        if (mIsComplete) {
        //            mLLayoutAdd.setVisibility(View.GONE);
        //            mLLayoutUpdate.setVisibility(View.GONE);
        //        }
    }

    private fun init() {
        val listTenThietBis = ArrayList<String>()
        try {
            for (thietBi in ListObjectDB.instance.thietBis!!)
                listTenThietBis.add(thietBi.tenThietBi!!)
        } catch (ignored: Exception) {

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listTenThietBis)
        autoCompleteTV_thietbi!!.setAdapter(adapter)

        mAdapter = ThietBiAdapter(this, ArrayList<ThietBiAdapter.Item>())
        val maThietBi = arrayOf("")
        lstview_thietbi!!.adapter = mAdapter
        //        if (!mIsComplete) {
        lstview_thietbi!!.setOnItemLongClickListener { adapterView, view, i, l ->
            val itemThietBi = adapterView.adapter.getItem(i) as ThietBiAdapter.Item
            val builder = AlertDialog.Builder(this@ThietBiActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
            builder.setTitle("Xóa thiết bị")
            builder.setMessage("Bạn có chắc muốn xóa thiết bị " + itemThietBi.tenThietBi)
            builder.setCancelable(false).setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }.setPositiveButton("Xóa") { dialogInterface, i12 ->
                mAdapter!!.remove(itemThietBi)
                mAdapter!!.notifyDataSetChanged()
                dialogInterface.dismiss()
            }
            val dialog = builder.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()
            false
        }
        autoCompleteTV_thietbi!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val tenThietBi = editable.toString()
                for (thietBi in ListObjectDB.instance.thietBis!!) {
                    if (thietBi.tenThietBi == tenThietBi) {
                        maThietBi[0] = thietBi.maThietBi!!
                        break
                    }
                }

            }
        })
        txt_thietbi_add!!.setOnClickListener { view ->
            if (etxt_thietbi_thoigian!!.text.toString().trim { it <= ' ' }.length == 0)
                MySnackBar.make(etxt_thietbi_thoigian!!, this@ThietBiActivity.getString(R.string.message_soluong_themhoso), true)
            else {
                try {
                    val soLuong = java.lang.Double.parseDouble(etxt_thietbi_thoigian!!.text.toString())
                    mAdapter!!.add(ThietBiAdapter.Item(autoCompleteTV_thietbi!!.text.toString(),
                            soLuong, maThietBi[0]))
                    mAdapter!!.notifyDataSetChanged()

                    autoCompleteTV_thietbi!!.setText("")
                    etxt_thietbi_thoigian!!.setText("")

                    if (lstview_thietbi!!.height > 500) {
                        val params = lstview_thietbi!!.layoutParams
                        params.height = 500
                        lstview_thietbi!!.layoutParams = params
                    }
                } catch (e: NumberFormatException) {
                    MySnackBar.make(etxt_thietbi_thoigian!!, this@ThietBiActivity.getString(R.string.message_number_format_exception), true)
                }

                mAdapter!!.notifyDataSetChanged()
            }
        }
        btn_thietbi_update!!.setOnClickListener { view -> updateEdit() }
        //        }
    }

    private fun loadThietBi() {
        txt_thietbi_status!!.text = Html.fromHtml(getString(R.string.info_thietbi_loading), Html.FROM_HTML_MODE_LEGACY)

        val hoSoThietBiSuCoAsync = HoSoThietBiSuCoAsync(this, object : HoSoThietBiSuCoAsync.AsyncResponse {
            override fun processFinish(`object`: Any?) {
                if (`object` != null) {
                    try {
                        val hoSoThietBiSuCos = `object` as List<HoSoThietBiSuCo>
                        for (hoSoThietBiSuCo in hoSoThietBiSuCos) {
                            mAdapter!!.add(ThietBiAdapter.Item(hoSoThietBiSuCo.tenThietBi, hoSoThietBiSuCo.thoigianVanHanh,
                                    hoSoThietBiSuCo.maThietBi))
                        }
                    } catch (e: Exception) {
                        Log.e("Lỗi ép kiểu thiết bị", e.toString())
                    }

                    mAdapter!!.notifyDataSetChanged()
                    lstview_thietbi!!.adapter = mAdapter
                }
                txt_thietbi_status!!.text = ""
            }

        })
        hoSoThietBiSuCoAsync.execute(Constant.HOSOSUCO_METHOD.FIND, mIDSuCoTT)

    }

    private fun updateEdit() {
        if (lstview_thietbi!!.adapter != null && lstview_thietbi!!.adapter.count == 0) {
            MySnackBar.make(lstview_thietbi!!, getString(R.string.message_CapNhat_ThietBi), true)
        } else {
            val thietBiAdapter = lstview_thietbi!!.adapter as ThietBiAdapter
            val hoSoThietBiSuCos = ArrayList<HoSoThietBiSuCo>()
            for (itemThietBi in thietBiAdapter.getItems()) {
                hoSoThietBiSuCos.add(HoSoThietBiSuCo(mIDSuCoTT!!,
                        itemThietBi.soLuong, itemThietBi.maThietBi, itemThietBi.tenThietBi!!))
            }
            ListObjectDB.instance.setLstHoSoThietBiSuCoInsert(hoSoThietBiSuCos)
            if (ListObjectDB.instance.getLstHoSoThietBiSuCoInsert()!!.size > 0) {
                val hoSoThietBiSuCoInsertAsync = HoSoThietBiSuCoAsync(this,
                        object : HoSoThietBiSuCoAsync.AsyncResponse {
                            override fun processFinish(`object`: Any?) {
                                try {
                                    if (`object` != null) {
                                        val isDone = `object` as Boolean
                                        //                            Object[] a = (Object[]) object;
                                        //                            boolean isDone = (boolean) a[0];
                                        if (isDone) {
                                            if (mIsComplete)
                                                APICompleteAsync(mApplication!!, mApplication!!.arcGISFeature!!.attributes[Constant.FIELD_SUCOTHONGTIN.ID_SUCO].toString())
                                                        .execute()
                                            goHome()
                                            txt_thietbi_status!!.text = Html.fromHtml(this@ThietBiActivity.getString(R.string.info_thietbi_complete), Html.FROM_HTML_MODE_LEGACY)
                                        } else
                                            txt_thietbi_status!!.text = Html.fromHtml(this@ThietBiActivity.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY)
                                    } else
                                        txt_thietbi_status!!.text = Html.fromHtml(this@ThietBiActivity.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY)
                                } catch (e: Exception) {
                                    txt_thietbi_status!!.text = Html.fromHtml(this@ThietBiActivity.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY)
                                }
                            }

                        })
                hoSoThietBiSuCoInsertAsync.execute(Constant.HOSOSUCO_METHOD.INSERT, mIDSuCoTT)
            }
            var check: Boolean
            do {
                check = ListObjectDB.instance.getLstHoSoThietBiSuCoInsert()!!.size > 0
            } while (check)
        }
    }

    override fun onBackPressed() {
        goHome()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun goHome() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}
