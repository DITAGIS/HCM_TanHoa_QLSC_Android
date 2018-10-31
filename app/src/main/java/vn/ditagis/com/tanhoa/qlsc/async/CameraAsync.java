package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;


/**
 * Created by ThanLe on 4/16/2018.
 */

public class CameraAsync extends AsyncTask<byte[], Void, byte[]> {
    @SuppressLint("StaticFieldLeak")
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private Dialog mDialog;

    public interface AsyncResponse {
        void processFinish(byte[] output);
    }

    public CameraAsync(Activity activity, AsyncResponse delegate) {
        this.mDelegate = delegate;
        mDialog = new Dialog(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_progress_dialog, null);
        TextView txtTitle = layout.findViewById(R.id.txt_progress_dialog_title);
        TextView txtMessage = layout.findViewById(R.id.txt_progress_dialog_message);
        txtTitle.setText("Vui lòng đợi");
        txtMessage.setText("Đang giảm chất lượng hình ảnh...");
        mDialog.setContentView(layout);
        mDialog.show();
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

    @SuppressLint("DefaultLocale")
    @Override
    protected byte[] doInBackground(byte[]... params) {
        if (params != null && params.length > 0) {
            return handlingCapture(params[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(byte[] result) {
        mDialog.dismiss();

        mDelegate.processFinish(result);
    }
}