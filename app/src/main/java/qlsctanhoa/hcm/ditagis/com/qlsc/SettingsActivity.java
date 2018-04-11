package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Constant;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Preference;

public class SettingsActivity extends AppCompatActivity {
    private ListView mLstViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mLstViewSettings = findViewById(R.id.lstView_Settings);
        mLstViewSettings.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.SETTINGS_CATEGORY));
        mLstViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showPhuongThucThemDiemSuCo();
                        break;
                    case 1:
                        break;
                }
            }
        });
    }

    private void showPhuongThucThemDiemSuCo() {
        Preference.getInstance().setContext(SettingsActivity.this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setCancelable(true);
        builder.setTitle("Phương thức thêm điểm sự cố");
        builder.setPositiveButton("THOÁT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_settings_phuong_thuc_them_diem_su_co, null);
        final RadioGroup group = (RadioGroup) layout.findViewById(R.id.rdgr_layout_settings);
        final String key = SettingsActivity.this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co);
        String type_Add_Point = Preference.getInstance().loadPreference(key);
        if (type_Add_Point.equals("") || type_Add_Point.equals(this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_cham_diem)))
            group.check(R.id.rd_layout_settings_cham_diem);
        else if (type_Add_Point.equals(this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_toa_do)))
            group.check(R.id.rd_layout_settings_toa_do);
        else if (type_Add_Point.equals(this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_keo_tha)))
            group.check(R.id.rd_layout_settings_keo_tha);

//        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Preference.getInstance().deletePreferences(key);
                switch (checkedId) {
                    case R.id.rd_layout_settings_cham_diem:
                        Preference.getInstance().savePreferences(key,
                                SettingsActivity.this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_cham_diem));
                        break;
                    case R.id.rd_layout_settings_toa_do:
                        Preference.getInstance().savePreferences(key,
                                SettingsActivity.this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_toa_do));
                        break;
                    case R.id.rd_layout_settings_keo_tha:
                        Preference.getInstance().savePreferences(key,
                                SettingsActivity.this.getResources().getString(R.string.preference_settings_phuong_thuc_them_diem_su_co_keo_tha));
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog.setView(layout);
        dialog.show();
    }
}
