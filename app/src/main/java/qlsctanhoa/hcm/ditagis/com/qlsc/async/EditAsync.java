package qlsctanhoa.hcm.ditagis.com.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.FeatureViewMoreInfoAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, Void, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;

    public EditAsync(Context context, ServiceFeatureTable serviceFeatureTable, ArcGISFeature selectedArcGISFeature) {
        mContext = context;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Đang xử lý...");
        dialog.setCancelable(false);

        dialog.show();

    }

    @Override
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        FeatureViewMoreInfoAdapter adapter = params[0];
        for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
            if (item.getValue() == null)
                continue;
            switch (item.getFieldType()) {
                case DATE:
                    Date date = null;
                    try {
                        date = Constant.DATE_FORMAT.parse(item.getValue());
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), c);
                    } catch (ParseException e) {
                    }
                    break;

                case TEXT:
                    mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), item.getValue());
                    break;
                case SHORT:
                    mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                    break;
            }
        }

        mServiceFeatureTable.loadAsync();
        mServiceFeatureTable.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // update feature in the feature table
                    mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            mServiceFeatureTable.applyEditsAsync().addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                }
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

