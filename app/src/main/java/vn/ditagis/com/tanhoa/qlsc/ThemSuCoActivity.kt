package vn.ditagis.com.tanhoa.qlsc

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain

import java.util.ArrayList
import java.util.Objects

import kotlinx.android.synthetic.main.activity_them_su_co.*
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class ThemSuCoActivity : AppCompatActivity() {
    private var mApplication: DApplication? = null
    private var mCodeValues: List<CodedValue>? = null
    private var mCodeValuesKetCauDuong: List<CodedValue>? = null

    private//                !etxtPhoneNumber_add_feature.getText().toString().trim().isEmpty() &&
    val isNotEmpty: Boolean
        get() = !eTxtFullName_add_feature!!.text.toString().trim { it <= ' ' }.isEmpty() && !etxtAddress_add_feature!!.text.toString().trim { it <= ' ' }.isEmpty()


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_them_su_co)
        mApplication = application as DApplication
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowHomeEnabled(true)
        initView()

        //for camera
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun initView() {
        eTxtFullName_add_feature!!.setText(mApplication!!.userDangNhap!!.userName)
        etxtAddress_add_feature!!.setText(mApplication!!.getDiemSuCo.vitri)
        etxtSubAdmin_add_feature!!.setText(mApplication!!.getDiemSuCo.quan)
        //        mETxtPhuiDaoDai.setOnClickListener(this::onClickTextView);
        //        mETxtPhuiDaoRong.setOnClickListener(this::onClickTextView);
        //        etxtLocality_add_feature.setText(mApplication.getDiemSuCo.getPhuong());

        val domain = mApplication!!.getDFeatureLayer.layer!!.featureTable.getField(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN).domain
        mCodeValues = (domain as CodedValueDomain).codedValues
        if (mCodeValues != null) {
            val codes = ArrayList<String>()
            for (codedValue in mCodeValues!!)
                codes.add(codedValue.name)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, codes)
            spin_hinh_thuc_phat_hien_add_feature!!.adapter = adapter
            spin_hinh_thuc_phat_hien_add_feature!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (mApplication!!.userDangNhap!!.role!!.toLowerCase().startsWith(Constant.Role.ROLE_PGN)) {
                        if (Constant.Another.HINH_THUC_PHAT_HIEN_BE_NGAM.toLowerCase() == adapter.getItem(i)!!.toLowerCase()) {
                        } else {
                        }
                    } else {
                        Toast.makeText(this@ThemSuCoActivity, "Bạn không có quyền chọn hình thức phát hiện Bể ngầm!", Toast.LENGTH_LONG).show()
                        if (adapter.count > 1)
                            spin_hinh_thuc_phat_hien_add_feature!!.setSelection(1)
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {

                }
            }
        }

        val domainKetCauDuong = mApplication!!.getDFeatureLayer.layer!!.featureTable.getField(Constant.FIELD_SUCO.KET_CAU_DUONG).domain
        mCodeValuesKetCauDuong = (domainKetCauDuong as CodedValueDomain).codedValues
        if (mCodeValuesKetCauDuong != null) {
            val codesKetCauDuong = ArrayList<String>()
            for (codedValue in mCodeValuesKetCauDuong!!)
                codesKetCauDuong.add(codedValue.name)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, codesKetCauDuong)
            spin_add_feature_ket_cau_duong!!.adapter = adapter
        }
    }

    private fun handlingEmpty() {
        Toast.makeText(this, "Thiếu thông tin!!!", Toast.LENGTH_SHORT).show()
    }

    fun capture() {
        val cameraIntent = Intent(this, CameraActivity::class.java)
        startActivityForResult(cameraIntent, Constant.RequestCode.REQUEST_CODE_CAPTURE)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun onClickTextView(view: View) {
        when (view.id) {
            R.id.txt_add_feature_add_feature -> if (isNotEmpty) {
                mApplication!!.getDiemSuCo.nguoiPhanAnh = eTxtFullName_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.sdtPhanAnh = etxtPhoneNumber_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.emailPhanAnh = etxtEmail_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.vitri = etxtAddress_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.quan = etxtSubAdmin_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.phuong = etxtLocality_add_feature!!.text.toString().trim { it <= ' ' }
                mApplication!!.getDiemSuCo.ghiChu = etxtNote_add_feature!!.text.toString().trim { it <= ' ' }
                //                    if (!mETxtPhuiDaoDai.getText().toString().isEmpty())
                //                        mApplication.getDiemSuCo.setPhuiDaoDai(Double.parseDouble(mETxtPhuiDaoDai.getText().toString()));
                //                    if (!mETxtPhuiDaoRong.getText().toString().isEmpty())
                //                        mApplication.getDiemSuCo.setPhuiDaoRong(Double.parseDouble(mETxtPhuiDaoRong.getText().toString()));
                //                    if (!mEtxtPhuiDaoSau.getText().toString().isEmpty())
                //                        mApplication.getDiemSuCo.setPhuiDaoSau(Double.parseDouble(mEtxtPhuiDaoSau.getText().toString()));
                for (codedValue in mCodeValues!!) {
                    if (codedValue.name == spin_hinh_thuc_phat_hien_add_feature!!.selectedItem.toString())
                        mApplication!!.getDiemSuCo.hinhThucPhatHien = java.lang.Short.parseShort(codedValue.code.toString())
                }
                for (codedValueKetCauDuong in mCodeValuesKetCauDuong!!) {
                    if (codedValueKetCauDuong.name == spin_add_feature_ket_cau_duong!!.selectedItem.toString())
                        mApplication!!.getDiemSuCo.ketCauDuong = java.lang.Short.parseShort(codedValueKetCauDuong.code.toString())
                }
                finish()
            } else {
                handlingEmpty()
            }
        }
    }

    fun onClickButton(view: View) {
        when (view.id) {
            R.id.btnAttachemnt_add_feature -> capture()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            Constant.RequestCode.REQUEST_CODE_CAPTURE -> if (resultCode == RESULT_OK) {
                if (mApplication!!.capture != null) {
                    val bitmap = BitmapFactory.decodeByteArray(mApplication!!.capture, 0, mApplication!!.capture!!.size)
                    try {
                        if (bitmap != null) {
                            img_add_feature!!.setImageBitmap(bitmap)
                            mApplication!!.getDiemSuCo.image = mApplication!!.capture
                        }
                    } catch (ignored: Exception) {
                    }

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Hủy chụp ảnh", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBackPressed() {
        goHome()
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    fun goHome() {
        mApplication!!.getDiemSuCo.point = null
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

}
