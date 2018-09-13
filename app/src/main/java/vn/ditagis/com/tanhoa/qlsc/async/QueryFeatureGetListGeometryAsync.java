package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryFeatureGetListGeometryAsync extends AsyncTask<List<String>, List<Geometry>, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    @SuppressLint("StaticFieldLeak")
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private ServiceFeatureTable mServiceFeatureTable;
    private AlertDialog mDialog;

    public interface AsyncResponse {
        void processFinish(List<Geometry> output);
    }

    public QueryFeatureGetListGeometryAsync(Activity activity, ServiceFeatureTable serviceFeatureTable,
                                            AsyncResponse delegate) {
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LinearLayout layout = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
        TextView txtTitle = layout.findViewById(R.id.txt_progress_dialog_title);
        TextView txtMessage = layout.findViewById(R.id.txt_progress_dialog_message);
        txtTitle.setText(mActivity.getApplicationContext().getString(R.string.message_list_task_title));
        txtMessage.setText(mActivity.getApplicationContext().getString(R.string.message_list_task_message));
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(false);
        builder.setView(layout);

        mDialog = builder.create();
        mDialog.show();
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(mDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mDialog.getWindow().setAttributes(layoutParams);
        }
//        mDialog = new ProgressDialog(mActivity);
//        mDialog.setCancelable(false);
//        mDialog.setTitle(R.string.message_title_list_task);
//        mDialog.show();

    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(List<String>... lists) {
        try {
            if (lists != null && lists.length > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("%s in (", Constant.FIELD_SUCO.ID_SUCO));
                for (String idSuCo : lists[0]) {
                    builder.append(String.format("'%s' ,", idSuCo));
                }
                builder.append("'')");
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setWhereClause(builder.toString());

                ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                featureQueryResultListenableFuture.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = featureQueryResultListenableFuture.get();
                        Iterator<Feature> iterator = result.iterator();
                        Feature item;
                        List<Geometry> geometries = new ArrayList<>();
                        while (iterator.hasNext()) {
                            item = iterator.next();

                            geometries.add(item.getGeometry());
                        }
                        publishProgress(geometries);

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        publishProgress();
                    }
                });
            } else
                publishProgress();
        } catch (Exception e) {
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(List<Geometry>... values) {
        if (values == null) {
            mDelegate.processFinish(null);
        } else if (values.length > 0) mDelegate.processFinish(values[0]);
        else mDelegate.processFinish(null);

        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }


    @Override
    protected void onPostExecute(Void result) {


    }

}