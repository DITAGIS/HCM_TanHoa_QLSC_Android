package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.VersionInfo;


public class CheckVersionAsycn extends AsyncTask<String, Void, VersionInfo> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private CheckVersionAsycn.AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(VersionInfo versionInfo);
    }

    public CheckVersionAsycn(Context context, CheckVersionAsycn.AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage("Đang kiểm tra phiên bản...");
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected VersionInfo doInBackground(String... params) {
        VersionInfo versionInfo = null;
        try {
            URL url = new URL(String.format(Constant.URL_API.CHECK_VERSION, "1.1.0"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    break;
                }
                bufferedReader.close();
                versionInfo = parse(stringBuilder.toString());
            } catch (Exception e1) {
                Log.e("Lỗi check version", e1.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);

        } finally {
            return versionInfo;
        }

    }

    private VersionInfo parse(String data) throws JSONException {
        if (data == null)
            return null;
        VersionInfo versionInfo = null;
        String myData = "{ \"version\": [".concat(data).concat("]}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("version");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            String versionCode = jsonRoute.getString("VersionCode");
            String type = jsonRoute.getString("Type");
            String link = jsonRoute.getString("Link");
            String date = jsonRoute.getString("Date");
            try {
                versionInfo = new VersionInfo(versionCode, type, link, new SimpleDateFormat("yyyy-MM-dd").parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return versionInfo;
    }

    @Override
    protected void onPostExecute(VersionInfo versionInfo) {
//        if (user != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(versionInfo);
//        }
    }

}
