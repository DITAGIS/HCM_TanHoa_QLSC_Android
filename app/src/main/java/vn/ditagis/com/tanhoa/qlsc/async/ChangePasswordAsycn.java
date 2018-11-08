package vn.ditagis.com.tanhoa.qlsc.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class ChangePasswordAsycn extends AsyncTask<String, Void, Boolean> {
    //    private Dialog mDialog;
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private Activity mActivity;

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    public ChangePasswordAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
        this.mApplication = (DApplication) activity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        LinearLayout layout = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_dialog, null);
//        TextView txtTitle = layout.findViewById(R.id.txt_progress_dialog_title);
//        TextView txtMessage = layout.findViewById(R.id.txt_progress_dialog_message);
//        txtTitle.setText("Vui lòng đợi");
//        txtMessage.setText(mActivity.getApplicationContext().getString(R.string.change_password_message));
//        this.mDialog = new Dialog(mActivity);
//        this.mDialog.setContentView(layout);
//        this.mDialog.setCancelable(false);
//        this.mDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Boolean doInBackground(String... params) {
        String pin = params[0];
        String newPin = params[1];
        try {
            String API_URL = Constant.URL_API.CHANGE_PASSWORD;
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod(Constant.HTTPRequest.POST_METHOD);

                JSONObject cred = new JSONObject();
                cred.put("OldPassword", pin);
                cred.put("NewPassword", newPin);


                conn.setRequestProperty(Constant.HTTPRequest.AUTHORIZATION, mApplication.getUserDangNhap().getToken());
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(cred.toString());
                wr.flush();

                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                if (builder.toString().contains("true"))
                    return true;
                return false;
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("Lỗi đổi mật khẩu", e.toString());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean value) {
//        if (khachHang != null) {
//        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }
}
