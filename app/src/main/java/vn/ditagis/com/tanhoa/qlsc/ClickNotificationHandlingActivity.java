package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class ClickNotificationHandlingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_notification_handling);

        ((DApplication) getApplication()).setFromNotification(true);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
