package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.Calendar;
import java.util.List;
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
            addFeature(feature);

        } catch (Exception e) {
            publishProgress();
        }
        return null;
    }

    private void addFeature(Feature feature) {
        new GenerateIDSuCoByAPIAsycn(mActivity, output -> {
            if (output.isEmpty()) {
                publishProgress();
                return;
            }
            feature.getAttributes().put(Constant.FIELD_SUCO.ID_SUCO, output);
            Short intObj = (short) 0;
            feature.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI, intObj);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Calendar c = Calendar.getInstance();
                feature.getAttributes().put(Constant.FIELD_SUCO.TGPHAN_ANH, c);
            }

            mServiceFeatureTable.addFeatureAsync(feature).addDoneListener(() -> {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mServiceFeatureTable.applyEditsAsync();
                listListenableEditAsync.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                        if (featureEditResults.size() > 0) {
                            addServiceFeatureTable((ArcGISFeature) feature, feature);
                        } else publishProgress();
                    } catch (InterruptedException | ExecutionException e) {
                        publishProgress();
                        e.printStackTrace();
                    }

                });
            });
        }).execute(mApplication.getConstant.GENERATE_ID_SUCO);
    }

    private void addServiceFeatureTable(ArcGISFeature arcGISFeature, Feature feature) {
        ServiceFeatureTable serviceFeatureTable = mApplication.getDFeatureLayer.getServiceFeatureTable();
        serviceFeatureTable.loadAsync();
        serviceFeatureTable.addDoneLoadingListener(() -> {
            String idSuCo = feature.getAttributes().get(Constant.FIELD_SUCO.ID_SUCO).toString();
            new GenerateIDSuCoByAPIAsycn(mActivity, output -> {
                if (output != null) {

                    Feature suCoThongTinFeature = serviceFeatureTable.createFeature();
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.ID_SUCO,
                            idSuCo);
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT,
                            output);
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI,
                            (short) 0);
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN,
                            mApplication.getUserDangNhap.getUserName());
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN,
                            mApplication.getDiemSuCo.getHinhThucPhatHien());
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.DIA_CHI,
                            mApplication.getDiemSuCo.getVitri());
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.GHI_CHU,
                            mApplication.getDiemSuCo.getGhiChu());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Calendar c = Calendar.getInstance();
                        suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TG_CAP_NHAT,
                                c);
                    }
                    suCoThongTinFeature.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.DON_VI,
                            mApplication.getUserDangNhap.getRole());
                    serviceFeatureTable.addFeatureAsync(suCoThongTinFeature).addDoneListener(() -> {
                        ListenableFuture<List<FeatureEditResult>> listListenableFuture = serviceFeatureTable.applyEditsAsync();
                        listListenableFuture.addDoneListener(() -> {
                            try {
                                List<FeatureEditResult> featureEditResults = listListenableFuture.get();
                                if (featureEditResults.size() > 0) {
                                    final QueryParameters queryParameters = new QueryParameters();
//                            final String query = String.format(mActivity.getString(R.string.arcgis_query_by_OBJECTID), objectId);
                                    final String query = String.format("%s = '%s'", Constant.FIELD_SUCOTHONGTIN.ID_SUCOTT, output);
                                    queryParameters.setWhereClause(query);
                                    final ListenableFuture<FeatureQueryResult> featuresAsync = serviceFeatureTable
                                            .queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.IDS_ONLY);
                                    featuresAsync.addDoneListener(() -> {
                                        try {
                                            FeatureQueryResult result = featuresAsync.get();
                                            if (result.iterator().hasNext()) {
                                                Feature item = result.iterator().next();
                                                addAttachment(item, serviceFeatureTable);
                                            }
                                        } catch (InterruptedException | ExecutionException e) {
                                            e.printStackTrace();
                                            publishProgress();
                                        }

                                    });
//                                    addAttachment(arcGISFeature, feature);
//                                    publishProgress(feature);
                                } else publishProgress();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                                publishProgress();
                            }
                        });
                    });
//                    addAttachment(arcGISFeature, feature);
//                    publishProgress(feature);
                }
            }).execute(mApplication.getConstant.getGENERATE_ID_SUCOTHONGTIN(idSuCo));
        });
    }

    private void addAttachment(final Feature feature, ServiceFeatureTable serviceFeatureTable) {
        ArcGISFeature arcGISFeature = (ArcGISFeature) feature;
        final String attachmentName = mApplication.getApplicationContext().getString(R.string.attachment_add) + "_" + System.currentTimeMillis() + ".png";
        final ListenableFuture<Attachment> addResult = arcGISFeature.addAttachmentAsync(
                mApplication.getDiemSuCo.getImage(), Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
//            if (mDialog != null && mDialog.isShowing()) {
//                mDialog.dismiss();
//            }
            try {
                Attachment attachment = addResult.get();
                if (attachment.getSize() > 0) {
                    final ListenableFuture<Void> tableResult = serviceFeatureTable.updateFeatureAsync(feature);
                    tableResult.addDoneListener(() -> {
                        final ListenableFuture<List<FeatureEditResult>> updatedServerResult = serviceFeatureTable.applyEditsAsync();
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
                                publishProgress();
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