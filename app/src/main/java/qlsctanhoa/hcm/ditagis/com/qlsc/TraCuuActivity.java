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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.TraCuuAdapter;

public class TraCuuActivity extends AppCompatActivity {

    private TraCuuAdapter mTraCuuAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 3);
        setResult(Activity.RESULT_OK, returnIntent);

        super.finish();
    }

}
