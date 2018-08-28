package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class SingleTapAddFeatureAsync extends AsyncTask<Void, Feature, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    @SuppressLint("StaticFieldLeak")
    private AsyncResponse mDelegate;
    private DApplication mApplication;

    public interface AsyncResponse {
        void processFinish(Feature output);
    }

    public SingleTapAddFeatureAsync(Activity activity,
                                    ServiceFeatureTable serviceFeatureTable, AsyncResponse delegate) {
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mDialog = new ProgressDialog(activity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... aVoids) {
        final Feature feature;
        try {
            feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(mApplication.getDiemSuCo.getPoint());
            feature.getAttributes().put(Constant.FIELD_SUCO.DIA_CHI, mApplication.getDiemSuCo.getVitri());
            feature.getAttributes().put(Constant.FIELD_SUCO.QUAN, mApplication.getDiemSuCo.getQuan());
            feature.getAttributes().put(Constant.FIELD_SUCO.PHUONG, mApplication.getDiemSuCo.getPhuong());
            feature.getAttributes().put(Constant.FIELD_SUCO.GHI_CHU, mApplication.getDiemSuCo.getGhiChu());
            feature.getAttributes().put(Constant.FIELD_SUCO.NGUOI_PHAN_ANH, mApplication.getDiemSuCo.getNguoiPhanAnh());
            feature.getAttributes().put(Constant.FIELD_SUCO.SDT, mApplication.getDiemSuCo.getSdtPhanAnh());
            feature.getAttributes().put(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN, mApplication.getDiemSuCo.getHinhThucPhatHien());
            addFeatureAsync(feature);

        } catch (Exception e) {
            publishProgress();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateString() {
//        String timeStamp = Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat writeDate = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return writeDate.format(Calendar.getInstance().getTime());
    }

    private String getTimeID() {
        return Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    private void addFeatureAsync(Feature feature) {
        new GenerateIDSuCoByAPIAsycn(mActivity, output -> {
            if (output.isEmpty()) {
                publishProgress();
                return;
            }
            feature.getAttributes().put(Constant.FIELD_SUCO.ID_SUCO, output);
            Short intObj = (short) 1;
            feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, intObj);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Calendar c = Calendar.getInstance();
                feature.getAttributes().put(Constant.FIELD_SUCO.TGPHAN_ANH, c);
            }

            String finalOutput = output;
            mServiceFeatureTable.addFeatureAsync(feature).addDoneListener(() -> {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mServiceFeatureTable.applyEditsAsync();
                listListenableEditAsync.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                        if (featureEditResults.size() > 0) {
                            long objectId = featureEditResults.get(0).getObjectId();
                            final QueryParameters queryParameters = new QueryParameters();
//                            final String query = String.format(mActivity.getString(R.string.arcgis_query_by_OBJECTID), objectId);
                            final String query = mActivity.getString(R.string.arcgis_query_by_IDSuCo, finalOutput);
                            queryParameters.setWhereClause(query);
                            final ListenableFuture<FeatureQueryResult> featuresAsync = mServiceFeatureTable
                                    .queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.IDS_ONLY);
                            featuresAsync.addDoneListener(() -> {
                                try {
                                    FeatureQueryResult result = featuresAsync.get();
                                    if (result.iterator().hasNext()) {
                                        Feature item = result.iterator().next();
                                        ArcGISFeature arcGISFeature = (ArcGISFeature) item;
                                        addAttachment(arcGISFeature, feature);
                                        publishProgress(item);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    publishProgress();
                                }

                            });
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        publishProgress();
                        e.printStackTrace();
                    }

                });
            });
        }).execute();
    }

    private void addAttachment(ArcGISFeature arcGISFeature, final Feature feature) {
        final String attachmentName = mApplication.getApplicationContext().getString(R.string.attachment) + "_" + System.currentTimeMillis() + ".png";
        final ListenableFuture<Attachment> addResult = arcGISFeature.addAttachmentAsync(
                mApplication.getDiemSuCo.getImage(), Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            try {
                Attachment attachment = addResult.get();
                if (attachment.getSize() > 0) {
                    final ListenableFuture<Void> tableResult = mServiceFeatureTable.updateFeatureAsync(arcGISFeature);
                    tableResult.addDoneListener(() -> {
                        final ListenableFuture<List<FeatureEditResult>> updatedServerResult = mServiceFeatureTable.applyEditsAsync();
                        updatedServerResult.addDoneListener(() -> {
                            List<FeatureEditResult> edits;
                            try {
                                edits = updatedServerResult.get();
                                if (edits.size() > 0) {
                                    if (!edits.get(0).hasCompletedWithErrors()) {
                                        publishProgress(feature);
                                    }
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            } finally {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            }

                        });
                    });
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onProgressUpdate(Feature... values) {
        if (values == null) {
            Toast.makeText(mActivity.getApplicationContext(), "Không phản ánh được sự cố. Vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            mDelegate.processFinish(null);
        } else if (values.length > 0) mDelegate.processFinish(values[0]);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    protected void onPostExecute(Void result) {


    }

}