package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import vn.ditagis.com.tanhoa.qlsc.adapter.ThietBiAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.HoSoThietBiSuCoAsync;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.ThietBi;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import vn.ditagis.com.tanhoa.qlsc.utities.APICompleteAsync;
import vn.ditagis.com.tanhoa.qlsc.utities.MySnackBar;

public class ThietBiActivity extends AppCompatActivity {
    @BindView(R.id.autoCompleteTV_thietbi)
    AutoCompleteTextView mAutoCompleteTV;
    @BindView(R.id.lstview_thietbi)
    ListView mLstView;
    @BindView(R.id.etxt_thietbi_thoigian)
    EditText mEtxtSoLuong;
    @BindView(R.id.txt_thietbi_add)
    TextView mTxtThem;
    @BindView(R.id.btn_thietbi_update)
    Button mBtnUpdate;
    @BindView(R.id.txt_thietbi_status)
    TextView mTxtStatus;
    @BindView(R.id.llayout_thietbi_add)
    LinearLayout mLLayoutAdd;
    @BindView(R.id.llayout_thietbi_update)
    LinearLayout mLLayoutUpdate;

    private DApplication mApplication;
    private String mIDSuCoTT;
    private ThietBiAdapter mAdapter;
    private boolean mIsComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thiet_bi);

        ButterKnife.bind(this);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        (Objects.requireNonNull(getSupportActionBar())).setDisplayShowHomeEnabled(true);
        mApplication = (DApplication) getApplication();
        mIsComplete = Short.parseShort(mApplication.getArcGISFeature().getAttributes().
                get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString()) == Constant.TRANG_THAI_SU_CO.HOAN_THANH;
        mIDSuCoTT = mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT).toString();
//        QueryServiceFeatureTableAsync queryServiceFeatureTableAsync = new QueryServiceFeatureTableAsync(
//                this, mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin(), output -> {
            init();
            loadThietBi();
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

    private void init() {
        List<String> listTenThietBis = new ArrayList<>();
        try {
            for (ThietBi thietBi : ListObjectDB.getInstance().getThietBis())
                listTenThietBis.add(thietBi.getTenThietBi());
        } catch (Exception ignored) {

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listTenThietBis);
        mAutoCompleteTV.setAdapter(adapter);

        mAdapter = new ThietBiAdapter(this, new ArrayList<>());
        final String[] maThietBi = {""};
        mLstView.setAdapter(mAdapter);
//        if (!mIsComplete) {
        mLstView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final ThietBiAdapter.Item itemThietBi = (ThietBiAdapter.Item) adapterView.getAdapter().getItem(i);
            final AlertDialog.Builder builder = new AlertDialog.Builder(ThietBiActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle("Xóa thiết bị");
            builder.setMessage("Bạn có chắc muốn xóa thiết bị " + itemThietBi.getTenThietBi());
            builder.setCancelable(false).setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()).setPositiveButton("Xóa", (dialogInterface, i12) -> {
                mAdapter.remove(itemThietBi);
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
                String tenThietBi = editable.toString();
                for (ThietBi thietBi : ListObjectDB.getInstance().getThietBis()) {
                    if (thietBi.getTenThietBi().equals(tenThietBi)) {
                        maThietBi[0] = thietBi.getMaThietBi();
                        break;
                    }
                }

            }
        });
        mTxtThem.setOnClickListener(view -> {
            if (mEtxtSoLuong.getText().toString().trim().length() == 0)
                MySnackBar.make(mEtxtSoLuong, ThietBiActivity.this.getString(R.string.message_soluong_themhoso), true);
            else {
                try {
                    double soLuong = Double.parseDouble(mEtxtSoLuong.getText().toString());
                    mAdapter.add(new ThietBiAdapter.Item(mAutoCompleteTV.getText().toString(),
                            soLuong, maThietBi[0]));
                    mAdapter.notifyDataSetChanged();

                    mAutoCompleteTV.setText("");
                    mEtxtSoLuong.setText("");

                    if (mLstView.getHeight() > 500) {
                        ViewGroup.LayoutParams params = mLstView.getLayoutParams();
                        params.height = 500;
                        mLstView.setLayoutParams(params);
                    }
                } catch (NumberFormatException e) {
                    MySnackBar.make(mEtxtSoLuong, ThietBiActivity.this.getString(R.string.message_number_format_exception), true);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
        mBtnUpdate.setOnClickListener(view -> {
            updateEdit();
        });
//        }
    }

    private void loadThietBi() {
        mTxtStatus.setText(Html.fromHtml(getString(R.string.info_thietbi_loading), Html.FROM_HTML_MODE_LEGACY));

        HoSoThietBiSuCoAsync hoSoThietBiSuCoAsync = new HoSoThietBiSuCoAsync(this, object -> {
            if (object != null) {
                try {
                    List<HoSoThietBiSuCo> hoSoThietBiSuCos = (List<HoSoThietBiSuCo>) object;
                    for (HoSoThietBiSuCo hoSoThietBiSuCo : hoSoThietBiSuCos) {
                        mAdapter.add(new ThietBiAdapter.Item(hoSoThietBiSuCo.getTenThietBi(), hoSoThietBiSuCo.getThoigianVanHanh(),
                                hoSoThietBiSuCo.getMaThietBi()));
                    }
                } catch (Exception e) {
                    Log.e("Lỗi ép kiểu thiết bị", e.toString());
                }
                mAdapter.notifyDataSetChanged();
                mLstView.setAdapter(mAdapter);
            }
            mTxtStatus.setText("");
        });
        hoSoThietBiSuCoAsync.execute(Constant.HOSOSUCO_METHOD.FIND, mIDSuCoTT);

    }

    private void updateEdit() {
        if (mLstView.getAdapter() != null && mLstView.getAdapter().getCount() == 0) {
            MySnackBar.make(mLstView, getString(R.string.message_CapNhat_ThietBi), true);
        } else {
            ThietBiAdapter thietBiAdapter = (ThietBiAdapter) mLstView.getAdapter();
            List<HoSoThietBiSuCo> hoSoThietBiSuCos = new ArrayList<>();
            for (ThietBiAdapter.Item itemThietBi : thietBiAdapter.getItems()) {
                hoSoThietBiSuCos.add(new HoSoThietBiSuCo(mIDSuCoTT,
                        itemThietBi.getSoLuong(), itemThietBi.getMaThietBi(), itemThietBi.getTenThietBi()));
            }
            ListObjectDB.getInstance().setLstHoSoThietBiSuCoInsert(hoSoThietBiSuCos);
            if (ListObjectDB.getInstance().getLstHoSoThietBiSuCoInsert().size() > 0) {
                HoSoThietBiSuCoAsync hoSoThietBiSuCoInsertAsync = new HoSoThietBiSuCoAsync(this, object -> {
                    try {
                        if (object != null) {
                            boolean isDone = (boolean) object;
//                            Object[] a = (Object[]) object;
//                            boolean isDone = (boolean) a[0];
                            if (isDone) {
                                if (mIsComplete)
                                    new APICompleteAsync(mApplication, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString())
                                            .execute();
                                goHome();
                                mTxtStatus.setText(Html.fromHtml(ThietBiActivity.this.getString(R.string.info_thietbi_complete), Html.FROM_HTML_MODE_LEGACY));
                            } else
                                mTxtStatus.setText(Html.fromHtml(ThietBiActivity.this.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY));
                        } else
                            mTxtStatus.setText(Html.fromHtml(ThietBiActivity.this.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY));
                    } catch (Exception e) {
                        mTxtStatus.setText(Html.fromHtml(ThietBiActivity.this.getString(R.string.info_thietbi_fail), Html.FROM_HTML_MODE_LEGACY));
                    }
                });
                hoSoThietBiSuCoInsertAsync.execute(Constant.HOSOSUCO_METHOD.INSERT, mIDSuCoTT);
            }
            boolean check;
            do {
                check = ListObjectDB.getInstance().getLstHoSoThietBiSuCoInsert().size() > 0;
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
