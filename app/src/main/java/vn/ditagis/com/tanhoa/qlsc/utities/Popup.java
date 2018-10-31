package vn.ditagis.com.tanhoa.qlsc.utities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.CameraActivity;
import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.ThietBiActivity;
import vn.ditagis.com.tanhoa.qlsc.VatTuActivity;
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewInfoAdapter;
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.EditAsync;
import vn.ditagis.com.tanhoa.qlsc.async.FindLocationAsycn;
import vn.ditagis.com.tanhoa.qlsc.async.NotifyDataSetChangeAsync;
import vn.ditagis.com.tanhoa.qlsc.async.ViewAttachmentAsync;
import vn.ditagis.com.tanhoa.qlsc.connectDB.HoSoVatTuSuCoDB;
import vn.ditagis.com.tanhoa.qlsc.connectDB.HoSoVatTuThuHoiSuCoDB;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DAddress;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;

@SuppressLint("Registered")
public class Popup extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 44;
    private List<String> mListTenVatTus;
    private MainActivity mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private List<String> lstFeatureType;
    private FeatureViewMoreInfoAdapter mFeatureViewMoreInfoAdapter;

    public FeatureViewMoreInfoAdapter getFeatureViewMoreInfoAdapter() {
        return mFeatureViewMoreInfoAdapter;
    }

    private DialogInterface mDialog;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private Object mLoaiSuCoID;
    private Geocoder mGeocoder;
    private List<HoSoVatTuSuCo> mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo;
    private String mIDSuCo;
    private Button mBtnLeft;
    private List<FeatureViewMoreInfoAdapter.Item> mListItemBeNgam;
    private DApplication mApplication;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Popup(Callout callout, MainActivity mainActivity, MapView mapView
                 , Geocoder geocoder) {
        this.mMainActivity = mainActivity;
        mApplication = (DApplication) mainActivity.getApplication();
        if (mApplication.getDFeatureLayer.getLayer() != null)
            mServiceFeatureTable = (ServiceFeatureTable) mApplication.getDFeatureLayer.getLayer().getFeatureTable();
        this.mMapView = mapView;
        this.mCallout = callout;
        this.mGeocoder = geocoder;
        mListTenVatTus = new ArrayList<>();
        mLoaiSuCoID = 0;
        try {
            for (VatTu vatTu : ListObjectDB.getInstance().getVatTus())
                mListTenVatTus.add(vatTu.getTenVatTu());
        } catch (Exception ignored) {

        }
        this.mListItemBeNgam = new ArrayList<>();

    }

    public DialogInterface getDialog() {
        return mDialog;
    }

    public Button getmBtnLeft() {
        return mBtnLeft;
    }

    public Callout getCallout() {
        return mCallout;
    }

    public List<HoSoVatTuSuCo> getListHoSoVatTuSuCo() {
        return mListHoSoVatTuSuCo;
    }

    public List<HoSoVatTuSuCo> getmListHoSoVatTuThuHoiSuCo() {
        return mListHoSoVatTuThuHoiSuCo;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void refreshPopup(ArcGISFeature arcGISFeatureSuCoThongTin) {
        mSelectedArcGISFeature = arcGISFeatureSuCoThongTin;
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(mMainActivity, new ArrayList<FeatureViewInfoAdapter.Item>());
        listView.setAdapter(featureViewInfoAdapter);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        String[] outFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getOutFields().split(",");
        String[] noOutFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getNoOutFields().split(",");
        boolean isFoundField = false;
//        if (mSelectedArcGISFeature.getFeatureTable().getLayerInfo().getServiceLayerName().equals(mMainActivity.getResources().getString(R.string.ALIAS_DIEM_SU_CO))) {
        mIDSuCo = mApplication.getDiemSuCo.getIdSuCo();
//        }


        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            if (outFields.length == 1 && (outFields[0].equals("*") || outFields[0].equals("null"))) {
            } else {
                for (String outField : outFields)
                    if (outField.equals(field.getName())) {
                        isFoundField = true;
                        break;
                    }
                if (isFoundField) {
                    isFoundField = false;
                } else {
                    continue;
                }
            }
            for (String noOutField : noOutFields)
                if (noOutField.equals(field.getName())) {
                    isFoundField = true;
                    break;
                }
            if (isFoundField) {
                isFoundField = false;
                continue;
            }
            Object value = attributes.get(field.getName());
            FeatureViewInfoAdapter.Item item = new FeatureViewInfoAdapter.Item();

            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            if (value != null) {

                if (item.getFieldName().equals(typeIdField)) {
                    List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                    Object valueFeatureType = getValueFeatureType(featureTypes, value.toString());

                    if (valueFeatureType != null) {
                        mLoaiSuCoID = (Short.parseShort(attributes.get(Constant.FIELD_SUCOTHONGTIN.LOAI_SU_CO).toString()));
                        item.setValue(valueFeatureType.toString());
                    } else continue;
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = new ArrayList<>();
                    try {
                        switch (field.getName()) {
                            case Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN:
                                for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                    if (featureType.getId().equals(mLoaiSuCoID)) {
                                        codedValues = ((CodedValueDomain) featureType.getDomains()
                                                .get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();
                                        break;
                                    }
                                }
                                break;
                            case Constant.FIELD_SUCOTHONGTIN.VAT_LIEU:
                                for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                    if (featureType.getId().equals(mLoaiSuCoID)) {
                                        codedValues = ((CodedValueDomain) featureType.getDomains()
                                                .get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();
                                        break;
                                    }
                                }
                                break;
                            case Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG:
                                for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                    if (featureType.getId().equals(mLoaiSuCoID)) {
                                        codedValues = ((CodedValueDomain) featureType.getDomains()
                                                .get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();
                                        break;
                                    }
                                }
                                break;
                            default:
                                codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();

                                break;
                        }
                    } catch (Exception ignored) {

                    }
                    Object valueDomain = getValueDomain(codedValues, value.toString());
                    if (valueDomain != null) item.setValue(valueDomain.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
//                        if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
//                                || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
//                            item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
//                        else
                        Constant.DateFormat.DATE_FORMAT_VIEW.setTimeZone(TimeZone.getTimeZone("UTC"));
                        item.setValue(Constant.DateFormat.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
                        break;
                    case OID:
                    case TEXT:
                    case SHORT:
                    case DOUBLE:
                    case INTEGER:
                    case FLOAT:
                        item.setValue(value.toString());
                        break;
                }

            }
            if (item.getValue() != null && item.getValue().length() > 0) {
                featureViewInfoAdapter.add(item);
                featureViewInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void viewMoreInfo() {
//        AlertDialog.Builder builderProgress = new AlertDialog.Builder(mMainActivity);
//        LinearLayout layoutProgress = (LinearLayout) mMainActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
//        TextView txtTitle = layoutProgress.findViewById(R.id.txt_progress_dialog_title);
//        txtTitle.setText(mMainActivity.getString(R.string.message_progress_title));
//        TextView txtMessage = layoutProgress.findViewById(R.id.txt_progress_dialog_message);
//        txtMessage.setText(mMainActivity.getString(R.string.message_viewmore_message));
//
//        builderProgress.setView(layoutProgress);
//        builderProgress.setCancelable(false);
//        AlertDialog dialogProgress = builderProgress.create();
//        dialogProgress.show();
//        Window window = dialogProgress.getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//            layoutParams.copyFrom(dialogProgress.getWindow().getAttributes());
//            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            dialogProgress.getWindow().setAttributes(layoutParams);
//        }

        ArcGISFeature arcGISFeatureSuCoThongTin = mApplication.getArcGISFeature();
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        @SuppressLint("InflateParams") final View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        mFeatureViewMoreInfoAdapter = new FeatureViewMoreInfoAdapter(mMainActivity, new ArrayList<>());
        final ListView lstViewInfo = layout.findViewById(R.id.lstView_alertdialog_info);
        mBtnLeft = layout.findViewById(R.id.btn_updateinfo_left);
        Button btnRight = layout.findViewById(R.id.btn_update_right);
        Button btnStart = layout.findViewById(R.id.btn_updateinfo_start);
        btnStart.setVisibility(View.VISIBLE);
        layout.findViewById(R.id.layout_viewmoreinfo_id_su_co).setVisibility(View.VISIBLE);

        layout.findViewById(R.id.framelayout_viewmoreinfo_attachment).setOnClickListener(v -> viewAttachment());

        lstViewInfo.setAdapter(mFeatureViewMoreInfoAdapter);
        lstViewInfo.setOnItemClickListener((parent, view, position, id) -> listViewMoreInfoItemClick(parent, position, arcGISFeatureSuCoThongTin));
        loadDataViewMoreInfo(layout, arcGISFeatureSuCoThongTin);
        builder.setView(layout);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        layout.findViewById(R.id.framelayout_viewmoreinfo_attachment).setVisibility(View.VISIBLE);
        mBtnLeft.setText(mMainActivity.getResources().getString(R.string.btnLeftUpdateFeature));
        btnRight.setText(mMainActivity.getResources().getString(R.string.btnRightUpdateFeature));
        btnStart.setOnClickListener(view -> complete(arcGISFeatureSuCoThongTin, dialog));
        mBtnLeft.setOnClickListener(view ->
                update(arcGISFeatureSuCoThongTin, dialog)
        );
        btnRight.setOnClickListener(view ->

        {
            Intent intent = new Intent(mMainActivity, CameraActivity.class);
            mMainActivity.startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_CAPTURE);
            mDialog = dialog;
        });
        dialog.show();
//        if (dialogProgress.isShowing())
//            dialogProgress.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void complete(ArcGISFeature arcGISFeatureSuCoThongTin, Dialog dialog) {
        if (arcGISFeatureSuCoThongTin != null) {
            final ListenableFuture<List<Attachment>> attachmentResults = arcGISFeatureSuCoThongTin.fetchAttachmentsAsync();
            attachmentResults.addDoneListener(() -> {

                final List<Attachment> attachments;
                try {
                    attachments = attachmentResults.get();
                    boolean isFound = false;
                    if (!attachments.isEmpty()) {
                        for (final Attachment attachment : attachments) {
                            if (!attachment.getName().contains(mMainActivity.getString(R.string.attachment_add))) {
                                isFound = true;
                                break;
                            }
                        }
                    }
                    if (isFound) {
                        arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI, Constant.TRANG_THAI_SU_CO.HOAN_THANH);
                        mApplication.setArcGISFeature(arcGISFeatureSuCoThongTin);
                        for (FeatureViewMoreInfoAdapter.Item item : mFeatureViewMoreInfoAdapter.getItems())
                            if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI)) {
                                item.setValue(mMainActivity.getString(R.string.SuCo_TrangThai_HoanThanh));
                                break;
                            }
                        mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
                        update(arcGISFeatureSuCoThongTin, dialog);
                    } else {
                        Toast.makeText(dialog.getContext(), "Cần chụp ảnh để hoàn thành sự cố", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                // if selected feature has attachments, display them in a list fashion
            });


        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void update(ArcGISFeature arcGISFeatureSuCoThongTin, Dialog dialog) {
        boolean isComplete = false;
        for (FeatureViewMoreInfoAdapter.Item item : mFeatureViewMoreInfoAdapter.getItems())
            if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI)
                    && item.getValue().equals(mMainActivity.getResources().getString(R.string.SuCo_TrangThai_HoanThanh))) {
                isComplete = true;
            }
        if (isComplete) {
            final ListenableFuture<List<Attachment>> attachmentResults = arcGISFeatureSuCoThongTin.fetchAttachmentsAsync();
            attachmentResults.addDoneListener(() -> {
                try {
                    final List<Attachment> attachments = attachmentResults.get();
                    int size = attachments.size();
                    if (size == 0) {
                        MySnackBar.make(mBtnLeft, R.string.message_ChupAnh_HoanThanh, true);
                    } else if (mServiceFeatureTable != null) {
                        EditAsync editAsync;
                        editAsync = new EditAsync(mMainActivity,
                                mSelectedArcGISFeature, true, null,
                                mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo, output -> {
                            if (output != null) {
                                mCallout.dismiss();
                                dialog.dismiss();
                                new APICompleteAsync(mApplication, mIDSuCo)
                                        .execute();
                            } else {
                                MySnackBar.make(getmBtnLeft(), mMainActivity.getResources().getString(R.string.message_update_failed), true);
                            }

                        });
                        editAsync.execute(mFeatureViewMoreInfoAdapter);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        } else if (mServiceFeatureTable != null) {

            EditAsync editAsync;
            editAsync = new EditAsync(mMainActivity,
                    mSelectedArcGISFeature, true, null,
                    mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo, output -> {
                if (output != null) {
                    mCallout.dismiss();
                    dialog.dismiss();
                } else {
                    MySnackBar.make(getmBtnLeft(), mMainActivity.getResources().getString(R.string.message_update_failed), true);
                }
            });
            editAsync.execute(mFeatureViewMoreInfoAdapter);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadDataViewMoreInfo(View layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        Map<String, Object> attr = arcGISFeatureSuCoThongTin.getAttributes();
        mListItemBeNgam.clear();
        String[] updateFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getUpdateFields().split(",");
        String[] noOutFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getNoOutFields().split(",");
        boolean isFoundField = false;
        String typeIdField = arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField();
        short hinhThucPhatHien = -1;
        for (Field field : arcGISFeatureSuCoThongTin.getFeatureTable().getFields()) {
            if (field.getName().equals(Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN)) {
                Object value = attr.get(field.getName());
                if (value != null)
                    hinhThucPhatHien = Short.parseShort(value.toString());
                break;
            }
        }
        for (Field field : arcGISFeatureSuCoThongTin.getFeatureTable().getFields()) {
            for (String noOutField : noOutFields)
                if (noOutField.equals(field.getName())) {
                    isFoundField = true;
                    break;
                }
            if (isFoundField) {
                isFoundField = false;
                continue;
            }
            Object value = attr.get(field.getName());

            if (field.getName().equals(Constant.FIELD_SUCOTHONGTIN.ID_SUCO)) {
                if (value != null) {
                    mIDSuCo = value.toString();
                    ((TextView) layout.findViewById(R.id.txt_alertdialog_id_su_co)).setText(mIDSuCo);
                    this.mListHoSoVatTuSuCo = new HoSoVatTuSuCoDB(mMainActivity).find(mIDSuCo);
                    this.mListHoSoVatTuThuHoiSuCo = new HoSoVatTuThuHoiSuCoDB(mMainActivity).find(mIDSuCo);
                }
            } else {
                FeatureViewMoreInfoAdapter.Item item = new FeatureViewMoreInfoAdapter.Item();
                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                item.setEdit(false);
                for (String updateField : updateFields) {
                    //Nếu là update field
                    if (item.getFieldName().equals(updateField)) {
                        item.setEdit(true);
                        break;
                    }
                }
                if ((field.getName().equals(Constant.FIELD_SUCOTHONGTIN.TGTC_DU_KIEN_TU) ||
                        field.getName().equals(Constant.FIELD_SUCOTHONGTIN.TGTC_DU_KIEN_DEN)))
                    if (hinhThucPhatHien == Constant.HinhThucPhatHien.BE_NGAM
                            && mApplication.getUserDangNhap().getRole().equals(Constant.Role.ROLE_PGN)) {
                        item.setEdit(true);
                    } else item.setEdit(false);
//                boolean isPGNField = false, isPGNField_NotPGNRole = false;
//                for (String pgnField : pgnFields) {
//                    if (item.getFieldName().equals(pgnField)) {
//                        isPGNField = true;
////                        if (KhachHangDangNhap.getInstance().getKhachHang().getRole().equals(mMainActivity.getResources().getString(R.string.role_phong_giam_nuoc))) {
//                        item.setEdit(true);
////                        } else
////                            isPGNField_NotPGNRole = true;
//                    }
//                }

                if (value != null) {
                    if (item.getFieldName().equals(typeIdField)) {
                        List<FeatureType> featureTypes = arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes();
                        Object valueFeatureType = getValueFeatureType(featureTypes, value.toString());
                        mLoaiSuCoID = Short.parseShort(value.toString());
                        if (valueFeatureType != null) {
                            item.setValue(valueFeatureType.toString());
                        }

                    } else if (field.getDomain() != null) {
                        List<CodedValue> codedValues = new ArrayList<>();
                        try {
                            switch (field.getName()) {
                                case Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN:
                                    for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                        if (featureType.getId().equals(mLoaiSuCoID)) {
                                            codedValues = ((CodedValueDomain) featureType.getDomains()
                                                    .get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();
                                            break;
                                        }
                                    }
                                    break;
                                case Constant.FIELD_SUCOTHONGTIN.VAT_LIEU:
                                    for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                        if (featureType.getId().equals(mLoaiSuCoID)) {
                                            codedValues = ((CodedValueDomain) featureType.getDomains()
                                                    .get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();
                                            break;
                                        }
                                    }
                                    break;
                                case Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG:
                                    for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                                        if (featureType.getId().equals(mLoaiSuCoID)) {
                                            codedValues = ((CodedValueDomain) featureType.getDomains()
                                                    .get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();
                                            break;
                                        }
                                    }
                                    break;
                                default:
                                    codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();

                                    break;
                            }
                        } catch (Exception ignored) {

                        }
                        Object valueDomain = getValueDomain(codedValues, value.toString());
                        if (valueDomain != null) item.setValue(valueDomain.toString());
                    } else switch (field.getFieldType()) {
                        case DATE:
//                            if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
//                                    || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
//                                item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
//                            else
                            item.setValue(Constant.DateFormat.DATE_FORMAT.format(((Calendar) value).getTime()));
                            break;
                        case OID:
                        case TEXT:
                            item.setValue(value.toString());
                            break;
                        case DOUBLE:
                        case SHORT:
                            item.setValue(value.toString());
                            break;
                    }
                }

                item.setFieldType(field.getFieldType());
//                if (isPGNField) {
//                    if (!mListItemBeNgam.contains(item))
//                        mListItemBeNgam.add(item);
//                    continue;
//                }
//                if (isPGNField_NotPGNRole)
//                    continue;
                mFeatureViewMoreInfoAdapter.add(item);
                mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
            }
        }

    }

    private void viewAttachment() {
        try {
            ViewAttachmentAsync viewAttachmentAsync = new ViewAttachmentAsync(mMainActivity);
            viewAttachmentAsync.execute();
        } catch (Exception e) {
            Toast.makeText(mMainActivity.getApplicationContext(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    private Object getValueDomain(List<CodedValue> codedValues, String code) {
        Object value = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getCode().toString().equals(code)) {
                value = codedValue.getName();
                break;
            }

        }
        return value;
    }

    private Object getValueFeatureType(List<FeatureType> featureTypes, String code) {
        Object value = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getId().toString().equals(code)) {
                value = featureType.getName();
                break;
            }
        }
        return value;
    }

    private void listViewMoreInfoItemClick(final AdapterView<?> parent,
                                           int position, ArcGISFeature arcGISFeature) {
        if (parent.getItemAtPosition(position) instanceof FeatureViewMoreInfoAdapter.Item) {
            final FeatureViewMoreInfoAdapter.Item item = (FeatureViewMoreInfoAdapter.Item) parent.getItemAtPosition(position);
            if (item.isEdit()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Cập nhật thuộc tính");
                builder.setMessage(item.getAlias());

                @SuppressLint("InflateParams") final LinearLayout layout = (LinearLayout) mMainActivity.getLayoutInflater().
                        inflate(R.layout.layout_dialog_update_feature_listview, null);
                Button btnLeft = layout.findViewById(R.id.btn_updateinfo_left);
                Button btnRight = layout.findViewById(R.id.btn_update_right);

                btnLeft.setText(mMainActivity.getResources().getString(R.string.btnLeft_editItemViewMoreInfo));
                btnRight.setText(mMainActivity.getResources().getString(R.string.btnRight_editItemViewMoreInfo));


                builder.setView(layout);

                loadDataEdit(item, layout, arcGISFeature);

                final AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                btnLeft.setOnClickListener(view -> dialog.dismiss());
                btnRight.setOnClickListener(view -> updateEdit(item, layout, parent, dialog, arcGISFeature));
                dialog.show();

            }
        }

    }

    private void loadDataEdit(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeature) {
        switch (item.getFieldName()) {
            case Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN:
                loadDataEdit_NguyenNhan(item, layout, arcGISFeature);
                break;
            //Trường hợp vật liệu, không tự động lấy được domain
            case Constant.FIELD_SUCOTHONGTIN.VAT_LIEU:
                loadDataEdit_VatLieu(item, layout, arcGISFeature);

                break;
            case Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG:
                loadDataEdit_DuongKinhOng(item, layout, arcGISFeature);
                break;
            default:
                loadDataEdit_Another(item, layout, arcGISFeature);
                break;
        }
    }

//    private void loadDataEdit_DMA(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout) {
//        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
//        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);
//        layoutSpin.setVisibility(View.VISIBLE);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(),
//                android.R.layout.simple_list_item_1, ListObjectDB.getInstance().getDmas());
//        spin.setAdapter(adapter);
//        if (item.getValue() != null)
//            spin.setSelection(ListObjectDB.getInstance().getDmas().indexOf(item.getValue()));
//    }

//    private void loadDataEdit_ViTri(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout) {
//        final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
//        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
//        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);
//        layoutSpin.setVisibility(View.VISIBLE);
//        if (mLoaiSuCoID == Constant.LoaiSuCo.LOAISUCO_ONGNGANH) {
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, mMainActivity.getResources().getStringArray(R.array.vitri_ongnganh_arrays));
//            spin.setAdapter(adapter);
//        } else if (mLoaiSuCoID == Constant.LoaiSuCo.LOAISUCO_ONGCHINH) {
//            layoutEditText.setVisibility(View.VISIBLE);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, mMainActivity.getResources().getStringArray(R.array.vitri_ongchinh1_arrays));
//            spin.setAdapter(adapter);
//        }
////        if (item.getValue() != null)
////            spin.setSelection(mListObjectDB.indexOf(item.getValue()));
//    }

    private void loadDataEdit_NguyenNhan(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        try {
            List<CodedValue> codedValues = null;
            for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                if (featureType.getId().equals(mLoaiSuCoID)) {
                    codedValues = ((CodedValueDomain) featureType.getDomains()
                            .get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();
                    break;
                }
            }
            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } catch (Exception e) {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }

    private void loadDataEdit_VatLieu(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        try {
            List<CodedValue> codedValues = null;
            for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                if (featureType.getId().equals(mLoaiSuCoID)) {
                    codedValues = ((CodedValueDomain) featureType.getDomains()
                            .get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();
                    break;
                }
            }
            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } catch (Exception e) {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }

    private void message_select_loaiSuCo() {
        Toast.makeText(mMainActivity.getApplicationContext(), R.string.message_select_loai_su_co, Toast.LENGTH_LONG).show();
    }

    private void loadDataEdit_DuongKinhOng(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        try {
            List<CodedValue> codedValues = null;
            for (FeatureType featureType : arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()) {
                if (featureType.getId().equals(mLoaiSuCoID)) {
                    codedValues = ((CodedValueDomain) featureType.getDomains()
                            .get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();
                    break;
                }
            }

            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } catch (Exception e) {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }


    private void loadDataEdit_Another(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeature) {
        final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
        final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
        final Button button = layout.findViewById(R.id.btn_edit_viewmoreinfo);
        final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
        final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        final Domain domain = arcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
        if (item.getFieldName().equals(arcGISFeature.getFeatureTable().getTypeIdField())) {
            layoutSpin.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, lstFeatureType);
            spin.setAdapter(adapter);
            if (item.getValue() != null) {
                spin.setSelection(lstFeatureType.indexOf(item.getValue()));
            }

        } else if (domain != null) {
            layoutSpin.setVisibility(View.VISIBLE);
            List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();

            if (codedValues != null) {
                List<String> codes = new ArrayList<>();
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
                if (item.getValue() != null)
                    spin.setSelection(codes.indexOf(item.getValue()));


                if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.HINH_THUC_PHAT_HIEN)) {
                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i == 0) {
//                                if (!KhachHangDangNhap.getInstance().getKhachHang().getRole()
//                                        .equals(mMainActivity.getResources().getString(R.string.role_phong_giam_nuoc))) {
                                MySnackBar.make(spin, "Bạn không có quyền chọn lựa chọn: bể ngầm!!!", true);
                                spin.setSelection(1);

//                                } else
//                                    isVISIBLE = true;
                            }


                            for (FeatureViewMoreInfoAdapter.Item itemBeNgam : mListItemBeNgam) {
                                if (mFeatureViewMoreInfoAdapter.getItems().contains(itemBeNgam))
                                    mFeatureViewMoreInfoAdapter.remove(itemBeNgam);
                            }
                            mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

            }
        } else switch (item.getFieldType()) {
            case DATE:
                layoutTextView.setVisibility(View.VISIBLE);
                textView.setText(item.getValue());
                button.setOnClickListener(v -> {
                    final View dialogView = View.inflate(mMainActivity, R.layout.date_time_picker, null);
                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mMainActivity).create();
                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {
                        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                        String s = String.format(mMainActivity.getResources().getString(R.string.format_date_month_year), datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());

                        textView.setText(s);
                        alertDialog.dismiss();
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                });
                break;
            case TEXT:
                layoutEditText.setVisibility(View.VISIBLE);
                editText.setText(item.getValue());
                break;
            case SHORT:
                layoutEditText.setVisibility(View.VISIBLE);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(item.getValue());
                break;
            case DOUBLE:
                layoutEditText.setVisibility(View.VISIBLE);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setText(item.getValue());
                break;
        }
    }

    private void updateEdit(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, AdapterView<?> parent, DialogInterface dialog, ArcGISFeature
                                    arcGISFeatureSuCoThongTin) {
        final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
        final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        final Domain domain = arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain();
        if (item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField()) || (domain != null)) {
            //Khi đổi subtype
            item.setValue(spin.getSelectedItem().toString());
            if (item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField())) {
                for (int i = 0; i < arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes().size(); i++) {
                    FeatureType featureType = arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes().get(i);
                    if (featureType.getName().equals(item.getValue())) {
                        mLoaiSuCoID = (Short.parseShort(featureType.getId().toString()));
                        //reset những field ảnh hưởng bởi subtype
                        FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
                        for (FeatureViewMoreInfoAdapter.Item item1 : adapter.getItems()) {
                            if (item1.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN) ||
                                    item1.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU) ||
                                    item1.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)) {
                                item1.setValue(null);
                            }
                        }
                        break;
                    }
                }

            }
        } else {
            if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN) ||
                    item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU) ||
                    item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)) {
                item.setValue(spin.getSelectedItem().toString());
            } else {
                switch (item.getFieldType()) {
                    case DATE:
                        item.setValue(textView.getText().toString());
                        break;
                    case DOUBLE:
                        try {
                            double x = Double.parseDouble(editText.getText().toString());
                            item.setValue(String.format("%s", x));
                        } catch (Exception e) {
                            Toast.makeText(mMainActivity, "Số liệu nhập vào không đúng định dạng!!!", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case TEXT:
                        item.setValue(editText.getText().toString());
                        break;
                    case SHORT:
                        try {
                            short x = Short.parseShort(editText.getText().toString());
                            item.setValue(String.format("%s", x));
                        } catch (Exception e) {
                            Toast.makeText(mMainActivity, "Số liệu nhập vào không đúng định dạng!!!", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        }
        dialog.dismiss();
        item.setEdited(true);
        FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
        new NotifyDataSetChangeAsync(mMainActivity).execute(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void clearSelection() {
        if (mServiceFeatureTable != null) {
            FeatureLayer featureLayer = mApplication.getDFeatureLayer.getLayer();
            featureLayer.clearSelection();
        }
    }

    private void dimissCallout() {
        if (mCallout != null && mCallout.isShowing()) {
            mCallout.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    public void showPopup() {
        Object idSuCo = mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO);
        if (idSuCo != null) {
            mIDSuCo = idSuCo.toString();
            mApplication.getDiemSuCo.setIdSuCo(mIDSuCo);
        } else if (mApplication.getDiemSuCo != null && mApplication.getDiemSuCo.getIdSuCo() != null) {
            mIDSuCo = mApplication.getDiemSuCo.getIdSuCo();
        }
        clearSelection();
        dimissCallout();
        this.mSelectedArcGISFeature = mApplication.getArcGISFeature();
        FeatureLayer featureLayer;
        featureLayer = mApplication.getDFeatureLayer.getLayer();
        featureLayer.selectFeature(mSelectedArcGISFeature);
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_thongtinsuco, null);
        refreshPopup(mSelectedArcGISFeature);
        ((TextView) linearLayout.findViewById(R.id.txt_thongtin_ten)).setText(featureLayer.getName());
        linearLayout.findViewById(R.id.imgBtn_cancel_layout_thongtinsuco).setOnClickListener(view -> mCallout.dismiss());
        //user admin mới có quyền xóa
        if (mApplication.getDFeatureLayer.getLayerInfoDTG().isDelete()) {
            linearLayout.findViewById(R.id.imgBtn_delete).setOnClickListener(this);
        } else {
            linearLayout.findViewById(R.id.imgBtn_delete).setVisibility(View.GONE);
        }
//        if (Short.parseShort(mApplication.getArcGISFeature().getAttributes().
//                get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString()) == Constant.TRANG_THAI_SU_CO.HOAN_THANH)
//            linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo).setVisibility(View.GONE);
//        else
        linearLayout.findViewById(R.id.imgBtn_thongtinsuco_menu).setOnClickListener(this);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = mApplication.getGeometry().getExtent();
        mMapView.setViewpointGeometryAsync(envelope, 0);
        // show CallOut
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        mCallout.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    public void showPopupFindLocation(Point position, String location) {
        try {
            if (position == null)
                return;
            clearSelection();
            dimissCallout();

            LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
            linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_timkiemdiachi, null);

            ((TextView) linearLayout.findViewById(R.id.txt_timkiemdiachi)).setText(location);
            linearLayout.findViewById(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(this);
            linearLayout.findViewById(R.id.imgBtn_cancel_timkiemdiachi).setOnClickListener(view -> {
                mCallout.dismiss();
                mMainActivity.setIsAddFeature(false);
            });


            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // show CallOut
            mCallout.setLocation(position);
            mCallout.setContent(linearLayout);
            this.runOnUiThread(() -> {
                mCallout.refresh();
                mCallout.show();
            });
        } catch (Exception e) {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showPopupFindLocation(final Point position) {
        try {
            if (position == null)
                return;

            @SuppressLint("InflateParams") FindLocationAsycn findLocationAsycn = new FindLocationAsycn(mMainActivity, false,
                    mGeocoder, output -> {
                if (output != null && output.size() > 0) {
                    clearSelection();

                    dimissCallout();

                    DAddress address = output.get(0);
                    String addressLine = address.getLocation();
                    LayoutInflater inflater = LayoutInflater.from(mMainActivity.getApplicationContext());
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_timkiemdiachi, null);
                    ((TextView) linearLayout.findViewById(R.id.txt_timkiemdiachi)).

                            setText(addressLine);
                    linearLayout.findViewById(R.id.imgBtn_timkiemdiachi_themdiemsuco).

                            setOnClickListener(Popup.this);
                    linearLayout.findViewById(R.id.imgBtn_cancel_timkiemdiachi).

                            setOnClickListener(view -> {
                                mMainActivity.setIsAddFeature(false);
                                mCallout.dismiss();
                            });
                    linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    // show CallOut
                    mCallout.setLocation(position);
                    mCallout.setContent(linearLayout);
                    Popup.this.

                            runOnUiThread(() ->

                            {
                                mCallout.refresh();
                                mCallout.show();
                            });
                }
            });
            Geometry project = GeometryEngine.project(position, SpatialReferences.getWgs84());
            double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
            findLocationAsycn.setmLongtitude(location[0]);
            findLocationAsycn.setmLatitude(location[1]);
            findLocationAsycn.execute();
        } catch (
                Exception e)

        {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_thongtinsuco_menu:
                PopupMenu popup = new PopupMenu(mMainActivity, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_feature_popup, popup.getMenu());
                popup.setOnMenuItemClickListener((MenuItem item) -> {
                    switch (item.getItemId()) {
                        case R.id.item_popup_find_route:
                            mMainActivity.findRoute();
                            return true;
                        case R.id.item_popup_view_attachment:
                            viewAttachment();
                            return true;
                        case R.id.item_popup_edit:
                            viewMoreInfo();
                            return true;
                        case R.id.item_popup_vattu_capmoi:
                            Intent intent = new Intent(mMainActivity, VatTuActivity.class);
                            mApplication.setLoaiVatTu(Constant.CodeVatTu.CAPMOI);
                            mApplication.getDiemSuCo.setIdSuCo(mIDSuCo);
                            mMainActivity.startActivity(intent);
                            return true;
                        case R.id.item_popup_vattu_thuhoi:
                            Intent intentThuHoi = new Intent(mMainActivity, VatTuActivity.class);
                            mApplication.setLoaiVatTu(Constant.CodeVatTu.THUHOI);
                            mApplication.getDiemSuCo.setIdSuCo(mIDSuCo);
                            mMainActivity.startActivity(intentThuHoi);
                            return true;
                        case R.id.item_popup_thietbi:
                            Intent intentThietBi = new Intent(mMainActivity, ThietBiActivity.class);
                            mMainActivity.startActivity(intentThietBi);
                            return true;

                        default:
                            return false;
                    }
                });
                popup.show();

                break;
            case R.id.imgBtn_delete:
                mSelectedArcGISFeature.getFeatureTable().getFeatureLayer().clearSelection();
//                deleteFeature();
                break;
            case R.id.imgBtn_timkiemdiachi_themdiemsuco:
                mMainActivity.onClick(view);
                break;
        }
    }
}
