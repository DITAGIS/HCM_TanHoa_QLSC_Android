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

import kotlinx.android.synthetic.main.activity_vat_tu.*
import vn.ditagis.com.tanhoa.qlsc.adapter.VatTuAdapter
import vn.ditagis.com.tanhoa.qlsc.async.HoSoVatTuSuCoAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB
import vn.ditagis.com.tanhoa.qlsc.utities.APICompleteAsync
import vn.ditagis.com.tanhoa.qlsc.utities.MySnackBar

class VatTuActivity : AppCompatActivity() {

    private var mApplication: DApplication? = null
    private var mIDSuCoTT: String? = null
    private var mAdapter: VatTuAdapter? = null
    private var mIsComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vat_tu)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowHomeEnabled(true)
        mApplication = application as DApplication
        mIsComplete = java.lang.Short.parseShort(mApplication!!.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.TRANG_THAI].toString()) == Constant.TrangThaiSuCo.HOAN_THANH
        when (mApplication!!.loaiVatTu) {
            Constant.CodeVatTu.CAPMOI -> title = "Vật tư cấp mới"
            Constant.CodeVatTu.THUHOI -> title = "Vật tư thu hồi"
        }

        mIDSuCoTT = mApplication!!.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCOTT].toString()
        //        QueryServiceFeatureTableAsync queryServiceFeatureTableAsync = new QueryServiceFeatureTableAsync(
        //                this, mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin(), output -> {
        init()
        loadVatTu()

        //        });

        //        String queryClause = String.format("%s = '%s' and %s = '%s'",
        //                Constant.FieldSuCoThongTin.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FieldSuCoThongTin.ID_SUCO).toString(),
        //                Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
        //        QueryParameters queryParameters = new QueryParameters();
        //        queryParameters.setWhereClause(queryClause);
        //        queryServiceFeatureTableAsync.execute(queryParameters);

        //        if (mIsComplete) {
        //            mLLayoutAdd.setVisibility(View.GONE);
        //            mLLayoutUpdate.setVisibility(View.GONE);
        //        }
    }

    private fun loadVatTu() {
        txt_status_vattu!!.text = Html.fromHtml(getString(R.string.info_vattu_loading), Html.FROM_HTML_MODE_LEGACY)

        val hoSoVatTuSuCoAsync = HoSoVatTuSuCoAsync(this,
                object : HoSoVatTuSuCoAsync.AsyncResponse {
                    override fun processFinish(`object`: Any?) {
                        if (`object` != null && `object` is List<*>) {
                            try {
                                for (item in `object`) {
                                    if (item is HoSoVatTuSuCo)
                                        mAdapter!!.add(VatTuAdapter.Item(item.tenVatTu, item.soLuong,
                                                item.donViTinh, item.maVatTu))
                                }
                            } catch (e: Exception) {
                                Log.e("Lỗi ép kiểu vật tư", e.toString())
                            }

                            mAdapter!!.notifyDataSetChanged()
                            lstview_vattu!!.adapter = mAdapter
                        }
                        txt_status_vattu!!.text = ""
                    }

                })
        hoSoVatTuSuCoAsync.execute(Constant.HoSoSuCoMethod.FIND, mIDSuCoTT)

    }


    private fun init() {
        val listTenVatTus = ArrayList<String>()
        try {
            for (vatTu in ListObjectDB.instance.vatTus!!)
                listTenVatTus.add(vatTu.tenVatTu!!)
        } catch (ignored: Exception) {

        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listTenVatTus)
        autoCompleteTV_vattu!!.setAdapter(adapter)

        mAdapter = VatTuAdapter(this, ArrayList())
        val maVatTu = arrayOf("")
        lstview_vattu!!.adapter = mAdapter
        //        if (!mIsComplete) {
        lstview_vattu!!.setOnItemLongClickListener { adapterView, _, i, _ ->
            val itemVatTu = adapterView.adapter.getItem(i) as VatTuAdapter.Item
            val builder = AlertDialog.Builder(this@VatTuActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
            builder.setTitle("Xóa vật tư")
            builder.setMessage("Bạn có chắc muốn xóa vật tư " + itemVatTu.tenVatTu)
            builder.setCancelable(false).setNegativeButton(
                    "Hủy") { dialog, _ -> dialog.dismiss()
            }.setPositiveButton("Xóa") { dialogInterface, _ ->
                mAdapter!!.remove(itemVatTu)
                mAdapter!!.notifyDataSetChanged()
                dialogInterface.dismiss()
            }
            val dialog = builder.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()
            false
        }
        autoCompleteTV_vattu!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val tenVatTu = editable.toString()
                for (vatTu in ListObjectDB.instance.vatTus!!) {
                    if (vatTu.tenVatTu == tenVatTu) {
                        txt_donvitinh_vattu!!.text = vatTu.donViTinh
                        maVatTu[0] = vatTu.maVatTu!!
                        break
                    }
                }

            }
        })
        txt_them_vattu!!.setOnClickListener {
            if (etxt_soLuong_vattu!!.text.toString().trim { item -> item <= ' ' }.isEmpty())
                MySnackBar.make(etxt_soLuong_vattu!!, this@VatTuActivity.getString(R.string.message_soluong_themhoso), true)
            else {
                try {
                    val soLuong = java.lang.Double.parseDouble(etxt_soLuong_vattu!!.text.toString())
                    mAdapter!!.add(VatTuAdapter.Item(autoCompleteTV_vattu!!.text.toString(),
                            soLuong, txt_donvitinh_vattu!!.text.toString(), maVatTu[0]))
                    mAdapter!!.notifyDataSetChanged()

                    autoCompleteTV_vattu!!.setText("")
                    etxt_soLuong_vattu!!.setText("")
                    txt_donvitinh_vattu!!.text = ""

                    if (lstview_vattu!!.height > 500) {
                        val params = lstview_vattu!!.layoutParams
                        params.height = 500
                        lstview_vattu!!.layoutParams = params
                    }
                } catch (e: NumberFormatException) {
                    MySnackBar.make(etxt_soLuong_vattu!!, this@VatTuActivity.getString(R.string.message_number_format_exception), true)
                }

                mAdapter!!.notifyDataSetChanged()
            }
        }
        btn_update_vattu!!.setOnClickListener { updateEdit() }
    }
    //    }

    private fun updateEdit() {
        if (lstview_vattu!!.adapter != null && lstview_vattu!!.adapter.count == 0) {
            MySnackBar.make(lstview_vattu!!, getString(R.string.message_CapNhat_VatTu), true)
        } else {
            val vatTuAdapter = lstview_vattu!!.adapter as VatTuAdapter
            val hoSoVatTuSuCos = ArrayList<HoSoVatTuSuCo>()
            for (itemVatTu in vatTuAdapter.getItems()) {
                hoSoVatTuSuCos.add(HoSoVatTuSuCo(mIDSuCoTT!!,
                        itemVatTu.soLuong, itemVatTu.maVatTu, itemVatTu.tenVatTu!!, itemVatTu.donVi))
            }
            ListObjectDB.instance.setLstHoSoVatTuSuCoInsert(hoSoVatTuSuCos)
            if (ListObjectDB.instance.getLstHoSoVatTuSuCoInsert()!!.isNotEmpty()) {
                val hoSoVatTuSuCoAsyncInsert = HoSoVatTuSuCoAsync(this,
                        object : HoSoVatTuSuCoAsync.AsyncResponse {
                            override fun processFinish(`object`: Any?) {
                                try {
                                    if (`object` != null) {
                                        val isDone = `object` as Boolean
                                        //                            Object[] a = (Object[]) object;
                                        //                            boolean isDone = (boolean) a[0];
                                        if (isDone) {
                                            if (mIsComplete)
                                                APICompleteAsync(mApplication!!, mApplication!!.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString())
                                                        .execute()
                                            goHome()
                                            txt_status_vattu!!.text = Html.fromHtml(this@VatTuActivity.getString(R.string.info_vattu_complete), Html.FROM_HTML_MODE_LEGACY)
                                        } else
                                            txt_status_vattu!!.text = Html.fromHtml(this@VatTuActivity.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY)
                                    } else
                                        txt_status_vattu!!.text = Html.fromHtml(this@VatTuActivity.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY)
                                } catch (e: Exception) {
                                    txt_status_vattu!!.text = Html.fromHtml(this@VatTuActivity.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY)
                                }
                            }

                        })
                hoSoVatTuSuCoAsyncInsert.execute(Constant.HoSoSuCoMethod.INSERT, mIDSuCoTT)
            }
            var check: Boolean
            do {
                check = ListObjectDB.instance.getLstHoSoVatTuSuCoInsert()!!.isNotEmpty()
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


