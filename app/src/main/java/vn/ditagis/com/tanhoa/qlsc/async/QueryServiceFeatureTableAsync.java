package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryServiceFeatureTableAsync extends AsyncTask<Void, Feature, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ArcGISFeature mSelectedArcGISFeature;
    @SuppressLint("StaticFieldLeak")
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private ServiceFeatureTable mServiceFeatureTable;

    public interface AsyncResponse {
        void processFinish(Feature output);
    }

    public QueryServiceFeatureTableAsync(Activity activity,
                                         ArcGISFeature selectedArcGISFeature, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mSelectedArcGISFeature = selectedArcGISFeature;
        this.mServiceFeatureTable = mApplication.getDFeatureLayer.getServiceFeatureTable();
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... aVoids) {
        try {
            String idSuCo = mSelectedArcGISFeature.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString();
            QueryParameters queryParameters = new QueryParameters();
            String queryClause = String.format("%s = '%s' or %s = '%s'",
                    Constant.FIELD_SUCOTHONGTIN.ID_SUCO, idSuCo,
                    Constant.FIELD_SUCOTHONGTIN.DON_VI, mApplication.getUserDangNhap.getRole());
            queryParameters.setWhereClause(queryClause);

            ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
            featureQueryResultListenableFuture.addDoneListener(() -> {
                try {
                    FeatureQueryResult result = featureQueryResultListenableFuture.get();
                    Iterator iterator = result.iterator();

                    if (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        publishProgress(feature);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    publishProgress();
                }
            });
        } catch (Exception e) {
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Feature... values) {
        if (values == null) {
            mDelegate.processFinish(null);
        } else if (values.length > 0) mDelegate.processFinish(values[0]);
    }


    @Override
    protected void onPostExecute(Void result) {


    }

}