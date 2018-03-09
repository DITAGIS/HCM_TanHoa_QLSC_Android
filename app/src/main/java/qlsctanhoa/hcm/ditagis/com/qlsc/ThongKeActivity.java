package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;

public class ThongKeActivity extends AppCompatActivity {
    private TextView txtTongSuCo, txtChuaSua, txtDangSua, txtDaSua;
    private QuanLySuCo mQuanLySuCo;
    private ServiceFeatureTable mServiceFeatureTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        this.txtTongSuCo = this.findViewById(R.id.txtTongSuCo);
        this.txtChuaSua = this.findViewById(R.id.txtChuaSua);
        this.txtDangSua = this.findViewById(R.id.txtDangSua);
        this.txtDaSua = this.findViewById(R.id.txtDaSua);
        thongKe();
    }

    public void thongKe() {
        final int[] tongloaitrangthai = getIntent().getIntArrayExtra(this.getString(R.string.tongloaitrangthai));
        txtTongSuCo.setText(tongloaitrangthai[0]+"");
        txtChuaSua.setText(tongloaitrangthai[1]+"");
        txtDangSua.setText(tongloaitrangthai[2]+"");
        txtDaSua.setText(tongloaitrangthai[3]+"");

    }
}
