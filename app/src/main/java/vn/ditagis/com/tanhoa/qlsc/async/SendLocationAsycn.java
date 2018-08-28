package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.QuanLySuCo;
import vn.ditagis.com.tanhoa.qlsc.R;

public class SendLocationAsycn extends AsyncTask<Integer, Void, String> {
    private Context mContext;

    public interface AsyncResponse {
        void processFinish(ArcGISFeature output);
    }


    public SendLocationAsycn(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {
        int seconds = params[0];
        try { Thread.sleep(2000); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return "OK";
    }

    @Override
    protected void onProgressUpdate(Void... arcGISFeatures) {
        super.onProgressUpdate(arcGISFeatures);
    }

}
