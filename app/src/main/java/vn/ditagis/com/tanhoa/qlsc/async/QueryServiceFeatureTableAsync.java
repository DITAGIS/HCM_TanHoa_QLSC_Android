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
                            //tạo sự cố thông tin nếu chưa có
//                        ServiceFeatureTable serviceFeatureTable = mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThonTin();
//                        serviceFeatureTable.loadAsync();
//                        serviceFeatureTable.addDoneLoadingListener(() -> {
//                            new GenerateIDSuCoByAPIAsycn(mActivity, output -> {
//                                if (output != null) {
//
//                                    Feature suCoThongTinFeature = serviceFeatureTable.createFeature();
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.ID_SUCO,
//                                            idSuCo);
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT,
//                                            output);
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI,
//                                            (short) 0);
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN,
//                                            mApplication.getUserDangNhap.getUserName());
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN,
//                                            Short.parseShort(mSelectedArcGISFeature.getAttributes().get(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN).toString()));
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.DIA_CHI,
//                                            mSelectedArcGISFeature.getAttributes().get(Constant.FIELD_SUCO.DIA_CHI).toString());
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.GHI_CHU,
//                                            mSelectedArcGISFeature.getAttributes().get(Constant.FIELD_SUCO.GHI_CHU).toString());
//                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                        Calendar c = Calendar.getInstance();
//                                        suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TG_CAP_NHAT,
//                                                c);
//                                    }
//                                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.DON_VI,
//                                            mApplication.getUserDangNhap.getRole());
//                                    serviceFeatureTable.addFeatureAsync(suCoThongTinFeature).addDoneListener(() -> {
//                                        ListenableFuture<List<FeatureEditResult>> listListenableFuture = serviceFeatureTable.applyEditsAsync();
//                                        listListenableFuture.addDoneListener(() -> {
//                                            try {
//                                                List<FeatureEditResult> featureEditResults = listListenableFuture.get();
//                                                if (featureEditResults.size() > 0) {
//                                                    publishProgress(suCoThongTinFeature);
//                                                }
//                                            } catch (InterruptedException | ExecutionException e) {
//                                                e.printStackTrace();
//                                                publishProgress();
//                                            }
//                                        });
//                                    });
//                                }
//                            }).execute(mApplication.getConstant.getGENERATE_ID_SUCOTHONGTIN(idSuCo));
//                        });
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