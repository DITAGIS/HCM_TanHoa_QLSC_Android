package vn.ditagis.com.tanhoa.qlsc.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import vn.ditagis.com.tanhoa.qlsc.services.GetDMA;
import vn.ditagis.com.tanhoa.qlsc.services.GetThietBi;
import vn.ditagis.com.tanhoa.qlsc.services.GetVatTu;

public class PreparingByAPIAsycn extends AsyncTask<Void, Boolean, Void> {
    private ProgressDialog mDialog;
    private Activity mActivity;
    private DApplication mApplication;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {

        void processFinish();
    }

    public PreparingByAPIAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
        mApplication = (DApplication) activity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mActivity.getApplicationContext().getString(R.string.preparing));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            getLayerInfoAPI();
            new GetVatTu(mActivity.getApplicationContext(), () -> {
                new GetDMA(mActivity.getApplicationContext(), () -> {
                    new GetThietBi(mActivity.getApplicationContext(), () -> {
                        publishProgress(true);

                    }).execute();
                }).execute();

            }).execute();
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (values != null && values.length > 0 && values[0] && mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            this.mDelegate.processFinish();
        }

    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {

//        }
    }

    private void getLayerInfoAPI() {
        try {
            String API_URL = mApplication.getConstant.LAYER_INFO;

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
                pajsonRouteeJSon(builder.toString());
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("Lỗi lấy LayerInfo", e.toString());
        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data == null)
            return;
        String myData = "{ \"layerInfo\": ".concat(data).concat("}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("layerInfo");
        List<DLayerInfo> layerDTGS = new ArrayList<>();
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            String url = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_url));
            if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/SuCo/FeatureServer")) {
                url = url.replace("TanHoa/SuCo", "TanHoa/THSuCo");
            } else if (url.startsWith("http://113.161.88.180:800/arcgis/rest/services/TanHoa/TanHoaSuCo/FeatureServer")) {
                url = url.replace("TanHoa/TanHoaSuCo", "TanHoa/THSuCo");

            }
            String definition = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_definition));
            if (definition.contains("null"))
                definition = null;
            String addFields = "";
            try {
                addFields = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_add_fields_arr));

            } catch (Exception ignored) {

            }
            String outFields = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_out_fields_arr));
            String noOutFields = "";
            String id = jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_id));
            if (id.equals(Constant.ID_SU_CO_THONG_TIN_TABLE))
                noOutFields = noOutFields.concat(",").concat(Constant.NO_OUTFIELD_SUCO.DON_VI);
            layerDTGS.add(new DLayerInfo(id,
                    jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_title)),
                    url,
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_iscreate)), jsonRoute.getBoolean(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_isdelete)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isedit)), jsonRoute.getBoolean(mActivity.getApplicationContext().getString(R.string.sql_coloumn_sys_isview)),
                    definition,
                    outFields,
                    noOutFields,
                    addFields,
                    jsonRoute.getString(mActivity.getApplicationContext().getString(R.string.sql_column_sys_update_fields_arr))));


        }
        ListObjectDB.getInstance().setLstFeatureLayerDTG(layerDTGS);

    }

}
