package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.ConnectionDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.LoginDB;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.KhachHang;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.KhachHangDangNhap;

public class LoginAsycn extends AsyncTask<String, Void, KhachHang> {
    private ProgressDialog mDialog;
    private Context mContext;
    private String IMEI;
    private AsyncResponse mDelegate;
    private boolean mIsIMEI;

    public interface AsyncResponse {
        void processFinish(KhachHang output);
    }

    public LoginAsycn(Context context, boolean mIsIMEI, AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
        this.mIsIMEI = mIsIMEI;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.connect_message));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected KhachHang doInBackground(String... params) {
        String danhBo = params[0];
        String pin = params[1];
        String IMEI = "";
        if (params.length > 2)
            IMEI = params[2];
        try {
            ConnectionDB.getInstance().getConnection();
            publishProgress();
            LoginDB loginDB = new LoginDB(mContext);
            KhachHang khachHang;
            if (mIsIMEI)
                khachHang = loginDB.find(danhBo, pin, IMEI);
            else
                khachHang = loginDB.find(danhBo, pin);
            KhachHangDangNhap.getInstance().setKhachHang(khachHang);
            return khachHang;
        } catch (Exception e) {
            Log.e("Lỗi đăng nhập", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        this.mDialog.setMessage(mContext.getString(R.string.login_message));
    }

    @Override
    protected void onPostExecute(KhachHang khachHang) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(khachHang);
//        }
    }
}
