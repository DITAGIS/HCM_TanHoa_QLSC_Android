package hcm.ditagis.com.tanhoa.qlsc.utities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
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
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.QuanLySuCo;
import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.adapter.FeatureViewInfoAdapter;
import hcm.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter;
import hcm.ditagis.com.tanhoa.qlsc.async.EditAsync;
import hcm.ditagis.com.tanhoa.qlsc.async.FindLocationAsycn;
import hcm.ditagis.com.tanhoa.qlsc.async.NotifyDataSetChangeAsync;
import hcm.ditagis.com.tanhoa.qlsc.async.ViewAttachmentAsync;
import hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB.KhachHang;
import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;

@SuppressLint("Registered")
public class Popup extends AppCompatActivity implements View.OnClickListener {
    private List<String> mListDMA;
    private QuanLySuCo mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private FeatureLayerDTG mFeatureLayerDTG;
    private List<String> lstFeatureType;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 44;
    private FeatureViewMoreInfoAdapter mFeatureViewMoreInfoAdapter;
    private DialogInterface mDialog;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private LocationDisplay mLocationDisplay;

    public DialogInterface getDialog() {
        return mDialog;
    }

    public void setmListDMA(List<String> mListDMA) {
        this.mListDMA = mListDMA;
    }

    public Popup(QuanLySuCo mainActivity, MapView mapView, ServiceFeatureTable serviceFeatureTable, Callout callout, LocationDisplay locationDisplay) {
        this.mMainActivity = mainActivity;
        this.mMapView = mapView;
        this.mServiceFeatureTable = serviceFeatureTable;
        this.mCallout = callout;
        this.mLocationDisplay = locationDisplay;
    }

    public void setFeatureLayerDTG(FeatureLayerDTG layerDTG) {
        this.mFeatureLayerDTG = layerDTG;
    }

    private void refreshPopup() {
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(mMainActivity, new ArrayList<FeatureViewInfoAdapter.Item>());
        listView.setAdapter(featureViewInfoAdapter);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        String[] noDisplayFields = mMainActivity.getResources().getStringArray(R.array.no_display_fields_arrays);
        boolean isFoundField = false;
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            for (String noDisplayField : noDisplayFields)
                if (noDisplayField.equals(field.getName())) {
                    isFoundField = true;
                    break;
                }
            if (isFoundField) {
                isFoundField = false;
                continue;
            }
            Object value = attributes.get(field.getName());
            if (value != null) {
                FeatureViewInfoAdapter.Item item = new FeatureViewInfoAdapter.Item();

                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                if (item.getFieldName().equals(typeIdField)) {
                    List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                    String valueFeatureType = getValueFeatureType(featureTypes, value.toString()).toString();
                    if (valueFeatureType != null) item.setValue(valueFeatureType);
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                    Object valueDomainObject = getValueDomain(codedValues, value.toString());
                    if (valueDomainObject != null) item.setValue(valueDomainObject.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
                        item.setValue(Constant.DATE_FORMAT_VIEW.format(((Calendar) value).getTime()));
                        break;
                    case OID:
                    case TEXT:
                        item.setValue(value.toString());
                        break;
                    case SHORT:
                    case DOUBLE:
                    case INTEGER:
                    case FLOAT:
                        item.setValue(value.toString());
                        break;
                }
                featureViewInfoAdapter.add(item);
                featureViewInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void viewMoreInfo(boolean isAddFeature) {
        Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        @SuppressLint("InflateParams") View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        mFeatureViewMoreInfoAdapter = new FeatureViewMoreInfoAdapter(mMainActivity, new ArrayList<FeatureViewMoreInfoAdapter.Item>());
        final ListView lstViewInfo = layout.findViewById(R.id.lstView_alertdialog_info);
        layout.findViewById(R.id.layout_viewmoreinfo_id_su_co).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.framelayout_viewmoreinfo_attachment).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.framelayout_viewmoreinfo_attachment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAttachment();
            }
        });

        lstViewInfo.setAdapter(mFeatureViewMoreInfoAdapter);
        lstViewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit(parent, position);
            }
        });

        String[] updateFields = mFeatureLayerDTG.getUpdateFields();
        String[] addFields = mMainActivity.getResources().getStringArray(R.array.add_fields_arrays);
        String[] no_displayFields = mMainActivity.getResources().getStringArray(R.array.no_display_fields_arrays);
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        boolean isFoundContinue = false;
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attr.get(field.getName());
            for (String noDisplayField : no_displayFields)
                if (field.getName().equals(noDisplayField)) {
                    isFoundContinue = true;
                    break;
                }
            if (isFoundContinue) {
                isFoundContinue = false;
                continue;
            }
            if (isAddFeature) {
                isFoundContinue = true;
                for (String addField : addFields)
                    if (field.getName().equals(addField)) {
                        isFoundContinue = false;
                        break;
                    }
            }
            if (isFoundContinue) {
                isFoundContinue = false;
                continue;
            }
            if (field.getName().equals(mMainActivity.getString(R.string.IDSuCo))) {
                if (value != null)
                    ((TextView) layout.findViewById(R.id.txt_alertdialog_id_su_co)).setText(value.toString());
            } else {
                FeatureViewMoreInfoAdapter.Item item = new FeatureViewMoreInfoAdapter.Item();
                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                if (value != null) {
                    if (item.getFieldName().equals(typeIdField)) {
                        List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                        String valueFeatureType = getValueFeatureType(featureTypes, value.toString()).toString();
                        if (valueFeatureType != null) item.setValue(valueFeatureType);
                    } else if (field.getDomain() != null) {
                        List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                        String valueDomain = getValueDomain(codedValues, value.toString()).toString();
                        if (valueDomain != null) item.setValue(valueDomain);
                    } else switch (field.getFieldType()) {
                        case DATE:
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
                item.setEdit(false);
                for (String updateField : updateFields) {
                    if (item.getFieldName().equals(updateField)) {
                        item.setEdit(true);
                        break;
                    }
                }
                item.setFieldType(field.getFieldType());
                mFeatureViewMoreInfoAdapter.add(item);
                mFeatureViewMoreInfoAdapter.notifyDataSetChanged();
            }
        }

        builder.setView(layout);
        builder.setCancelable(false);

        if (isAddFeature) {
            builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener()

            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditAsync editAsync = new EditAsync(mMainActivity, (ServiceFeatureTable) mFeatureLayerDTG.getFeatureLayer().getFeatureTable(), mSelectedArcGISFeature, true, null);
                    editAsync.execute(mFeatureViewMoreInfoAdapter);
                    dialog.dismiss();
                }
            }).setNegativeButton("Chụp ảnh và lưu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    capture();
                    mDialog = dialog;
                    refreshPopup();
                }
            });
        } else {
            builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener()

            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setNegativeButton("Chụp ảnh và cập nhật", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    capture();
                    mDialog = dialog;
                    refreshPopup();
                }
            });
        }
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
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

    private void edit(final AdapterView<?> parent, int position) {
        if (parent.getItemAtPosition(position) instanceof FeatureViewMoreInfoAdapter.Item) {
            final FeatureViewMoreInfoAdapter.Item item = (FeatureViewMoreInfoAdapter.Item) parent.getItemAtPosition(position);
            if (item.isEdit()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Cập nhật thuộc tính");
                builder.setMessage(item.getAlias());
                builder.setCancelable(false).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                @SuppressLint("InflateParams") final LinearLayout layout = (LinearLayout) mMainActivity.getLayoutInflater().
                        inflate(R.layout.layout_dialog_update_feature_listview, null);
                builder.setView(layout);
                final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
                final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
                final Button button = layout.findViewById(R.id.btn_edit_viewmoreinfo);
                final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
                final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
                final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
                final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

                final Domain domain = mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
                if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField())) {
                    layoutSpin.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, lstFeatureType);
                    spin.setAdapter(adapter);
                    if (item.getValue() != null)
                        spin.setSelection(lstFeatureType.indexOf(item.getValue()));
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

                    }
                } else switch (item.getFieldType()) {
                    case DATE:
                        layoutTextView.setVisibility(View.VISIBLE);
                        textView.setText(item.getValue());
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final View dialogView = View.inflate(mMainActivity, R.layout.date_time_picker, null);
                                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mMainActivity).create();
                                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                                        String s = String.format(getString(R.string.format_date_month_year), datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());

                                        textView.setText(s);
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.setView(dialogView);
                                alertDialog.show();
                            }
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
                builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField()) || (domain != null)) {
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


                        dialog.dismiss();
                        FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
                        new NotifyDataSetChangeAsync(mMainActivity).execute(adapter);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();

            }
        }

    }

    private void deleteFeature() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có chắc chắn xóa sự cố này?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSelectedArcGISFeature.loadAsync();

                // update the selected feature
                mSelectedArcGISFeature.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if (mSelectedArcGISFeature.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                            Log.d(mMainActivity.getResources().getString(R.string.app_name), "Error while loading feature");
                        }
                        try {
                            // update feature in the feature table
                            ListenableFuture<Void> mapViewResult = mServiceFeatureTable.deleteFeatureAsync(mSelectedArcGISFeature);
                            mapViewResult.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    // apply change to the server
                                    final ListenableFuture<List<FeatureEditResult>> serverResult = mServiceFeatureTable.applyEditsAsync();
                                    serverResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<FeatureEditResult> edits;
                                            try {
                                                edits = serverResult.get();
                                                if (edits.size() > 0) {
                                                    if (!edits.get(0).hasCompletedWithErrors()) {
                                                        Log.e("", "Feature successfully updated");
                                                    }
                                                }
                                            } catch (InterruptedException | ExecutionException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Log.e(mMainActivity.getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
                        }
                    }
                });
                if (mCallout != null) mCallout.dismiss();
            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }

    public void capture() {
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
        mMainActivity.startActivityForResult(cameraIntent, REQUEST_ID_IMAGE_CAPTURE);

    }

    private void clearSelection() {
        if (mFeatureLayerDTG != null) {
            FeatureLayer featureLayer = mFeatureLayerDTG.getFeatureLayer();
            featureLayer.clearSelection();
        }
    }

    private void dimissCallout() {
        if (mCallout != null && mCallout.isShowing()) {
            mCallout.dismiss();
        }
    }

    @SuppressLint("InflateParams")
    public void showPopup(final ArcGISFeature mSelectedArcGISFeature, final boolean isAddFeature) {
        clearSelection();
        dimissCallout();
        this.mSelectedArcGISFeature = mSelectedArcGISFeature;
        FeatureLayer featureLayer = mFeatureLayerDTG.getFeatureLayer();
        featureLayer.selectFeature(mSelectedArcGISFeature);
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_thongtinsuco, null);
        refreshPopup();
        ((TextView) linearLayout.findViewById(R.id.txt_thongtin_ten)).setText(featureLayer.getName());
        linearLayout.findViewById(R.id.imgBtn_layout_thongtinsuco).setOnClickListener(this);
        if (featureLayer.getName().equals(mMainActivity.getString(R.string.ALIAS_DIEM_SU_CO))) {
            //user admin mới có quyền xóa
            if (KhachHang.khachHangDangNhap.getUserName().equals("admin")) {
                linearLayout.findViewById(R.id.imgBtn_delete).setOnClickListener(this);
            } else {
                linearLayout.findViewById(R.id.imgBtn_delete).setVisibility(View.GONE);
            }

            //khi hoàn thành rồi thì không chỉnh sửa được
            if (Integer.parseInt(mSelectedArcGISFeature.getAttributes().get(mMainActivity.getString(R.string.TrangThai)).toString())
                    != mMainActivity.getResources().getInteger(R.integer.trang_thai_hoan_thanh))
                linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo).setOnClickListener(this);
        } else {
            linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo).setVisibility(View.INVISIBLE);
            linearLayout.findViewById(R.id.imgBtn_delete).setVisibility(View.INVISIBLE);
        }

        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = mSelectedArcGISFeature.getGeometry().getExtent();
        mMapView.setViewpointGeometryAsync(envelope, 0);
        // show CallOut
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallout.refresh();
                mCallout.show();
                if (isAddFeature)
                    viewMoreInfo(true);
            }
        });

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
            linearLayout.findViewById(R.id.imgBtn_timkiemdiachi).setOnClickListener(this);


            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // show CallOut
            mCallout.setLocation(position);
            mCallout.setContent(linearLayout);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallout.refresh();
                    mCallout.show();
                }
            });
        } catch (Exception e) {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }

    public void showPopupFindLocation(final Point position) {
        try {
            if (position == null)
                return;

            FindLocationAsycn findLocationAsycn = new FindLocationAsycn(mMainActivity, false, new FindLocationAsycn.AsyncResponse() {
                @SuppressLint("InflateParams")
                @Override
                public void processFinish(List<Address> output) {
                    if (output != null && output.size() > 0) {
                        clearSelection();
                        dimissCallout();
                        Address address = output.get(0);
                        String addressLine = address.getAddressLine(0);
                        LayoutInflater inflater = LayoutInflater.from(mMainActivity.getApplicationContext());
                        linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_timkiemdiachi, null);
                        ((TextView) linearLayout.findViewById(R.id.txt_timkiemdiachi)).setText(addressLine);
                        linearLayout.findViewById(R.id.imgBtn_timkiemdiachi_themdiemsuco).setOnClickListener(Popup.this);
                        linearLayout.findViewById(R.id.imgBtn_timkiemdiachi).setOnClickListener(Popup.this);
                        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        // show CallOut
                        mCallout.setLocation(position);
                        mCallout.setContent(linearLayout);
                        Popup.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCallout.refresh();
                                mCallout.show();
                            }
                        });
                    }
                }
            });
            Geometry project = GeometryEngine.project(position, SpatialReferences.getWgs84());
            double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
            findLocationAsycn.setmLongtitude(location[0]);
            findLocationAsycn.setmLatitude(location[1]);
            findLocationAsycn.execute();
        } catch (Exception e) {
            Log.e("Popup tìm kiếm", e.toString());
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_layout_thongtinsuco:
            case R.id.imgBtn_timkiemdiachi:
                if (mCallout != null && mCallout.isShowing())
                    mCallout.dismiss();
                break;
            case R.id.imgBtn_ViewMoreInfo:
                viewMoreInfo(false);
                break;
            case R.id.imgBtn_delete:
                mSelectedArcGISFeature.getFeatureTable().getFeatureLayer().clearSelection();
                deleteFeature();
                break;
            case R.id.imgBtn_timkiemdiachi_themdiemsuco:
                mMainActivity.onClick(view);
                break;
        }
    }
}
