package vn.ditagis.com.tanhoa.qlsc.utities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureEditResult;
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
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
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
    private DialogInterface mDialog;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private LocationDisplay mLocationDisplay;
    private short mLoaiSuCoShort;
    private Geocoder mGeocoder;
    private List<HoSoVatTuSuCo> mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo;
    private String mIDSuCo;
    private ArcGISMapImageLayer mArcGISMapImageLayerAdmin;
    private Button mBtnLeft;
    private List<FeatureViewMoreInfoAdapter.Item> mListItemBeNgam;
    private DApplication mApplication;

    public Popup(Callout callout, MainActivity mainActivity, MapView mapView,
                 LocationDisplay locationDisplay, Geocoder geocoder, ArcGISMapImageLayer arcGISMapImageLayer) {
        this.mMainActivity = mainActivity;
        mApplication = (DApplication) mainActivity.getApplication();
        if (mApplication.getDFeatureLayer.getLayer() != null)
            mServiceFeatureTable = (ServiceFeatureTable) mApplication.getDFeatureLayer.getLayer().getFeatureTable();
        this.mMapView = mapView;
        this.mCallout = callout;
        this.mLocationDisplay = locationDisplay;
        this.mGeocoder = geocoder;
        mListTenVatTus = new ArrayList<>();
        mLoaiSuCoShort = 0;
        try {
            for (VatTu vatTu : ListObjectDB.getInstance().getVatTus())
                mListTenVatTus.add(vatTu.getTenVatTu());
        } catch (Exception ignored) {

        }
        this.mArcGISMapImageLayerAdmin = arcGISMapImageLayer;
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


    private void refreshPopup(ArcGISFeature arcGISFeature) {
        mSelectedArcGISFeature = arcGISFeature;
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(mMainActivity, new ArrayList<FeatureViewInfoAdapter.Item>());
        listView.setAdapter(featureViewInfoAdapter);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        String[] outFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getOutFields().split(",");
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
            Object value = attributes.get(field.getName());
            FeatureViewInfoAdapter.Item item = new FeatureViewInfoAdapter.Item();

            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
//            if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTu))) {
//                StringBuilder builder = new StringBuilder();
//                this.mListHoSoVatTuSuCo = new HoSoVatTuSuCoDB(mMainActivity).find(mIDSuCo);
//                for (HoSoVatTuSuCo hoSoVatTuSuCo : mListHoSoVatTuSuCo) {
//                    builder.append(hoSoVatTuSuCo.getTenVatTu()).append(" ").append(hoSoVatTuSuCo.getSoLuong()).append(" ").append(hoSoVatTuSuCo.getDonViTinh()).append("\n");
//                }
//                if (builder.length() > 0)
//                    builder.replace(builder.length() - 2, builder.length(), "");
//                item.setValue(builder.toString());
//            } else if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTuThuHoi))) {
//                StringBuilder builder = new StringBuilder();
//                this.mListHoSoVatTuThuHoiSuCo = new HoSoVatTuThuHoiSuCoDB(mMainActivity).find(mIDSuCo);
//                for (HoSoVatTuSuCo hoSoVatTuSuCo : mListHoSoVatTuThuHoiSuCo) {
//                    builder.append(hoSoVatTuSuCo.getTenVatTu()).append(" ").append(hoSoVatTuSuCo.getSoLuong()).append(" ").append(hoSoVatTuSuCo.getDonViTinh()).append("\n");
//                }
//                if (builder.length() > 0)
//                    builder.replace(builder.length() - 2, builder.length(), "");
//                item.setValue(builder.toString());
//            }
//            else
            if (value != null) {

                if (item.getFieldName().equals(typeIdField)) {
                    List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                    Object valueFeatureType = getValueFeatureType(featureTypes, value.toString());

                    if (valueFeatureType != null) {
                        mLoaiSuCoShort = (Short.parseShort(attributes.get(Constant.FIELD_SUCOTHONGTIN.LOAI_SU_CO).toString()));
                        item.setValue(valueFeatureType.toString());
                    } else continue;
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = new ArrayList<>();
                    if (field.getName().equals(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)) {
                        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            codedValues = ((CodedValueDomain) mSelectedArcGISFeature.getFeatureTable().getFeatureTypes()
                                    .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();

                        }
                    } else if (field.getName().equals(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)) {
                        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            codedValues = ((CodedValueDomain) mSelectedArcGISFeature.getFeatureTable().getFeatureTypes()
                                    .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();

                        }
                    } else if (field.getName().equals(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)) {
                        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                            codedValues = ((CodedValueDomain) mSelectedArcGISFeature.getFeatureTable().getFeatureTypes()
                                    .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();

                        }
                    } else {
                        codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();

                    }
                    Object valueDomain = getValueDomain(codedValues, value.toString());
                    if (valueDomain != null) item.setValue(valueDomain.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
//                        if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
//                                || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
//                            item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
//                        else
                        item.setValue(Constant.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
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

    /**
     * Xem sự cố thông tin của nhân viên đó
     *
     * @return
     */

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
            capture(false);
            mDialog = dialog;
        });
        dialog.show();
//        if (dialogProgress.isShowing())
//            dialogProgress.dismiss();
    }

    private void complete(ArcGISFeature arcGISFeatureSuCoThongTin, Dialog dialog) {
        if (arcGISFeatureSuCoThongTin != null) {
            arcGISFeatureSuCoThongTin.getAttributes().put(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI, Constant.TRANG_THAI_SU_CO.HOAN_THANH);
            mApplication.setArcGISFeature(arcGISFeatureSuCoThongTin);
            for (FeatureViewMoreInfoAdapter.Item item : mFeatureViewMoreInfoAdapter.getItems())
                if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI)) {
                    item.setValue(mMainActivity.getString(R.string.SuCo_TrangThai_HoanThanh));
                    break;
                }
            mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
            update(arcGISFeatureSuCoThongTin, dialog);
        }

    }

    private void update(ArcGISFeature arcGISFeatureSuCoThongTin, Dialog dialog) {
        boolean isComplete = false;
        for (FeatureViewMoreInfoAdapter.Item item : mFeatureViewMoreInfoAdapter.getItems())
            if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI)
                    && item.getValue().toString().equals(mMainActivity.getResources().getString(R.string.SuCo_TrangThai_HoanThanh))) {
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
                                mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo, arcGISFeature1 -> {
                            mCallout.dismiss();
                            dialog.dismiss();
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
                    mListHoSoVatTuSuCo, mListHoSoVatTuThuHoiSuCo, arcGISFeature -> {
                mCallout.dismiss();
                dialog.dismiss();
            });
            editAsync.execute(mFeatureViewMoreInfoAdapter);
        }

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

    private void loadDataViewMoreInfo(View layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        Map<String, Object> attr = arcGISFeatureSuCoThongTin.getAttributes();
        mListItemBeNgam.clear();
        String[] updateFields = mApplication.getDFeatureLayer.getLayerInfoDTG().getUpdateFields().split(",");
        String[] pgnFields = new String[]{};
        String typeIdField = arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField();
        for (Field field : arcGISFeatureSuCoThongTin.getFeatureTable().getFields()) {
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
                        mLoaiSuCoShort = Short.parseShort(value.toString());
                        if (valueFeatureType != null) {
                            item.setValue(valueFeatureType.toString());
                        }

                    } else if (field.getDomain() != null) {
                        List<CodedValue> codedValues = new ArrayList<>();
                        switch (field.getName()) {
                            case Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN:
                                if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                                    codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                            .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();
                                }
                                break;
                            case Constant.FIELD_SUCOTHONGTIN.VAT_LIEU:
                                if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                                    codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                            .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();

                                }
                                break;
                            case Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG:
                                if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
                                    codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                                            .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();

                                }
                                break;
                            default:
                                codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();

                                break;
                        }
                        Object valueDomain = getValueDomain(codedValues, value.toString());
                        if (valueDomain != null) item.setValue(valueDomain.toString());
                    } else switch (field.getFieldType()) {
                        case DATE:
//                            if (item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienTuNgay))
//                                    || item.getFieldName().equals(mMainActivity.getString(R.string.Field_SuCo_ThoiGianThiCongDuKienDenNgay)))
//                                item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
//                            else
                            item.setValue(Constant.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
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
                for (String updateField : updateFields) {
                    //Nếu là update field
                    if (item.getFieldName().equals(updateField)) {
                        item.setEdit(true);
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
        ViewAttachmentAsync viewAttachmentAsync = new ViewAttachmentAsync(mMainActivity, mSelectedArcGISFeature);
        viewAttachmentAsync.execute();
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

    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().toString().equals(value)) {
                code = codedValue.getCode();
                break;
            }

        }
        return code;
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

    private void listViewMoreInfoItemClick(final AdapterView<?> parent, int position, ArcGISFeature arcGISFeature) {
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

    private void loadDataEdit(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout, ArcGISFeature arcGISFeature) {
        //Load danh sách madma từ csdl
//        if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_MADMA))) {
//            loadDataEdit_DMA(item, layout);
//        }
        //Trường hợp vị trí thì không dùng domain, vì còn có nhập khoảng cách
//        else
//            if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_ViTri))) {
//            loadDataEdit_ViTri(item, layout);
//        }
        //Trường hợp nguyên nhân, không tự động lấy được domain
//        else
        if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)) {
            loadDataEdit_NguyenNhan(item, layout, arcGISFeature);
        }
        //Trường hợp vật liệu, không tự động lấy được domain
        else if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)) {
            loadDataEdit_VatLieu(item, layout, arcGISFeature);

        }
        //Trường hợp vật tư, không tự động lấy được domain
//        else if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTu))) {
//            loadDataEdit_VatTu(item, layout);
//        } else if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTuThuHoi))) {
//            loadDataEdit_VatTuThuHoi(item, layout);
//        }
        else if (item.getFieldName().equals(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)) {
            loadDataEdit_DuongKinhOng(item, layout, arcGISFeature);
        } else {
            loadDataEdit_Another(item, layout, arcGISFeature);
        }
    }

    private void loadDataEdit_DMA(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);
        layoutSpin.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(),
                android.R.layout.simple_list_item_1, ListObjectDB.getInstance().getDmas());
        spin.setAdapter(adapter);
        if (item.getValue() != null)
            spin.setSelection(ListObjectDB.getInstance().getDmas().indexOf(item.getValue()));
    }

    private void loadDataEdit_ViTri(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout) {
        final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);
        layoutSpin.setVisibility(View.VISIBLE);
        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, mMainActivity.getResources().getStringArray(R.array.vitri_ongnganh_arrays));
            spin.setAdapter(adapter);
        } else if (mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
            layoutEditText.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, mMainActivity.getResources().getStringArray(R.array.vitri_ongchinh1_arrays));
            spin.setAdapter(adapter);
        }
//        if (item.getValue() != null)
//            spin.setSelection(mListObjectDB.indexOf(item.getValue()));
    }

    private void loadDataEdit_NguyenNhan(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout, ArcGISFeature arcGISFeature) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeature.getFeatureTable().getFeatureTypes()
                    .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.NGUYEN_NHAN)).getCodedValues();
            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } else {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }

    private void loadDataEdit_VatLieu(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout, ArcGISFeature arcGISFeatureSuCoThongTin) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes()
                    .get(mLoaiSuCoShort - 1).getDomains().get(Constant.FIELD_SUCOTHONGTIN.VAT_LIEU)).getCodedValues();
            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } else {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }

    private void message_select_loaiSuCo() {
        Toast.makeText(mMainActivity.getApplicationContext(), R.string.message_select_loai_su_co, Toast.LENGTH_LONG).show();
    }

    private void loadDataEdit_DuongKinhOng(FeatureViewMoreInfoAdapter.Item item, LinearLayout
            layout, ArcGISFeature arcGISFeature) {
        final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        layoutSpin.setVisibility(View.VISIBLE);
        List<String> codes = new ArrayList<>();
        if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH || mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
            List<CodedValue> codedValues = ((CodedValueDomain) arcGISFeature
                    .getFeatureTable().getFeatureTypes()
                    .get(mLoaiSuCoShort - 1).getDomains()
                    .get(Constant.FIELD_SUCOTHONGTIN.DUONG_KINH_ONG)).getCodedValues();
            if (codedValues != null) {
                for (CodedValue codedValue : codedValues)
                    codes.add(codedValue.getName());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                spin.setAdapter(adapter);
            }
        } else {
            message_select_loaiSuCo();
        }
        if (item.getValue() != null)
            spin.setSelection(codes.indexOf(item.getValue()));
    }


//    private void loadDataEdit_VatTuThuHoi(FeatureViewMoreInfoAdapter.Item item, LinearLayout layout) {
//        final LinearLayout layoutAutoCompleteTV = layout.findViewById(R.id.llayout_AutoCompleteTV_vattu);
//        final AutoCompleteTextView autoCompleteTextView = layout.findViewById(R.id.autoCompleteTV_vattu);
//        autoCompleteTextView.setBackgroundResource(R.drawable.layout_border);
//        final ListView listViewVatTuThuHoi = layout.findViewById(R.id.lstview_vattu);
//        final EditText etxtSoLuong = layout.findViewById(R.id.etxt_soLuong_vattu);
//        final TextView txtDonViTinh = layout.findViewById(R.id.txt_donvitinh_vattu);
//        final TextView txtThemVatTu = layout.findViewById(R.id.txt_them_vattu);
//
//        if (mLoaiSuCoShort != Constant.LOAISUCO_CHUAPHANLOAI) {
//            layoutAutoCompleteTV.setVisibility(View.VISIBLE);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, mListTenVatTus);
//            autoCompleteTextView.setAdapter(adapter);
//        }
//        final VatTuAdapter vatTuThuHoiAdapter = new VatTuAdapter(layout.getContext(), new ArrayList<VatTuAdapter.Item>());
//        final String[] maVatTu = {""};
//        listViewVatTuThuHoi.setAdapter(vatTuThuHoiAdapter);
//
//        //Nhấn và giữ một item để xóa
//        listViewVatTuThuHoi.setOnItemLongClickListener((adapterView, view, i, l) -> {
//            final VatTuAdapter.Item itemVatTu = (VatTuAdapter.Item) adapterView.getAdapter().getItem(i);
//            final AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
//            builder.setTitle("Xóa vật tư");
//            builder.setMessage("Bạn có chắc muốn xóa vật tư " + itemVatTu.getTenVatTu());
//            builder.setCancelable(false).setNegativeButton("Hủy",
//                    (dialog, which) -> dialog.dismiss()).setPositiveButton("Xóa", (dialogInterface, i12) -> {
//                vatTuThuHoiAdapter.remove(itemVatTu);
//                vatTuThuHoiAdapter.notifyDataSetChanged();
//
//                dialogInterface.dismiss();
//            });
//            AlertDialog dialog = builder.create();
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.show();
//            return false;
//        });
//        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String tenVatTu = editable.toString();
//                if (mLoaiSuCoShort == Constant.LOAISUCO_ONGNGANH) {
//                    for (VatTu vatTu : ListObjectDB.getInstance().getVatTuOngNganhs()) {
//                        if (vatTu.getTenVatTu().equals(tenVatTu)) {
//                            txtDonViTinh.setText(vatTu.getDonViTinh());
//                            maVatTu[0] = vatTu.getMaVatTu();
//                            break;
//                        }
//                    }
//                } else if (mLoaiSuCoShort == Constant.LOAISUCO_ONGCHINH) {
//                    for (VatTu vatTu : ListObjectDB.getInstance().getVatTus()) {
//                        if (vatTu.getTenVatTu().equals(tenVatTu)) {
//                            txtDonViTinh.setText(vatTu.getDonViTinh());
//                            maVatTu[0] = vatTu.getMaVatTu();
//                            break;
//                        }
//                    }
//                }
//            }
//        });
//        txtThemVatTu.setOnClickListener(view -> {
//            if (etxtSoLuong.getText().toString().trim().length() == 0)
//                MySnackBar.make(etxtSoLuong, mMainActivity.getResources().getString(R.string.message_soluong_themvattu), true);
//            else {
//                try {
//                    double soLuong = Double.parseDouble(etxtSoLuong.getText().toString());
//                    vatTuThuHoiAdapter.add(new VatTuAdapter.Item(autoCompleteTextView.getText().toString(),
//                            soLuong, txtDonViTinh.getText().toString(), maVatTu[0]));
//                    vatTuThuHoiAdapter.notifyDataSetChanged();
//
//                    autoCompleteTextView.setText("");
//                    etxtSoLuong.setText("");
//                    txtDonViTinh.setText("");
//
//                    if (listViewVatTuThuHoi.getHeight() > 500) {
//                        ViewGroup.LayoutParams params = listViewVatTuThuHoi.getLayoutParams();
//                        params.height = 500;
//                        listViewVatTuThuHoi.setLayoutParams(params);
//                    }
//                } catch (NumberFormatException e) {
//                    MySnackBar.make(etxtSoLuong, mMainActivity.getResources().getString(R.string.message_number_format_exception), true);
//                }
//
//            }
//        });
//
//        for (HoSoVatTuSuCo hoSoVatTuSuCo : mListHoSoVatTuThuHoiSuCo) {
//            vatTuThuHoiAdapter.add(new VatTuAdapter.Item(hoSoVatTuSuCo.getTenVatTu(), hoSoVatTuSuCo.getSoLuong(),
//                    hoSoVatTuSuCo.getDonViTinh(), hoSoVatTuSuCo.getMaVatTu()));
//        }
//        vatTuThuHoiAdapter.notifyDataSetChanged();
//
//    }

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
                            boolean isVISIBLE = false;
                            if (i == 0) {
//                                if (!KhachHangDangNhap.getInstance().getKhachHang().getRole()
//                                        .equals(mMainActivity.getResources().getString(R.string.role_phong_giam_nuoc))) {
                                MySnackBar.make(spin, "Bạn không có quyền chọn lựa chọn: bể ngầm!!!", true);
                                spin.setSelection(1);

                                isVISIBLE = false;
//                                } else
//                                    isVISIBLE = true;
                            } else isVISIBLE = false;


                            if (isVISIBLE) {
                                for (FeatureViewMoreInfoAdapter.Item itemBeNgam : mListItemBeNgam) {
                                    if (!mFeatureViewMoreInfoAdapter.getItems().contains(itemBeNgam))
                                        mFeatureViewMoreInfoAdapter.add(itemBeNgam);
                                }
                            } else {
                                for (FeatureViewMoreInfoAdapter.Item itemBeNgam : mListItemBeNgam) {
                                    if (mFeatureViewMoreInfoAdapter.getItems().contains(itemBeNgam))
                                        mFeatureViewMoreInfoAdapter.remove(itemBeNgam);
                                }
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
                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                            String s = String.format(mMainActivity.getResources().getString(R.string.format_date_month_year), datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());

                            textView.setText(s);
                            alertDialog.dismiss();
                        }
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
        boolean isCanUpdate = true;
        final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
        final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
        final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

        final Domain domain = arcGISFeatureSuCoThongTin.getFeatureTable().getField(item.getFieldName()).getDomain();
//        if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_MADMA))) {
//            item.setValue(spin.getSelectedItem().toString());
//        } else
//            if (mLoaiSuCoShort != null && item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_ViTri))) {
//            if (mLoaiSuCoShort.equals(mMainActivity.getResources().getString(R.string.LoaiSuCo_OngNganh))) {
//                item.setValue(spin.getSelectedItem().toString());
//            } else if (mLoaiSuCoShort.equals(mMainActivity.getResources().getString(R.string.LoaiSuCo_OngChinh))) {
//                item.setValue(spin.getSelectedItem().toString() + editText.getText().toString());
//            }
//        } else if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTu))) {
//            mListHoSoVatTuSuCo = new ArrayList<>();
//
//            if (listViewVatTu.getAdapter() != null) {
//                if (listViewVatTu.getAdapter().getCount() == 0) {
//                    isCanUpdate = false;
//                    MySnackBar.make(listViewVatTu, mMainActivity.getResources().getString(R.string.message_CapNhat_VatTu), true);
//                } else {
//                    VatTuAdapter vatTuAdapter = (VatTuAdapter) listViewVatTu.getAdapter();
//                    for (VatTuAdapter.Item itemVatTu : vatTuAdapter.getItems()) {
//                        mListHoSoVatTuSuCo.add(new HoSoVatTuSuCo(mIDSuCo, itemVatTu.getSoLuong(), itemVatTu.getMaVatTu(), itemVatTu.getTenVatTu(), itemVatTu.getDonVi()));
//                    }
//                    if (mListHoSoVatTuSuCo.size() > 0) {
//                        VatTuAdapter.Item itemVatTu = vatTuAdapter.getItem(0);
//                        item.setValue(itemVatTu.getTenVatTu() + "\n" + itemVatTu.getSoLuong() + " " + itemVatTu.getDonVi() + "\n...");
//                    }
//                }
//            }
//
//        } else if (item.getFieldName().equals(mMainActivity.getResources().getString(R.string.Field_SuCo_VatTuThuHoi))) {
//            mListHoSoVatTuThuHoiSuCo = new ArrayList<>();
//
//            if (listViewVatTu.getAdapter() != null) {
//                if (listViewVatTu.getAdapter().getCount() == 0) {
//                    isCanUpdate = false;
//                    MySnackBar.make(listViewVatTu, mMainActivity.getResources().getString(R.string.message_CapNhat_VatTu), true);
//                } else {
//                    VatTuAdapter vatTuAdapter = (VatTuAdapter) listViewVatTu.getAdapter();
//                    for (VatTuAdapter.Item itemVatTu : vatTuAdapter.getItems()) {
//                        mListHoSoVatTuThuHoiSuCo.add(new HoSoVatTuSuCo(mIDSuCo, itemVatTu.getSoLuong(), itemVatTu.getMaVatTu(), itemVatTu.getTenVatTu(), itemVatTu.getDonVi()));
//                    }
//                    if (mListHoSoVatTuThuHoiSuCo.size() > 0) {
//                        VatTuAdapter.Item itemVatTu = vatTuAdapter.getItem(0);
//                        item.setValue(itemVatTu.getTenVatTu() + "\n" + itemVatTu.getSoLuong() + " " + itemVatTu.getDonVi() + "\n...");
//                    }
//                }
//            }

//        } else
        if (item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField()) || (domain != null)) {
            //Khi đổi subtype
            //Phải set những field liên quan đến subtype = null;
            if ((item.getValue() == null || !item.getValue().equals(spin.getSelectedItem().toString())) && item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField())) {
//                String[] field_subtypeArr = mMainActivity.getResources().getStringArray(R.array.field_subtype_array);
                String[] field_subtypeArr = new String[]{};
                for (int i = 0; i < parent.getCount(); i++) {
                    FeatureViewMoreInfoAdapter.Item item1 = (FeatureViewMoreInfoAdapter.Item) parent.getAdapter().getItem(i);
                    for (String field_subtype : field_subtypeArr) {
                        if (item1.getFieldName().equals(field_subtype)) {
                            item1.setValue("");
                            item1.setEdited(true);
                            ((FeatureViewMoreInfoAdapter) parent.getAdapter()).notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
            item.setValue(spin.getSelectedItem().toString());
            if (item.getFieldName().equals(arcGISFeatureSuCoThongTin.getFeatureTable().getTypeIdField())) {
                for (int i = 0; i < arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes().size(); i++) {
                    FeatureType featureType = arcGISFeatureSuCoThongTin.getFeatureTable().getFeatureTypes().get(i);
                    if (featureType.getName().equals(item.getValue())) {
                        mLoaiSuCoShort = (Short.parseShort(featureType.getId().toString()));
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
        } else if (domain == null) {
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
        if (isCanUpdate) {
            dialog.dismiss();
            item.setEdited(true);
            FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
            new NotifyDataSetChangeAsync(mMainActivity).execute(adapter);
        }
    }


    private void deleteFeature() {
        final LayerList operationalLayers = mMapView.getMap().getOperationalLayers();
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn xóa sự cố này?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            dialog.dismiss();
            mSelectedArcGISFeature.loadAsync();

            // update the selected feature
            mSelectedArcGISFeature.addDoneLoadingListener(() -> {
                if (mSelectedArcGISFeature.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    Log.d(mMainActivity.getResources().getString(R.string.app_name), "Error while loading feature");
                }
                try {
                    for (final Layer layer : operationalLayers) {
                        if (layer instanceof FeatureLayer) {
                            final FeatureLayer featureLayer = (FeatureLayer) layer;
                            if (featureLayer.getId().equals("diemsucoLYR")) {
                                // update feature in the feature table
                                ListenableFuture<Void> mapViewResult = featureLayer.getFeatureTable().deleteFeatureAsync(mSelectedArcGISFeature);
                                mapViewResult.addDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
                                        // apply change to the server
                                        final ListenableFuture<List<FeatureEditResult>> serverResult =
                                                ((ServiceFeatureTable) featureLayer.getFeatureTable()).applyEditsAsync();
                                        serverResult.addDoneListener(() -> {

                                            List<FeatureEditResult> edits;
                                            try {
                                                edits = serverResult.get();
                                                if (edits.size() > 0) {
                                                    if (!edits.get(0).hasCompletedWithErrors()) {

                                                        Log.e("", "Feature successfully updated");
                                                    }
                                                }
                                                HoSoVatTuSuCoDB hoSoVatTuSuCoDB = new HoSoVatTuSuCoDB(mMainActivity);
                                                hoSoVatTuSuCoDB.delete(mIDSuCo);
                                                HoSoVatTuThuHoiSuCoDB hoSoVatTuThuHoiSuCoDB = new HoSoVatTuThuHoiSuCoDB(mMainActivity);
                                                hoSoVatTuThuHoiSuCoDB.delete(mIDSuCo);
                                            } catch (InterruptedException | ExecutionException e) {
                                                e.printStackTrace();
                                            }

                                        });
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(mMainActivity.getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
                }
            });
            if (mCallout != null) mCallout.dismiss();
        }).setNegativeButton("Không", (dialog, which) -> dialog.dismiss()).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }

    public void capture(boolean isAddFeature) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

        File photo = ImageFile.getFile(mMainActivity);
//        this.mUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", photo);
        Uri uri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mMainActivity.setSelectedArcGISFeature(mSelectedArcGISFeature);
        mMainActivity.setFeatureViewMoreInfoAdapter(mFeatureViewMoreInfoAdapter);
        mMainActivity.setUri(uri);
//        this.mUri = Uri.fromFile(photo);
        if (isAddFeature)
            mMainActivity.startActivityForResult(cameraIntent, mMainActivity.getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_ADD_FEATURE));
        else
            mMainActivity.startActivityForResult(cameraIntent, mMainActivity.getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_POPUP));
    }

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
        linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo).setOnClickListener(this);
        linearLayout.findViewById(R.id.imgBtn_cap_nhat_vat_tu).setOnClickListener(this::onClick);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = mApplication.getGeometry().getExtent();
        mMapView.setViewpointGeometryAsync(envelope, 0);
        // show CallOut
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        mCallout.show();
    }

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
            linearLayout.findViewById(R.id.imgBtn_cancel_timkiemdiachi).setOnClickListener(view -> mCallout.dismiss());


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

                            setOnClickListener(view -> mCallout.dismiss());
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_ViewMoreInfo:
                viewMoreInfo();
                break;
            case R.id.imgBtn_cap_nhat_vat_tu:
                Intent intent = new Intent(mMainActivity, VatTuActivity.class);
                mApplication.getDiemSuCo.setIdSuCo(mIDSuCo);
                mMainActivity.startActivity(intent);
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
