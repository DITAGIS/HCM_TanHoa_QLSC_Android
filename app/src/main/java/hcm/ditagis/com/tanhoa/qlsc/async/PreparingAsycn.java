package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListDMADB;

public class PreparingAsycn extends AsyncTask<Void, Void, List<String>> {
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(List<String> output);
    }

    public PreparingAsycn(Context context, AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.preparing));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        List<String> lstDMA = new ArrayList<>();
        try {
            GetListDMADB getListDMADB = new GetListDMADB(mContext);
            lstDMA = getListDMADB.find();
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return lstDMA;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(List<String> value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }
}
