package vn.ditagis.com.tanhoa.qlsc.fragment.list_task;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.Feature;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import vn.ditagis.com.tanhoa.qlsc.ListTaskActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableGetListAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

@SuppressLint("ValidFragment")
public class ListTaskFragment extends Fragment {
    private View mRootView;
    ListView mLstChuaXuLy;
    ListView mLstDangXuLy;
    ListView mLstHoanThanh;
    TextView mTxtChuaXuLy;
    TextView mTxtDangXuLy;
    TextView mTxtHoanThanh;
    private ListTaskActivity mActivity;
    private DApplication mApplication;
    TraCuuAdapter mAdapterChuaXuLy, mAdapterDangXuLy, mAdapterHoanThanh;

    @SuppressLint("ValidFragment")
    public ListTaskFragment(ListTaskActivity activity, LayoutInflater inflater) {
        this.mActivity = activity;
        mApplication = (DApplication) activity.getApplication();
        mRootView = inflater.inflate(R.layout.fragment_list_task_list, null);

        init();
    }

    private void init() {
        mLstChuaXuLy = mRootView.findViewById(R.id.lst_list_task_chua_xu_ly);
        mLstDangXuLy = mRootView.findViewById(R.id.lst_list_task_dang_xu_ly);
        mLstHoanThanh = mRootView.findViewById(R.id.lst_list_task_da_hoan_thanh);

        mTxtChuaXuLy = mRootView.findViewById(R.id.txt_list_task_chua_xu_ly);
        mTxtDangXuLy = mRootView.findViewById(R.id.txt_list_task_dang_xu_ly);
        mTxtHoanThanh = mRootView.findViewById(R.id.txt_list_task_hoan_thanh);
        mTxtChuaXuLy.setOnClickListener(this::onClick);
        mTxtDangXuLy.setOnClickListener(this::onClick);
        mTxtHoanThanh.setOnClickListener(this::onClick);

        mAdapterChuaXuLy = new TraCuuAdapter(mActivity.getApplicationContext(), new ArrayList<>());
        mAdapterDangXuLy = new TraCuuAdapter(mActivity.getApplicationContext(), new ArrayList<>());
        mAdapterHoanThanh = new TraCuuAdapter(mActivity.getApplicationContext(), new ArrayList<>());

        mLstChuaXuLy.setAdapter(mAdapterChuaXuLy);
        mLstDangXuLy.setAdapter(mAdapterDangXuLy);
        mLstHoanThanh.setAdapter(mAdapterHoanThanh);

        mLstChuaXuLy.setOnItemClickListener((adapterView, view, i, l) -> {
            mActivity.itemClick(adapterView, i);
        });
        mLstDangXuLy.setOnItemClickListener((adapterView, view, i, l) -> {
            mActivity.itemClick(adapterView, i);
        });
        mLstHoanThanh.setOnItemClickListener((adapterView, view, i, l) -> {
//            Toast.makeText(mActivity.getApplicationContext(), R.string.message_click_feature_complete,
//                    Toast.LENGTH_SHORT).show();
            mActivity.itemClick(adapterView, i);
        });
        new QueryServiceFeatureTableGetListAsync(mActivity, output -> {
            if (output != null && output.size() > 0) {
                handlingQuerySuccess(output);
            }
            mAdapterChuaXuLy.notifyDataSetChanged();
            mAdapterDangXuLy.notifyDataSetChanged();
            mAdapterHoanThanh.notifyDataSetChanged();

            mTxtChuaXuLy.setText(mActivity.getResources().getString(R.string.txt_list_task_chua_xu_ly, mAdapterChuaXuLy.getCount()));
            mTxtDangXuLy.setText(mActivity.getResources().getString(R.string.txt_list_task_dang_xu_ly, mAdapterDangXuLy.getCount()));
            mTxtHoanThanh.setText(mActivity.getResources().getString(R.string.txt_list_task_hoan_thanh, mAdapterHoanThanh.getCount()));
        }).execute();
    }


    private void handlingQuerySuccess(List<Feature> output) {
        try {
            List<TraCuuAdapter.Item> chuaXuLyList = new ArrayList<>();
            List<TraCuuAdapter.Item> dangXuLyList = new ArrayList<>();
            List<TraCuuAdapter.Item> hoanThanhList = new ArrayList<>();
            for (Feature feature : output) {
                Map<String, Object> attributes = feature.getAttributes();
                TraCuuAdapter.Item item = new TraCuuAdapter.Item(
                        Integer.parseInt(attributes.get(Constant.FIELD_SUCOTHONGTIN.OBJECT_ID).toString()),
                        attributes.get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
                        Integer.parseInt(attributes.get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString()),
                        Constant.DATE_FORMAT_VIEW.format(((Calendar) attributes.get(Constant.FIELD_SUCOTHONGTIN.TG_GIAO_VIEC)).getTime()),
                        attributes.get(Constant.FIELD_SUCOTHONGTIN.DIA_CHI).toString());
                Object value = feature.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI);
                if (value == null) {
                    chuaXuLyList.add(item);
                } else {
                    short trangThai = Short.parseShort(value.toString());
                    switch (trangThai) {
                        case Constant.TRANG_THAI_SU_CO.CHUA_XU_LY:
                            chuaXuLyList.add(item);
                            break;
                        case Constant.TRANG_THAI_SU_CO.DANG_XU_LY:
                            dangXuLyList.add(item);
                            break;
                        case Constant.TRANG_THAI_SU_CO.HOAN_THANH:
                            hoanThanhList.add(item);
                            break;
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
            chuaXuLyList.sort(comparator);
            dangXuLyList.sort(comparator);
            hoanThanhList.sort(comparator);
            mAdapterChuaXuLy.addAll(chuaXuLyList);
            mAdapterDangXuLy.addAll(dangXuLyList);
            mAdapterHoanThanh.addAll(hoanThanhList);
        } catch (Exception e) {
            Log.e("Lỗi lấy ds công việc", e.toString());
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_list_task_chua_xu_ly:
                if (mLstChuaXuLy.getVisibility() == View.VISIBLE)
                    mLstChuaXuLy.setVisibility(View.GONE);
                else mLstChuaXuLy.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_list_task_dang_xu_ly:
                if (mLstDangXuLy.getVisibility() == View.VISIBLE)
                    mLstDangXuLy.setVisibility(View.GONE);
                else mLstDangXuLy.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_list_task_hoan_thanh:
                if (mLstHoanThanh.getVisibility() == View.VISIBLE)
                    mLstHoanThanh.setVisibility(View.GONE);
                else mLstHoanThanh.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }
}
