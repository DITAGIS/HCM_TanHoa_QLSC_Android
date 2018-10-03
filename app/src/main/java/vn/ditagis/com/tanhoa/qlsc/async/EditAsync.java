package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, ArcGISFeature, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTableSuCoThongTin, mServiceFeatureTableSuCo;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private boolean isUpdateAttachment;
    private byte[] mImage;
    private AsyncResponse mDelegate;
    private List<HoSoVatTuSuCo> mHoSoVatTuSuCos;
    private DApplication mApplication;

    public interface AsyncResponse {
        void processFinish(ArcGISFeature feature);
    }

    public EditAsync(Activity activity,
                     ArcGISFeature selectedArcGISFeature, boolean isUpdateAttachment, byte[] image,
                     List<HoSoVatTuSuCo> hoSoVatTu_suCos, List<HoSoVatTuSuCo> hoSoVatTuThuHoi_suCos, AsyncResponse delegate) {
        mActivity = activity;
        mApplication = (DApplication) activity.getApplication();

        this.mDelegate = delegate;
        mServiceFeatureTableSuCoThongTin = mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin();
        mServiceFeatureTableSuCo = (ServiceFeatureTable) mApplication.getDFeatureLayer.getLayer().getFeatureTable();
        mSelectedArcGISFeature = selectedArcGISFeature;
        mDialog = new ProgressDialog(activity, android.R.style.Theme_Material_Dialog_Alert);
        this.isUpdateAttachment = isUpdateAttachment;
        this.mImage = image;
        this.mHoSoVatTuSuCos = hoSoVatTu_suCos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mActivity.getString(R.string.async_dang_xu_ly));
        mDialog.setCancelable(false);
        mDialog.show();

    }

    @Override
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        QueryServiceFeatureTableAsync queryServiceFeatureTableAsync = new QueryServiceFeatureTableAsync(
                mActivity, mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin(), output -> {
            ArcGISFeature arcGISFeatureSuCoThongTin = (ArcGISFeature) output;
            final FeatureViewMoreInfoAdapter adapter = params[0];
            mDialog.setMax(adapter.getCount());
            final Calendar[] c = {Calendar.getInstance()};

            String loaiSuCo = "";
            short loaiSuCoShort = 0;
            String trangThai = "";
            boolean hasDomain = false;
            for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
                if (item.getFieldName().equals(Constant.FIELD_SUCO.LOAI_SU_CO)) {
                    loaiSuCo = item.getValue();

                } else if (item.getFieldName().equals(Constant.FIELD_SUCO.TRANG_THAI))
                    trangThai = item.getValue();
            }
            List<FeatureType> featureTypes = arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes();
            Object idFeatureTypes = getIdFeatureTypes(featureTypes, loaiSuCo);
            if (idFeatureTypes != null) {

                loaiSuCoShort = (Short.parseShort(idFeatureTypes.toString()));
//            mSelectedArcGISFeature.getAttributes().put(mActivity.getString(R.string.Field_SuCo_LoaiSuCo), loaiSuCoShort);
            }
//        mSelectedArcGISFeature.getAttributes().put("DuongKinhOng",Short.parseShort(("1")));
            final String finalLoaiSuCo = loaiSuCo;
            //todo loaiSuCo - 1 chưa rõ nguyên nhân
            final short finalLoaiSuCoShort = loaiSuCoShort;
            final String finalTrangThai = trangThai;

            for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
                if (item.getValue() == null || !item.isEdit() || !item.isEdited()) continue;
                Domain domain = arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain();
                Object codeDomain = null;
                if (domain != null) {
                    hasDomain = true;
                    //Trường hợp nguyên nhân, không tự động lấy được domain
                    if (item.getFieldName().equals(Constant.FIELD_SUCO.NGUYEN_NHAN)) {
                        if (finalLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                    .get(finalLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCO.NGUYEN_NHAN)).getCodedValues();
                            if (codedValues != null) {
                                for (CodedValue codedValue : codedValues) {
                                    if (codedValue.getName().equals(item.getValue())) {
                                        codeDomain = codedValue.getCode();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Trường hợp vật liệu, không tự động lấy được domain
                    else if (item.getFieldName().equals(Constant.FIELD_SUCO.VAT_LIEU)) {
                        if (finalLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                    .get(finalLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCO.VAT_LIEU)).getCodedValues();
                            if (codedValues != null) {
                                for (CodedValue codedValue : codedValues) {
                                    if (codedValue.getName().equals(item.getValue())) {
                                        codeDomain = codedValue.getCode();
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (item.getFieldName().equals(Constant.FIELD_SUCO.DUONG_KINH_ONG)) {
                        if (finalLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || finalLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                    .get(finalLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCO.DUONG_KINH_ONG)).getCodedValues();
                            if (codedValues != null) {
                                for (CodedValue codedValue : codedValues) {
                                    if (codedValue.getName().equals(item.getValue())) {
                                        codeDomain = codedValue.getCode();
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                        codeDomain = getCodeDomain(codedValues, item.getValue());
                    }
                }
                if (item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField())) {
                    arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), finalLoaiSuCoShort);
                } else switch (item.getFieldType()) {
                    case DATE:
                        Date date;
                        try {

                            date = Constant.DATE_FORMAT_VIEW.parse(item.getValue());
                            c[0].setTime(date);
                            arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), c[0]);
                        } catch (ParseException e) {
                            try {
                                date = Constant.DATE_FORMAT.parse(item.getValue());
                                c[0].setTime(date);
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), c[0]);
                            } catch (ParseException ignored) {

                            }

                        }
                        break;

                    case TEXT:
                        if (hasDomain)
                            if (codeDomain != null)
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), codeDomain.toString());
                            else
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), null);
                        else
                            arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), item.getValue());
                        break;
                    case SHORT:
                        if (codeDomain != null) {
                            arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                        } else
                            try {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                            } catch (NumberFormatException e) {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), null);
                            }
                        break;
                    case DOUBLE:
                        if (codeDomain != null) {
                            arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Double.parseDouble(codeDomain.toString()));
                        } else
                            try {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Double.parseDouble(item.getValue()));
                            } catch (NumberFormatException e) {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), null);
                            }
                        break;
                    case INTEGER:
                        if (codeDomain != null) {
                            arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Integer.parseInt(codeDomain.toString()));
                        } else
                            try {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), Integer.parseInt(item.getValue()));
                            } catch (NumberFormatException e) {
                                arcGISFeatureSuCoThongTin.getAttributes().put(item.getFieldName(), null);
                            }
                        break;
                }
                hasDomain = false;
            }
            arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI, Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
            if (finalTrangThai.equals(mActivity.getString(R.string.SuCo_TrangThai_HoanThanh))) {
                arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI, Constant.TRANG_THAI_SU_CO.HOAN_THANH);
//                c[0] = Calendar.getInstance();
//                arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCO.TGKHAC_PHUC, c[0]);
//                long ngayKhacPhuc = c[0].getTimeInMillis();
//                long ngayThongBao = ((Calendar) arcGISFeatureSuCoThongTin.getAttributes().
//                        get(Constant.FIELD_SUCO.TGPHAN_ANH)).getTimeInMillis();
//                double thoiGianThucHien = new BigDecimal((double) (ngayKhacPhuc - ngayThongBao) / (60 * 60 * 1000)).setScale(2, RoundingMode.HALF_UP).doubleValue();
//            arcGISFeature.getAttributes().put((mActivity.getString(R.string.Field_SuCo_ThoiGianThucHien)), thoiGianThucHien);
            }
            arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TG_CAP_NHAT, Calendar.getInstance());
//        arcGISFeature.getAttributes().put(mActivity.getString(R.string.Field_SuCo_NhanVienGiamSat),
//               mApplication.getUserDangNhap.getUserName());
            mServiceFeatureTableSuCoThongTin.loadAsync();
            mServiceFeatureTableSuCoThongTin.addDoneLoadingListener(() -> {
                // update feature in the feature table
                mServiceFeatureTableSuCoThongTin.updateFeatureAsync(arcGISFeatureSuCoThongTin).addDoneListener(() ->
                        mServiceFeatureTableSuCoThongTin.applyEditsAsync().addDoneListener(() -> {
                            if (isUpdateAttachment && mImage != null) {
                                if (arcGISFeatureSuCoThongTin.canEditAttachments())
                                    addAttachment(arcGISFeatureSuCoThongTin);
                                else
                                    updateSuCo(arcGISFeatureSuCoThongTin);
                            } else {
                                updateSuCo(arcGISFeatureSuCoThongTin);

                            }
                        }));
            });

        });
        String queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(queryClause);
        queryServiceFeatureTableAsync.execute(queryParameters);

        return null;
    }

    private void updateSuCo(ArcGISFeature arcGISFeature) {
        String queryClause = String.format("%s = '%s'",
                Constant.FIELD_SUCO.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString());
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(queryClause);
        new QueryServiceFeatureTableAsync(mActivity, (ServiceFeatureTable) mApplication.getDFeatureLayer.getLayer().getFeatureTable(), output -> {
            if (output != null) {
                ArcGISFeature arcGISFeatureSuCo = (ArcGISFeature) output;
                Object trangThai = arcGISFeature.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI);
                short trangThaiShort = 0;
                if (trangThai != null)
                    trangThaiShort = Short.parseShort((trangThai.toString()));

                Object hinhThucPhatHien = arcGISFeature.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN);
                short hinhThucPhatHienShort = 2;
                if (hinhThucPhatHien != null)
                    hinhThucPhatHienShort = Short.parseShort(hinhThucPhatHien.toString());
                if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.GROUPROLE_TC)) {
                    arcGISFeatureSuCo.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI_THI_CONG,
                            trangThaiShort);
                    arcGISFeatureSuCo.getAttributes().put(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_THI_CONG,
                            hinhThucPhatHienShort);

                } else if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.GROUPROLE_GS)) {
                    arcGISFeatureSuCo.getAttributes().put(Constant.FIELD_SUCO.TRANG_THAI_GIAM_SAT,
                            trangThaiShort);
                    arcGISFeatureSuCo.getAttributes().put(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_GIAM_SAT,
                            hinhThucPhatHienShort);
                }
                mServiceFeatureTableSuCo.loadAsync();
                mServiceFeatureTableSuCo.addDoneLoadingListener(() -> {
                    // update feature in the feature table
                    mServiceFeatureTableSuCo.updateFeatureAsync(arcGISFeatureSuCo).addDoneListener(() ->
                            mServiceFeatureTableSuCo.applyEditsAsync().addDoneListener(() -> {
                                applyEdit(arcGISFeature);
                            }));
                });
            } else publishProgress();
        }).execute(queryParameters);
        mServiceFeatureTableSuCo.loadAsync();
        mServiceFeatureTableSuCo.addDoneLoadingListener(() -> {
        });
    }

    private void addAttachment(ArcGISFeature arcGISFeature) {

        final String attachmentName = mActivity.getString(R.string.attachment) + "_" + System.currentTimeMillis() + ".png";
        final ListenableFuture<Attachment> addResult = arcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
            try {
                Attachment attachment = addResult.get();
                if (attachment.getSize() > 0) {
                    final ListenableFuture<Void> tableResult = mServiceFeatureTableSuCoThongTin.updateFeatureAsync(arcGISFeature);
                    tableResult.addDoneListener(() -> updateSuCo(arcGISFeature));
                } else publishProgress();
            } catch (Exception ignored) {
                publishProgress();
            }
        });
    }


    private void applyEdit(ArcGISFeature arcGISFeature) {
        final ListenableFuture<List<FeatureEditResult>> updatedServerResult = mServiceFeatureTableSuCoThongTin.applyEditsAsync();
        updatedServerResult.addDoneListener(() -> {
            List<FeatureEditResult> edits;
            try {
                edits = updatedServerResult.get();
//                if (edits.size() > 0) {
//                    if (!edits.get(0).hasCompletedWithErrors()) {
                publishProgress(arcGISFeature);
                //attachmentList.add(fileName);
//                                                String s = arcGISFeature.getAttributes().get("objectid").toString();
                // update the attachment list view/ on the control panel
//                    }
//                } else {
//                    publishProgress();
//                }
            } catch (InterruptedException | ExecutionException e) {
                publishProgress();
                e.printStackTrace();
            }
        });

    }

    private Object getIdFeatureTypes(List<FeatureType> featureTypes, String value) {
        Object code = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getName().equals(value)) {
                code = featureType.getId();
                break;
            }
        }
        return code;
    }

    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().equals(value)) {
                code = codedValue.getCode();
                break;
            }
        }
        return code;
    }

    @Override
    protected void onProgressUpdate(ArcGISFeature... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (values != null && values.length > 0)
            this.mDelegate.processFinish(values[0]);
        else mDelegate.processFinish(null);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

