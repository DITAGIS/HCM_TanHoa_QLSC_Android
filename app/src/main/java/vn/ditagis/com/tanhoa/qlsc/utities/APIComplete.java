package vn.ditagis.com.tanhoa.qlsc.utities;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

public class APIComplete {
    public static void send(DApplication mApplication, String idSuCo){
        try {
            String API_URL =String.format( mApplication.getConstant.API_COMPLETE, idSuCo);

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", mApplication.getUserDangNhap().getToken());
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("Lỗi lấy LayerInfo", e.toString());
        }
    }
}
