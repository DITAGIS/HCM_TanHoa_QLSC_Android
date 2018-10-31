package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class ThemSuCoActivity extends AppCompatActivity {
    @BindView(R.id.eTxtFullName_add_feature)
    EditText txtFullName;
    @BindView(R.id.etxtPhoneNumber_add_feature)
    EditText etxtPhoneNumber;
    @BindView(R.id.etxtEmail_add_feature)
    EditText etxtEmail;
    @BindView(R.id.etxtAddress_add_feature)
    EditText etxtAddress;
    @BindView(R.id.etxtSubAdmin_add_feature)
    EditText etxtSubAdmin;
    @BindView(R.id.etxtLocality_add_feature)
    EditText etxtLocality;
    @BindView(R.id.etxtNote_add_feature)
    EditText etxtNote;
    @BindView(R.id.spin_hinh_thuc_phat_hien_add_feature)
    Spinner spinHinhThucPhatHien;
    @BindView(R.id.img_add_feature)
    ImageView mImage;
    @BindView(R.id.spin_add_feature_ket_cau_duong)
    Spinner mSpinKetCauDuong;
    //    @BindView(R.id.etxt_them_su_co_phuidao_dai)
//    EditText mETxtPhuiDaoDai;
//    @BindView(R.id.etxt_them_su_co_phuidao_rong)
//    EditText mETxtPhuiDaoRong;
//    @BindView(R.id.etxt_them_su_co_phuidao_sau)
//    EditText mEtxtPhuiDaoSau;
    private DApplication mApplication;
    private Uri mUri;
    private List<CodedValue> mCodeValues, mCodeValuesKetCauDuong;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_su_co);
        mApplication = (DApplication) getApplication();
        ButterKnife.bind(this);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayShowHomeEnabled(true);
        initView();

        //for camera
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() {
        txtFullName.setText(mApplication.getUserDangNhap().getUserName());
        etxtAddress.setText(mApplication.getDiemSuCo.getVitri());
        etxtSubAdmin.setText(mApplication.getDiemSuCo.getQuan());
//        mETxtPhuiDaoDai.setOnClickListener(this::onClickTextView);
//        mETxtPhuiDaoRong.setOnClickListener(this::onClickTextView);
//        etxtLocality.setText(mApplication.getDiemSuCo.getPhuong());

        Domain domain = mApplication.getDFeatureLayer.getLayer().getFeatureTable().
                getField(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN).getDomain();
        mCodeValues = ((CodedValueDomain) domain).getCodedValues();
        if (mCodeValues != null) {
            List<String> codes = new ArrayList<>();
            for (CodedValue codedValue : mCodeValues)
                codes.add(codedValue.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, codes);
            spinHinhThucPhatHien.setAdapter(adapter);
            spinHinhThucPhatHien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mApplication.getUserDangNhap().getRole().toLowerCase().startsWith(Constant.Role.ROLE_PGN)) {
                        if (Constant.Another.HINH_THUC_PHAT_HIEN_BE_NGAM.toLowerCase().equals(adapter.getItem(i).toLowerCase())) {
                        } else {
                        }
                    } else {
                        Toast.makeText(ThemSuCoActivity.this, "Bạn không có quyền chọn hình thức phát hiện Bể ngầm!", Toast.LENGTH_LONG).show();
                        if (adapter.getCount() > 1)
                            spinHinhThucPhatHien.setSelection(1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        Domain domainKetCauDuong = mApplication.getDFeatureLayer.getLayer().getFeatureTable().
                getField(Constant.FIELD_SUCO.KET_CAU_DUONG).getDomain();
        mCodeValuesKetCauDuong = ((CodedValueDomain) domainKetCauDuong).getCodedValues();
        if (mCodeValuesKetCauDuong != null) {
            List<String> codesKetCauDuong = new ArrayList<>();
            for (CodedValue codedValue : mCodeValuesKetCauDuong)
                codesKetCauDuong.add(codedValue.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, codesKetCauDuong);
            mSpinKetCauDuong.setAdapter(adapter);
        }
    }

    private boolean isNotEmpty() {
        return !txtFullName.getText().toString().trim().isEmpty() &&
//                !etxtPhoneNumber.getText().toString().trim().isEmpty() &&
                !etxtAddress.getText().toString().trim().isEmpty();
    }

    private void handlingEmpty() {
        Toast.makeText(this, "Thiếu thông tin!!!", Toast.LENGTH_SHORT).show();
    }

    public void capture() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(cameraIntent, Constant.RequestCode.REQUEST_CODE_CAPTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickTextView(View view) {
        switch (view.getId()) {
            case R.id.txt_add_feature_add_feature:
                if (isNotEmpty()) {
                    mApplication.getDiemSuCo.setNguoiPhanAnh(txtFullName.getText().toString().trim());
                    mApplication.getDiemSuCo.setSdtPhanAnh(etxtPhoneNumber.getText().toString().trim());
                    mApplication.getDiemSuCo.setEmailPhanAnh(etxtEmail.getText().toString().trim());
                    mApplication.getDiemSuCo.setVitri(etxtAddress.getText().toString().trim());
                    mApplication.getDiemSuCo.setQuan(etxtSubAdmin.getText().toString().trim());
                    mApplication.getDiemSuCo.setPhuong(etxtLocality.getText().toString().trim());
                    mApplication.getDiemSuCo.setGhiChu(etxtNote.getText().toString().trim());
//                    if (!mETxtPhuiDaoDai.getText().toString().isEmpty())
//                        mApplication.getDiemSuCo.setPhuiDaoDai(Double.parseDouble(mETxtPhuiDaoDai.getText().toString()));
//                    if (!mETxtPhuiDaoRong.getText().toString().isEmpty())
//                        mApplication.getDiemSuCo.setPhuiDaoRong(Double.parseDouble(mETxtPhuiDaoRong.getText().toString()));
//                    if (!mEtxtPhuiDaoSau.getText().toString().isEmpty())
//                        mApplication.getDiemSuCo.setPhuiDaoSau(Double.parseDouble(mEtxtPhuiDaoSau.getText().toString()));
                    for (CodedValue codedValue : mCodeValues) {
                        if (codedValue.getName().equals(spinHinhThucPhatHien.getSelectedItem().toString()))
                            mApplication.getDiemSuCo.setHinhThucPhatHien(Short.parseShort(codedValue.getCode().toString()));
                    }
                    for (CodedValue codedValueKetCauDuong : mCodeValuesKetCauDuong) {
                        if (codedValueKetCauDuong.getName().equals(mSpinKetCauDuong.getSelectedItem().toString()))
                            mApplication.getDiemSuCo.setKetCauDuong(Short.parseShort(codedValueKetCauDuong.getCode().toString()));
                    }
                    finish();
                } else {
                    handlingEmpty();
                }
                break;
        }
    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.btnAttachemnt_add_feature:
                capture();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.RequestCode.REQUEST_CODE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    if (mApplication.capture != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(mApplication.capture, 0, mApplication.capture.length);
                        try {
                            if (bitmap != null) {
                                mImage.setImageBitmap(bitmap);
                                mApplication.getDiemSuCo.setImage(mApplication.capture);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Hủy chụp ảnh", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        goHome();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goHome() {
        mApplication.getDiemSuCo.setPoint(null);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
