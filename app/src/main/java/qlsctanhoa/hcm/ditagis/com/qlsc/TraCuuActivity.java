package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.TraCuuAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Constant;

public class TraCuuActivity extends AppCompatActivity {
    private ServiceFeatureTable mServiceFeatureTable;
    private TraCuuAdapter mTraCuuAdapter;
    private ListView mListView;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.service_feature_table));

        setContentView(R.layout.activity_tra_cuu);
        this.mListView = findViewById(R.id.lstView_TraCuu);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.ket_qua_objectid), ((TraCuuAdapter.Item) parent.getItemAtPosition(position)).getObjectID());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        List<TraCuuAdapter.Item> items = new ArrayList<>();
        mTraCuuAdapter = new TraCuuAdapter(this, items);
        this.mListView.setAdapter(this.mTraCuuAdapter);
        findViewById(R.id.btnTraCuu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTraCuuAdapter.clear();
                mTraCuuAdapter.notifyDataSetChanged();
                Parameter paras = new Parameter();
                paras.setDate(datePicker);
                EditText diachi = (EditText) findViewById(R.id.edit_dia_chi);
                EditText nguoicapnhat = (EditText) findViewById(R.id.edit_nguoi_sua_chua);
                Spinner quan = (Spinner) findViewById(R.id.spin_district);
                Spinner loai = (Spinner) findViewById(R.id.spin_phanloaisuco);
                int selectedItemId = (int) quan.getSelectedItemId();
                paras.setDiaChi(diachi.getText().toString());
                paras.setNguoicapnhat(nguoicapnhat.getText().toString());
                paras.setQuanHuyen(Constant.CODEID_DISTRICT[(int) quan.getSelectedItemId()]);
                paras.setPhanloaisuco(Constant.CODE_PHANLOAI[(int) loai.getSelectedItemId()]);
                query(paras);

            }


        });

    }

    public void showDateTimePicker(View view) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                String s = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                Button editText = (Button) findViewById(R.id.editShowDate);
                editText.setText(s);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    public void query(Parameter parameter) {

        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();

        if (parameter.getQuanHuyen() != null) {
            builder.append(parameter.getQuanHuyen());
            builder.append(" and ");
        }
        if (parameter.getDate() != null) {
            builder.append(parameter.getDate());
            builder.append(" and ");
        }
        if (parameter.getDiaChi() != null) {
            builder.append(parameter.getDiaChi());
//            builder.append(" and ");
        }
        if (parameter.getNguoicapnhat() != null) {
            builder.append(parameter.getNguoicapnhat());
            builder.append(" and ");
        }
//        String dateFrom = "2018-3-7" + " 00:00:00";
//        queryParameters.setWhereClause("NgayCapNhat <= '" + dateFrom +  "'");
//        queryParameters.setWhereClause("ViTri like '%ngân hàng%'");
//        queryParameters.setWhereClause("TRANGTHAI = 0");
//        queryParameters.setWhereClause("IDSuCo like '%09_03_2018%'");
//        queryParameters.setWhereClause("MAQUAN = '768'");
        if (parameter.getPhanloaisuco() != null) {
            builder.append(parameter.getPhanloaisuco());
            builder.append(" and ");
        }
        if (!builder.toString().isEmpty()) builder.append(" 1 = 1 ");
        queryParameters.setWhereClause(builder.toString());
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {

                    FeatureQueryResult result = feature.get();
                    Iterator iterator = result.iterator();

                    while (iterator.hasNext()) {
                        Feature item = (Feature) iterator.next();

                        Map<String, Object> attributes = item.getAttributes();
                        String format_date = "", viTri = "";
                        try{
                            viTri =  attributes.get(Constant.VI_TRI).toString();
                        }catch (Exception e){

                        }
                        if ((Calendar) attributes.get(Constant.NGAY_CAP_NHAT) != null)
                            format_date = Constant.DATE_FORMAT.format(((Calendar) attributes.get(Constant.NGAY_CAP_NHAT)).getTime());

                        mTraCuuAdapter.add(new TraCuuAdapter.Item(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()), attributes.get(Constant.IDSU_CO).toString(), Integer.parseInt(attributes.get(Constant.TRANG_THAI).toString()), format_date,viTri));
                        mTraCuuAdapter.notifyDataSetChanged();

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class Parameter {
        String diaChi;
        String quanHuyen;
        String date;
        String nguoicapnhat;
        String phanloaisuco;

        public Parameter() {
        }

        public Parameter(String diaChi, String quanHuyen, String date, String nguoicapnhat, String phanloaisuco) {
            this.diaChi = diaChi;
            this.quanHuyen = quanHuyen;
            this.date = date;
            this.nguoicapnhat = nguoicapnhat;
            this.phanloaisuco = phanloaisuco;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            if (diaChi != null && !diaChi.isEmpty()) {
                this.diaChi = "ViTri like '%" + diaChi + "%'";
            }

        }

        public String getQuanHuyen() {
            return quanHuyen;
        }

        public void setQuanHuyen(String quanHuyen) {
            if (quanHuyen != null) this.quanHuyen = "MaQuan = " + quanHuyen;
        }

        public String getDate() {
            return date;
        }

        public void setDate(DatePicker datePicker) {
            if (datePicker != null) {
                Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                String dateTime = Constant.DATE_FORMAT.format(calendar.getTime());
                this.date = Constant.IDSU_CO + " like '%" + dateTime + "'";
            }
//                this.date = "NgayCapNhat >= '" + datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth() + "'";
        }

        public String getNguoicapnhat() {
            return nguoicapnhat;
        }

        public void setNguoicapnhat(String nguoicapnhat) {
            if (nguoicapnhat != null && !nguoicapnhat.isEmpty()) {
                this.nguoicapnhat = "NGUOICAPNHAT like '%" + nguoicapnhat + "%'";
            }
        }

        public String getPhanloaisuco() {
            return phanloaisuco;
        }

        public void setPhanloaisuco(String phanloaisuco) {
            if (phanloaisuco != null) {
                this.phanloaisuco = "PhanLoaiSuCo = " + phanloaisuco;
            }
        }
    }


}