package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

public class ThongKeActivity extends AppCompatActivity {
    private TextView txtTongSuCo, txtChuaSua, txtDangSua, txtDaSua;
    private TextView  txtPhanTramChuaSua, txtPhanTramDangSua, txtPhanTramDaSua;
    private QuanLySuCo mQuanLySuCo;
    private ServiceFeatureTable mServiceFeatureTable;
    private PieChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);
        this.txtTongSuCo = this.findViewById(R.id.txtTongSuCo);
        this.txtChuaSua = this.findViewById(R.id.txtChuaSua);
        this.txtDangSua = this.findViewById(R.id.txtDangSua);
        this.txtDaSua = this.findViewById(R.id.txtDaSua);
        this.txtPhanTramChuaSua = this.findViewById(R.id.txtPhanTramChuaSua);
        this.txtPhanTramDangSua = this.findViewById(R.id.txtPhanTramDangSua);
        this.txtPhanTramDaSua = this.findViewById(R.id.txtPhanTramDaSua);
        thongKe();
    }

    public void thongKe() {
        final int[] tongloaitrangthai = getIntent().getIntArrayExtra(this.getString(R.string.tongloaitrangthai));
        txtTongSuCo.setText("Tổng các sự cố: " +tongloaitrangthai[0]);
        txtChuaSua.setText(tongloaitrangthai[1]+"");
        txtDangSua.setText(tongloaitrangthai[2]+"");
        txtDaSua.setText(tongloaitrangthai[3]+"");
        txtPhanTramChuaSua.setText((tongloaitrangthai[1]*100)/tongloaitrangthai[0]+"%");
        txtPhanTramDangSua.setText((tongloaitrangthai[2]*100)/tongloaitrangthai[0]+"%");
        txtPhanTramDaSua.setText((tongloaitrangthai[3]*100)/tongloaitrangthai[0]+"%");
        mChart = (PieChart) findViewById(R.id.piechart);
        mChart = configureChart(mChart);
        mChart = setData(mChart,tongloaitrangthai);
        mChart.animateXY(1500, 1500);
    }
    public PieChart configureChart(PieChart chart) {
        chart.setHoleColor(getResources().getColor(android.R.color.background_dark));
        chart.setHoleRadius(60f);
        chart.setDescription("");
        chart.setTransparentCircleRadius(5f);
        chart.setDrawCenterText(true);
        chart.setDrawHoleEnabled(false);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        chart.setUsePercentValues(false);

        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        return chart;
    }

    private PieChart setData(PieChart chart,int[] tongloaitrangthai) {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        yVals1.add(new Entry(tongloaitrangthai[1], 0));
        yVals1.add(new Entry(tongloaitrangthai[2], 1));
        yVals1.add(new Entry(tongloaitrangthai[3], 2));
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("Chưa sửa chữa");
        xVals.add("Đang sửa chữa");
        xVals.add("Đã sửa chữa");
        PieDataSet set1 = new PieDataSet(yVals1, "");
        set1.setSliceSpace(0f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        set1.setColors(colors);
        PieData data = new PieData(xVals, set1);
        data.setValueTextSize(15);
        set1.setValueTextSize(0);
        chart.setData(data);
        chart.highlightValues(null);
//        chart.invalidate();
        return chart;
    }

}
