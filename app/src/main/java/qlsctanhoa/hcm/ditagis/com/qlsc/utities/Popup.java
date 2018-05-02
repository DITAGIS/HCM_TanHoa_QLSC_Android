package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.ImageButton;
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
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.QuanLySuCo;
import qlsctanhoa.hcm.ditagis.com.qlsc.R;
import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.FeatureViewMoreInfoAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.async.EditAsync;
import qlsctanhoa.hcm.ditagis.com.qlsc.async.NotifyDataSetChangeAsync;
import qlsctanhoa.hcm.ditagis.com.qlsc.libs.FeatureLayerDTG;

public class Popup extends AppCompatActivity {
    private QuanLySuCo mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private FeatureLayerDTG mFeatureLayerDTG;
    private List<String> lstFeatureType;
    private LinearLayout linearLayout;

    public Popup(QuanLySuCo mainActivity, ServiceFeatureTable mServiceFeatureTable, Callout callout) {
        this.mMainActivity = mainActivity;
        this.mServiceFeatureTable = mServiceFeatureTable;
        this.mCallout = callout;

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setFeatureLayerDTG(FeatureLayerDTG layerDTG) {
        this.mFeatureLayerDTG = layerDTG;
    }

    private void refressPopup() {
        Map<String, Object> attributes = mSelectedArcGISFeature.getAttributes();
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attributes.get(field.getName());
            switch (field.getName()) {
                case Constant.IDSU_CO:
                    if (value != null)
                        ((TextView) linearLayout.findViewById(R.id.txt_id_su_co)).setText(value.toString());
                    break;
                case Constant.VI_TRI:
                    if (value != null)
                        ((TextView) linearLayout.findViewById(R.id.txt_vi_tri_su_co)).setText(value.toString());
                    break;
                case Constant.TRANG_THAI:
                    if (value != null) {
                        List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                        String valueFeatureType = getValueFeatureType(featureTypes, value.toString()).toString();
                        if (valueFeatureType != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_trang_thai)).setText(valueFeatureType);
                    }
                    break;
                case Constant.NGAY_CAP_NHAT:
                    if (value != null)
                        ((TextView) linearLayout.findViewById(R.id.txt_ngay_cap_nhat)).
                                setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                    break;
            }
        }
    }

    public LinearLayout createPopup(final ArcGISFeature mSelectedArcGISFeature) {
        this.mSelectedArcGISFeature = mSelectedArcGISFeature;
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(mSelectedArcGISFeature.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_thongtinsuco, null);
        refressPopup();
        if (mCallout != null) mCallout.dismiss();

        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMoreInfo();
            }
        });

        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedArcGISFeature.getFeatureTable().getFeatureLayer().clearSelection();
                deleteFeature();
            }
        });
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void viewMoreInfo() {
        Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        final FeatureViewMoreInfoAdapter adapter = new FeatureViewMoreInfoAdapter(mMainActivity,
                new ArrayList<FeatureViewMoreInfoAdapter.Item>());
        final ListView lstView = layout.findViewById(R.id.lstView_alertdialog_info);
        lstView.setAdapter(adapter);
        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit(parent, view, position, id);
            }
        });
        String[] updateFields = mFeatureLayerDTG.getUpdateFields();
        String typeIdField = mSelectedArcGISFeature.getFeatureTable().getTypeIdField();
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            Object value = attr.get(field.getName());
            if (field.getName().equals(Constant.IDSU_CO)) {
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
                        List<CodedValue> codedValues = ((CodedValueDomain)
                                this.mSelectedArcGISFeature.getFeatureTable()
                                        .getField(item.getFieldName()).getDomain()).getCodedValues();
                        String valueDomain = getValueDomain(codedValues, value.toString()).toString();
                        if (valueDomain != null) item.setValue(valueDomain);
                    } else switch (field.getFieldType()) {
                        case DATE:
                            item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                            break;
                        case OID:
                        case TEXT:
                            item.setValue(value.toString());
                            break;
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
                adapter.add(item);
                adapter.notifyDataSetChanged();
            }
        }
        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
                builder.setMessage("Bạn có muốn cập nhật tất cả thay đổi?");
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditAsync editAsync = new EditAsync(mMainActivity, mServiceFeatureTable, mSelectedArcGISFeature);
                        try {
                            Void aVoid = editAsync.execute(adapter).get();
                            refressPopup();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                alertDialog.show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();


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

    private void edit(final AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) instanceof FeatureViewMoreInfoAdapter.Item) {
            final FeatureViewMoreInfoAdapter.Item item = (FeatureViewMoreInfoAdapter.Item) parent.getItemAtPosition(position);
            if (item.isEdit()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity,
                        android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Cập nhật thuộc tính");
                builder.setMessage(item.getAlias());
                builder.setCancelable(false).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final LinearLayout layout = (LinearLayout) mMainActivity.getLayoutInflater().
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(),
                            android.R.layout.simple_list_item_1, lstFeatureType);
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
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
                                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                                datePicker.getMonth(), datePicker.getDayOfMonth());
                                        String s = String.format("%02d_%02d_%d",
                                                datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());

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
                                        item.setValue(editText.getText().toString());
                                    } catch (Exception e) {
                                        Toast.makeText(mMainActivity, "Số liệu nhập vào không đúng định dạng!!!", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case TEXT:
                                case SHORT:
                                    try {
                                        short x = Short.parseShort(editText.getText().toString());
                                        item.setValue(editText.getText().toString());
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
                            Log.d(getResources().getString(R.string.app_name), "Error while loading feature");
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
                                            List<FeatureEditResult> edits = null;
                                            try {
                                                edits = serverResult.get();
                                                if (edits.size() > 0) {
                                                    if (!edits.get(0).hasCompletedWithErrors()) {
                                                        Log.e("", "Feature successfully updated");
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
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

}
