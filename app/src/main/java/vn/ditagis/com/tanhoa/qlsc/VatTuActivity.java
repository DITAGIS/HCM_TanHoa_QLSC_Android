package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.QueryParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.adapter.VatTuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.HoSoVatTuSuCoAsync;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import vn.ditagis.com.tanhoa.qlsc.utities.MySnackBar;

public class VatTuActivity extends AppCompatActivity {

    @BindView(R.id.autoCompleteTV_vattu)
    AutoCompleteTextView mAutoCompleteTV;
    @BindView(R.id.lstview_vattu)
    ListView mLstView;
    @BindView(R.id.etxt_soLuong_vattu)
    EditText mEtxtSoLuong;
    @BindView(R.id.txt_donvitinh_vattu)
    TextView mTxtDonViTinh;
    @BindView(R.id.txt_them_vattu)
    TextView mTxtThem;
    @BindView(R.id.btn_update_vattu)
    Button mBtnUpdate;
    @BindView(R.id.txt_status_vattu)
    TextView mTxtStatus;
    @BindView(R.id.llayout_vattu_add)
    LinearLayout mLLayoutAdd;
    @BindView(R.id.llayout_vattu_update)
    LinearLayout mLLayoutUpdate;

    private DApplication mApplication;
    private String mIDSuCoTT;
    private VatTuAdapter mAdapter;
    private String mIDSuCo;
    private boolean mIsComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vat_tu);
        ButterKnife.bind(this);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayShowHomeEnabled(true);
        mApplication = (DApplication) getApplication();
        mIsComplete = Short.parseShort(mApplication.getArcGISFeature().getAttributes().
                get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString()) == Constant.TRANG_THAI_SU_CO.HOAN_THANH;
        switch (mApplication.getLoaiVatTu()) {
            case Constant.CODE_VATTU_CAPMOI:
                setTitle("Vật tư cấp mới");
                break;
            case Constant.CODE_VATTU_THUHOI:
                setTitle("Vật tư thu hồi");
                break;
        }
        mIDSuCo = mApplication.getDiemSuCo.getIdSuCo();
        QueryServiceFeatureTableAsync queryServiceFeatureTableAsync = new QueryServiceFeatureTableAsync(
                this, mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThonTin(), output -> {
            init();
            loadVatTu();
            mIDSuCoTT = output.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT).toString();
        });

        String queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(queryClause);
        queryServiceFeatureTableAsync.execute(queryParameters);

        if (mIsComplete) {
            mLLayoutAdd.setVisibility(View.GONE);
            mLLayoutUpdate.setVisibility(View.GONE);
        }
    }

    private void loadVatTu() {
        mTxtStatus.setText(Html.fromHtml(getString(R.string.info_vattu_loading), Html.FROM_HTML_MODE_LEGACY));

        HoSoVatTuSuCoAsync hoSoVatTuSuCoAsync = new HoSoVatTuSuCoAsync(this, object -> {
            if (object != null) {
                try {
                    List<HoSoVatTuSuCo> hoSoVatTuSuCoList = (List<HoSoVatTuSuCo>) object;
                    for (HoSoVatTuSuCo hoSoVatTuSuCo : hoSoVatTuSuCoList) {
                        mAdapter.add(new VatTuAdapter.Item(hoSoVatTuSuCo.getTenVatTu(), hoSoVatTuSuCo.getSoLuong(),
                                hoSoVatTuSuCo.getDonViTinh(), hoSoVatTuSuCo.getMaVatTu()));
                    }
                } catch (Exception e) {
                    Log.e("Lỗi ép kiểu vật tư", e.toString());
                }
                mAdapter.notifyDataSetChanged();
                mLstView.setAdapter(mAdapter);
            }
            mTxtStatus.setText("");
        });
        hoSoVatTuSuCoAsync.execute(Constant.HOSOVATTUSUCO_METHOD.FIND, mIDSuCo);

    }


    private void init() {
        List<String> listTenVatTus = new ArrayList<>();
        try {
            for (VatTu vatTu : ListObjectDB.getInstance().getVatTus())
                listTenVatTus.add(vatTu.getTenVatTu());
        } catch (Exception ignored) {

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listTenVatTus);
        mAutoCompleteTV.setAdapter(adapter);

        mAdapter = new VatTuAdapter(this, new ArrayList<>());
        final String[] maVatTu = {""};
        mLstView.setAdapter(mAdapter);
        if (!mIsComplete) {
            mLstView.setOnItemLongClickListener((adapterView, view, i, l) -> {
                final VatTuAdapter.Item itemVatTu = (VatTuAdapter.Item) adapterView.getAdapter().getItem(i);
                final AlertDialog.Builder builder = new AlertDialog.Builder(VatTuActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Xóa vật tư");
                builder.setMessage("Bạn có chắc muốn xóa vật tư " + itemVatTu.getTenVatTu());
                builder.setCancelable(false).setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()).setPositiveButton("Xóa", (dialogInterface, i12) -> {
                    mAdapter.remove(itemVatTu);
                    mAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                return false;
            });
            mAutoCompleteTV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String tenVatTu = editable.toString();
                    for (VatTu vatTu : ListObjectDB.getInstance().getVatTus()) {
                        if (vatTu.getTenVatTu().equals(tenVatTu)) {
                            mTxtDonViTinh.setText(vatTu.getDonViTinh());
                            maVatTu[0] = vatTu.getMaVatTu();
                            break;
                        }
                    }

                }
            });
            mTxtThem.setOnClickListener(view -> {
                if (mEtxtSoLuong.getText().toString().trim().length() == 0)
                    MySnackBar.make(mEtxtSoLuong, VatTuActivity.this.getString(R.string.message_soluong_themvattu), true);
                else {
                    try {
                        double soLuong = Double.parseDouble(mEtxtSoLuong.getText().toString());
                        mAdapter.add(new VatTuAdapter.Item(mAutoCompleteTV.getText().toString(),
                                soLuong, mTxtDonViTinh.getText().toString(), maVatTu[0]));
                        mAdapter.notifyDataSetChanged();

                        mAutoCompleteTV.setText("");
                        mEtxtSoLuong.setText("");
                        mTxtDonViTinh.setText("");

                        if (mLstView.getHeight() > 500) {
                            ViewGroup.LayoutParams params = mLstView.getLayoutParams();
                            params.height = 500;
                            mLstView.setLayoutParams(params);
                        }
                    } catch (NumberFormatException e) {
                        MySnackBar.make(mEtxtSoLuong, VatTuActivity.this.getString(R.string.message_number_format_exception), true);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
            mBtnUpdate.setOnClickListener(view -> {
                updateEdit();
            });
        }
    }

    private void updateEdit() {
        if (mLstView.getAdapter() != null && mLstView.getAdapter().getCount() == 0) {
            MySnackBar.make(mLstView, getString(R.string.message_CapNhat_VatTu), true);
        } else {
            VatTuAdapter vatTuAdapter = (VatTuAdapter) mLstView.getAdapter();
            List<HoSoVatTuSuCo> hoSoVatTuSuCos = new ArrayList<>();
            for (VatTuAdapter.Item itemVatTu : vatTuAdapter.getItems()) {
                hoSoVatTuSuCos.add(new HoSoVatTuSuCo(mIDSuCoTT,
                        itemVatTu.getSoLuong(), itemVatTu.getMaVatTu(), itemVatTu.getTenVatTu(), itemVatTu.getDonVi()));
            }
            ListObjectDB.getInstance().setLstHoSoVatTuSuCoInsert(hoSoVatTuSuCos);
            if (ListObjectDB.getInstance().getLstHoSoVatTuSuCoInsert().size() > 0) {
                HoSoVatTuSuCoAsync hoSoVatTuSuCoAsyncInsert = new HoSoVatTuSuCoAsync(this, object -> {
                    try {
                        if (object != null) {
                            boolean isDone = (boolean) object;
//                            Object[] a = (Object[]) object;
//                            boolean isDone = (boolean) a[0];
                            if (isDone) {
                                goHome();
                                mTxtStatus.setText(Html.fromHtml(VatTuActivity.this.getString(R.string.info_vattu_complete), Html.FROM_HTML_MODE_LEGACY));
                            } else
                                mTxtStatus.setText(Html.fromHtml(VatTuActivity.this.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY));
                        } else
                            mTxtStatus.setText(Html.fromHtml(VatTuActivity.this.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY));
                    } catch (Exception e) {
                        mTxtStatus.setText(Html.fromHtml(VatTuActivity.this.getString(R.string.info_vattu_fail), Html.FROM_HTML_MODE_LEGACY));
                    }
                });
                hoSoVatTuSuCoAsyncInsert.execute(Constant.HOSOVATTUSUCO_METHOD.INSERT, mIDSuCo);
            }
            boolean check;
            do {
                check = ListObjectDB.getInstance().getLstHoSoVatTuSuCoInsert().size() > 0;
            }
            while (check);
        }
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void goHome() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}


