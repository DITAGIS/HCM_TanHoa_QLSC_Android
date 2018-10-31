package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

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

public class QueryServiceFeatureTableAsync extends AsyncTask<QueryParameters, Feature, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    @SuppressLint("StaticFieldLeak")
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private ServiceFeatureTable mServiceFeatureTable;

    public interface AsyncResponse {
        void processFinish(Feature output);
    }

    public QueryServiceFeatureTableAsync(Activity activity,
                                          ServiceFeatureTable serviceFeatureTable, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(QueryParameters... params) {
        try {
            if (params != null && params.length > 0) {


                ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(params[0], ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                featureQueryResultListenableFuture.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = featureQueryResultListenableFuture.get();
                        Iterator iterator = result.iterator();

                        if (iterator.hasNext()) {
                            Feature feature = (Feature) iterator.next();
                            publishProgress(feature);
                        } else {
                            publishProgress();
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        publishProgress();
                    }
                });
            } else publishProgress();
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
        else mDelegate.processFinish(null);
    }


    @Override
    protected void onPostExecute(Void result) {


    }

}