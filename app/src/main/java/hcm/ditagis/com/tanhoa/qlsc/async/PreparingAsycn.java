package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListDMADB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListNguyenNhanOngChinhDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListNguyenNhanOngNganhDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListVatLieuOngChinhDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.GetListVatLieuOngNganhDB;

public class PreparingAsycn extends AsyncTask<Void, Void, List<Object>> {
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(List<Object> output);
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
    protected List<Object> doInBackground(Void... params) {
        List<Object> lst = new ArrayList<>();
        try {
            GetListDMADB getListDMADB = new GetListDMADB(mContext);
            lst.add(getListDMADB.find());

            GetListNguyenNhanOngChinhDB getListNguyenNhanOngChinhDB = new GetListNguyenNhanOngChinhDB(mContext);
            lst.add(getListNguyenNhanOngChinhDB.find());

            GetListNguyenNhanOngNganhDB getListNguyenNhanOngNganhDB = new GetListNguyenNhanOngNganhDB(mContext);
            lst.add(getListNguyenNhanOngNganhDB.find());

            GetListVatLieuOngChinhDB getListVatLieuOngChinhDB = new GetListVatLieuOngChinhDB(mContext);
            lst.add(getListVatLieuOngChinhDB.find());

            GetListVatLieuOngNganhDB getListVatLieuOngNganhDB = new GetListVatLieuOngNganhDB(mContext);
            lst.add(getListVatLieuOngNganhDB.find());
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return lst;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(List<Object> value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }
}
