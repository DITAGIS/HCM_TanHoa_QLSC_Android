package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.Feature;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableGetListAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class ListTaskActivity extends AppCompatActivity {
    @BindView(R.id.lst_list_task_chua_xu_ly)
    ListView mLstChuaXuLy;
    @BindView(R.id.lst_list_task_dang_xu_ly)
    ListView mLstDangXuLy;
    @BindView(R.id.lst_list_task_da_hoan_thanh)
    ListView mLstHoanThanh;
    @BindView(R.id.txt_list_task_chua_xu_ly)
    TextView mTxtChuaXuLy;
    @BindView(R.id.txt_list_task_dang_xu_ly)
    TextView mTxtDangXuLy;
    @BindView(R.id.txt_list_task_hoan_thanh)
    TextView mTxtHoanThanh;

    TraCuuAdapter mAdapterChuaXuLy, mAdapterDangXuLy, mAdapterHoanThanh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mAdapterChuaXuLy = new TraCuuAdapter(this, new ArrayList<>());
        mAdapterDangXuLy = new TraCuuAdapter(this, new ArrayList<>());
        mAdapterHoanThanh = new TraCuuAdapter(this, new ArrayList<>());

        mLstChuaXuLy.setAdapter(mAdapterChuaXuLy);
        mLstDangXuLy.setAdapter(mAdapterDangXuLy);
        mLstHoanThanh.setAdapter(mAdapterHoanThanh);

        mLstChuaXuLy.setOnItemClickListener((adapterView, view, i, l) -> {
            itemClick(adapterView, i);
        });
        mLstDangXuLy.setOnItemClickListener((adapterView, view, i, l) -> {
            itemClick(adapterView, i);
        });
        mLstHoanThanh.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(ListTaskActivity.this, R.string.message_click_feature_complete,
                    Toast.LENGTH_SHORT).show();

        });
        new QueryServiceFeatureTableGetListAsync(this, output -> {
            if (output != null && output.size() > 0) {
                handlingQuerySuccess(output);
            }
            mAdapterChuaXuLy.notifyDataSetChanged();
            mAdapterDangXuLy.notifyDataSetChanged();
            mAdapterHoanThanh.notifyDataSetChanged();

            mTxtChuaXuLy.setText(ListTaskActivity.this.getString(R.string.txt_list_task_chua_xu_ly, mAdapterChuaXuLy.getCount()));
            mTxtDangXuLy.setText(ListTaskActivity.this.getString(R.string.txt_list_task_dang_xu_ly, mAdapterDangXuLy.getCount()));
            mTxtHoanThanh.setText(ListTaskActivity.this.getString(R.string.txt_list_task_hoan_thanh, mAdapterHoanThanh.getCount()));
        }).execute();
    }

    private void handlingQuerySuccess(List<Feature> output) {
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
                mAdapterChuaXuLy.add(item);
            } else {
                short trangThai = Short.parseShort(value.toString());
                switch (trangThai) {
                    case Constant.TRANG_THAI_SU_CO.CHUA_XU_LY:
                        mAdapterChuaXuLy.add(item);
                        break;
                    case Constant.TRANG_THAI_SU_CO.DANG_XU_LY:
                        mAdapterDangXuLy.add(item);
                        break;
                    case Constant.TRANG_THAI_SU_CO.HOAN_THANH:
                        mAdapterHoanThanh.add(item);
                        break;
                }
            }
        }
    }

    private void itemClick(AdapterView<?> adapter, int position) {
        TraCuuAdapter.Item item = (TraCuuAdapter.Item) adapter.getItemAtPosition(position);
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_dialog, null);
        TextView txtTitle = layout.findViewById(R.id.txt_dialog_title);
        TextView txtMessage = layout.findViewById(R.id.txt_dialog_message);
        txtTitle.setText(getString(R.string.message_title_confirm));
        txtMessage.setText(getString(R.string.message_click_list_task, item.getId()));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setCancelable(false)
                .setPositiveButton(R.string.message_btn_ok, (dialog, i) -> {
                    ((DApplication) ListTaskActivity.this.getApplication()).getDiemSuCo.setIdSuCo(item.getId());
                    goHome();
                }).setNegativeButton(R.string.message_btn_cancel, (dialog, i) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                goHome();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
