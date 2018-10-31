package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class ShowCaptureActivity extends AppCompatActivity {
    private DApplication mApplication;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_capture);
        mApplication = (DApplication) getApplication();


        byte[] capture = mApplication.capture;
        if (capture != null) {
            ImageView imageView = findViewById(R.id.show_capture_imageView);
            Bitmap decodeBitmap = BitmapFactory.decodeByteArray(capture, 0, capture.length);
            imageView.setImageBitmap(decodeBitmap);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_capture_cancel:
                goHomeCancel();
                break;
            case R.id.show_capture_ok:
                goHome();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goHomeCancel();
    }


    public void goHome() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void goHomeCancel() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
