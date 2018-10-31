package vn.ditagis.com.tanhoa.qlsc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    @BindView(R.id.camera_flash)
    ImageView mImgFlash;

    private Camera mCamera;
    private Camera.PictureCallback mPictureCallback;
    private Camera.Parameters mParameters;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private DApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        mSurfaceView = findViewById(R.id.surfaceView_fragment_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mApplication = (DApplication) getApplication();
    }

    private void getParameters() {
        mCamera = Camera.open();
        mParameters = mCamera.getParameters();
        mCamera.setDisplayOrientation(90);
        mParameters.setPreviewFrameRate(30);
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

//        Camera.Size bestSize = null;
//        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedJpegThumbnailSizes();
//        bestSize = sizeList.get(0);
//        for (Camera.Size size : sizeList) {
//            if ((size.width * size.height) > (bestSize.width * bestSize.height)) {
//                bestSize = size;
//            }
//        }
//        mParameters.setPreviewSize(bestSize.width, bestSize.height);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            getParameters();
            mCamera.setParameters(mParameters);
            mSurfaceHolder = surfaceHolder;
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mPictureCallback = (bytes, camera) -> {
                try {
                    mApplication.capture = handlingCapture(bytes);

                    Intent intent = new Intent(CameraActivity.this, ShowCaptureActivity.class);
                    CameraActivity.this.startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_SHOW_CAPTURE);
                } catch (Exception e) {
                    Toast.makeText(CameraActivity.this, "Có lỗi khi chụp ảnh", Toast.LENGTH_SHORT).show();
                }
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] handlingCapture(byte[] bytes) {
        Bitmap decodeBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        final int IMAGE_MAX_SIZE = 1000000; // 1.0MP
        int height = decodeBitmap.getHeight();
        int width = decodeBitmap.getWidth();
        double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
        double x = (y / height) * width;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(decodeBitmap, (int) x, (int) y, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //rotate
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        bitmap.recycle();
        scaledBitmap.recycle();
        return outputStream.toByteArray();
    }

    private void captureImage() {
        mCamera.takePicture(null, null, mPictureCallback);
    }
    private void turnOnOffFlashCamera() throws IOException {
        //auto
        if (mCamera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
            mImgFlash.setImageResource(R.drawable.ic_flash_auto_white_64dp);
            mCamera = Camera.open();
            getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(mParameters);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
        //turn on
        else if (mCamera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_AUTO)) {
            mImgFlash.setImageResource(R.drawable.ic_flash_on_white_64dp);
            mCamera = Camera.open();
            getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
        //turn off
        else if (mCamera.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            mImgFlash.setImageResource(R.drawable.ic_flash_off_white_64dp);
            mCamera = Camera.open();
            getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_flash:
                try {
                    turnOnOffFlashCamera();
                } catch (Exception e) {
                    Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.camera_back:
                goHomeCancel();
                break;
            case R.id.camera_capture:
                captureImage();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.RequestCode.REQUEST_CODE_SHOW_CAPTURE:
                if (resultCode == RESULT_OK)
                    goHome();
                break;
        }
    }
}
