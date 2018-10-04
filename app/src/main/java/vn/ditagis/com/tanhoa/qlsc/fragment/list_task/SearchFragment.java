package vn.ditagis.com.tanhoa.qlsc.fragment.list_task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.Feature;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import vn.ditagis.com.tanhoa.qlsc.ListTaskActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.QueryFeatureAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;


@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment {
    private View mRootView;
    private ListTaskActivity mActivity;
    private EditText mEtxtAddress;
    private Spinner mSpinTrangThai;
    private TextView mTxtThoiGian;
    private Button mBtnSearch;
    private ListView mLstKetQua;
    private LinearLayout mLayoutKetQua;

    private DApplication mApplication;
    private List<CodedValue> mCodeValues;
    private List<Feature> mFeaturesResult;

    @SuppressLint("ValidFragment")
    public SearchFragment(ListTaskActivity activity, final LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.fragment_list_task_search, null);
        this.mActivity = activity;
        mApplication = (DApplication) activity.getApplication();
        init();
    }

    private void init() {
        mEtxtAddress = mRootView.findViewById(R.id.etxt_list_task_search_address);
        mSpinTrangThai = mRootView.findViewById(R.id.spin_list_task_search_trang_thai);
        mTxtThoiGian = mRootView.findViewById(R.id.txt_list_task_search_thoi_gian);
        mBtnSearch = mRootView.findViewById(R.id.btn_list_task_search);
        mLstKetQua = mRootView.findViewById(R.id.lst_list_task_search);
        mLayoutKetQua = mRootView.findViewById(R.id.llayout_list_task_search_ket_qua);

        mBtnSearch.setOnClickListener(this::onClick);
        mTxtThoiGian.setOnClickListener(this::onClick);
        initSpinTrangThai();
        initListViewKetQuaTraCuu();
    }

    private void initSpinTrangThai() {
        Domain domain = mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin().getField(Constant.FIELD_SUCO.TRANG_THAI).getDomain();
        if (domain != null) {
            mCodeValues = ((CodedValueDomain) domain).getCodedValues();
            if (mCodeValues != null) {
                List<String> codes = new ArrayList<>();
                codes.add("Tất cả");
                for (CodedValue codedValue : mCodeValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mRootView.getContext(), android.R.layout.simple_list_item_1, codes);
                mSpinTrangThai.setAdapter(adapter);
            }
        }
    }

    private void initListViewKetQuaTraCuu() {
        mLstKetQua.setOnItemClickListener((adapterView, view, i, l) -> {
            mActivity.itemClick(adapterView, i);
        });
    }

    private void showDateTimePicker() {
        final View dialogView = View.inflate(mRootView.getContext(), R.layout.date_time_picker, null);
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mRootView.getContext()).create();
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {
            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            String displaytime = (String) DateFormat.format((Constant.DATE_FORMAT_STRING), calendar.getTime());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatGmt = Constant.DATE_FORMAT_YEAR_FIRST;
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            mTxtThoiGian.setText(displaytime);
            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    private void traCuu() {
//        if (!mTxtThoiGian.getText().toString().equals(mRootView.getContext().getString(R.string.txt_chon_thoi_gian_tracuusuco))) {
            mLayoutKetQua.setVisibility(View.GONE);
            short trangThai = -1;
            for (CodedValue codedValue : mCodeValues) {
                if (codedValue.getName().equals(mSpinTrangThai.getSelectedItem().toString())) {
                    trangThai = Short.parseShort(codedValue.getCode().toString());
                }
            }
            new QueryFeatureAsync(mActivity, trangThai,
                    mEtxtAddress.getText().toString(),
                    mTxtThoiGian.getText().toString(), output -> {
                if (output != null && output.size() > 0) {
                    mFeaturesResult = output;
                    handlingTraCuuHoanTat();
                }
            }).execute();
//        } else
//            Toast.makeText(mRootView.getContext(), "Vui lòng chọn thời gian", Toast.LENGTH_SHORT).show();
    }

    private void handlingTraCuuHoanTat() {
        List<TraCuuAdapter.Item> items = new ArrayList<>();
        for (Feature feature : mFeaturesResult) {
            Map<String, Object> attributes = feature.getAttributes();
            for (CodedValue codedValue : mCodeValues) {
                if (Short.parseShort(codedValue.getCode().toString()) ==
                        Short.parseShort(attributes.get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString())) {
                    items.add(new TraCuuAdapter.Item(   Integer.parseInt(attributes.get(Constant.FIELD_SUCOTHONGTIN.OBJECT_ID).toString()),
                            attributes.get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
                            Integer.parseInt(attributes.get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString()),
                            Constant.DATE_FORMAT_VIEW.format(((Calendar) attributes.get(Constant.FIELD_SUCOTHONGTIN.TG_GIAO_VIEC)).getTime()),
                            attributes.get(Constant.FIELD_SUCOTHONGTIN.DIA_CHI).toString()));
                }
            }

        }
        Comparator<TraCuuAdapter.Item> comparator = (TraCuuAdapter.Item o1, TraCuuAdapter.Item o2) -> {
            try {
                long i = Constant.DATE_FORMAT_VIEW.parse(o2.getNgayGiaoViec()).getTime() -
                        Constant.DATE_FORMAT_VIEW.parse(o1.getNgayGiaoViec()).getTime();
                if (i > 0)
                    return 1;
                else if (i == 0)
                    return 0;
                else return -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        };
        items.sort(comparator);
        TraCuuAdapter adapter = new TraCuuAdapter(mRootView.getContext(), items);
        mLstKetQua.setAdapter(adapter);
        mLayoutKetQua.setVisibility(View.VISIBLE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_list_task_search:
                traCuu();
                break;
            case R.id.txt_list_task_search_thoi_gian:
                showDateTimePicker();
                break;
        }
    }
}
