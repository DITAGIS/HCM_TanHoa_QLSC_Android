package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.DMADB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.ListFeatureLayerDTGDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.VatTuOngChinhDB;
import hcm.ditagis.com.tanhoa.qlsc.connectDB.VatTuOngNganhDB;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import hcm.ditagis.com.tanhoa.qlsc.utities.Preference;

public class PreparingAsycn extends AsyncTask<Void, Void, Void> {
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Void output);
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
    protected Void doInBackground(Void... params) {
        try {
            DMADB getListDMADB = new DMADB(mContext);
            ListObjectDB.getInstance().setDmas(getListDMADB.find());

            VatTuOngChinhDB getListVatTuOngChinhDB = new VatTuOngChinhDB(mContext);
            ListObjectDB.getInstance().setVatTuOngChinhs(getListVatTuOngChinhDB.find());

            VatTuOngNganhDB getListVatTuOngNganhDB = new VatTuOngNganhDB(mContext);
            ListObjectDB.getInstance().setVatTuOngNganhs(getListVatTuOngNganhDB.find());


            ListFeatureLayerDTGDB listFeatureLayerDTGDB = new ListFeatureLayerDTGDB(mContext);
            ListObjectDB.getInstance().setLstFeatureLayerDTG(listFeatureLayerDTGDB.find(Preference.getInstance().loadPreference(
                    mContext.getString(R.string.preference_username)
            )));
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }
}
