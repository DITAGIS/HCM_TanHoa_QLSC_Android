package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.service_feature_table));

        setContentView(R.layout.activity_tra_cuu);
        List<TraCuuAdapter.Item> items = new ArrayList<>();
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 1, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 0, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 1, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 1, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 2, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));
        items.add(new TraCuuAdapter.Item("01_12_03_2018", 1, "12/03/2018", "327 Lê Văn Phương, Phường Tân Quy, Quận 7, Thành Phố Hồ Chí Minh"));

        this.mTraCuuAdapter = new TraCuuAdapter(TraCuuActivity.this, items);


        this.mListView = findViewById(R.id.lstView_TraCuu);
        this.mListView.setAdapter(this.mTraCuuAdapter);
        findViewById(R.id.btnTraCuu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 3);
                setResult(Activity.RESULT_OK, returnIntent);

            }


        });
        query();
    }

    public void showDateTimePicker(View view) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());

                String s = datePicker.getDayOfMonth() + "/" + datePicker.getMonth() + "/" + datePicker.getYear();
                Button editText = (Button) findViewById(R.id.editShowDate);
                editText.setText(s);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    public void query() {
        QueryParameters queryParameters = new QueryParameters();
        String dateFrom = "2018-3-7" + " 00:00:00";
//        queryParameters.setWhereClause("NgayCapNhat >= '" + "1520528400000" +  "'");
//        queryParameters.setWhereClause("TRANGTHAI = 0");
        queryParameters.setWhereClause("IDSuCo = '1_09_03_2018'");

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
                        List<TraCuuAdapter.Item> traCuuAdapters = new ArrayList<>();
                        String format_date = Constant.DATE_FORMAT.format(((Calendar) attributes.get(Constant.NGAY_CAP_NHAT)).getTime());
                        traCuuAdapters.add(new TraCuuAdapter.Item(attributes.get(Constant.IDSU_CO).toString(),
                                Integer.parseInt(attributes.get(Constant.TRANG_THAI).toString()),
                                format_date,
                                attributes.get(Constant.VI_TRI).toString()));
                        
                        mTraCuuAdapter = new TraCuuAdapter(TraCuuActivity.this, traCuuAdapters);


                        mListView = findViewById(R.id.lstView_TraCuu);
                        mListView.setAdapter(mTraCuuAdapter);

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 3);
        setResult(Activity.RESULT_OK, returnIntent);

        super.finish();
    }

}
