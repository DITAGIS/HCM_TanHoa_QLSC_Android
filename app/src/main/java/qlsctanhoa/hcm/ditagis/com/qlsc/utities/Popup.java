package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import qlsctanhoa.hcm.ditagis.com.qlsc.QuanLySuCo;
import qlsctanhoa.hcm.ditagis.com.qlsc.R;


/**
 * Created by NGUYEN HONG on 1/31/2018.
 */

public class Popup extends AppCompatActivity {
    private QuanLySuCo mainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private Map<String, Object> mAttr;
    private BottomSheetDialog mBottomSheetDialog;

    public Popup(QuanLySuCo mainActivity, ServiceFeatureTable mServiceFeatureTable, Callout callout, BottomSheetDialog bottomSheetDialog) {
        this.mainActivity = mainActivity;
        this.mServiceFeatureTable = mServiceFeatureTable;
        this.mCallout = callout;
        this.mBottomSheetDialog = bottomSheetDialog;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public LinearLayout createPopup(final ArcGISFeature mSelectedArcGISFeature, final Map<String, Object> attr) {
        this.mSelectedArcGISFeature = mSelectedArcGISFeature;
//        LinearLayout linearLayout = new LinearLayout(this.mainActivity.getApplicationContext());
        LayoutInflater inflater = LayoutInflater.from(this.mainActivity.getApplicationContext());//getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_thongtinsuco, null);
//        LinearLayout linearLayoutInfo = linearLayout.findViewById(R.id.linearlayout_info);
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
//            LinearLayout layout = new LinearLayout(linearLayoutInfo.getContext());
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
                switch (field.getName()) {
                    case Constant.FEATURE_ATTRIBUTE_ID_SUCO:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_id_su_co)).setText(value.toString());
                        break;
                    case Constant.FEATURE_ATTRIBUTE_VITRI_SUCO:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_vi_tri_su_co)).setText(value.toString());
                        break;
                    case Constant.FEATURE_ATTRIBUTE_TRANGTHAI_SUCO:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_trang_thai)).setText(
                                    ((UniqueValueRenderer)this.mSelectedArcGISFeature
                                            .getFeatureTable()
                                            .getLayerInfo()
                                            .getDrawingInfo()
                                            .getRenderer())
                                            .getUniqueValues()
                                            .get(Integer.parseInt(value.toString()))
                                            .getLabel());
                        break;
                    case Constant.FEATURE_ATTRIBUTE_NGAYCAPNHAT_SUCO:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_ngay_cap_nhat)).setText(Constant.DATE_FORMAT.format(((Calendar)value).getTime()));
                        break;
                }
//                linearLayoutInfo.addView(layout);
//
//                TextView alias = new TextView(linearLayoutInfo.getContext());
//                alias.setPadding(0, 0, 10, 0);
//                alias.setText(field.getAlias());
//
//
//                TextView txtValue = new TextView(linearLayoutInfo.getContext());
//                txtValue.setPadding(0, 0, 10, 0);
//                Object value = attr.get(field.getAlias());
//                if (value == null)
//                    txtValue.setText("");
//                else
//                    txtValue.setText(attr.get(field.getAlias()).toString());
//
//                layout.addView(alias);
//                layout.addView(txtValue);
            }
        }
        if (mCallout != null)
            mCallout.dismiss();

        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMoreInfo(mSelectedArcGISFeature, attr);
            }
        });
        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_Edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit(mSelectedArcGISFeature, attr);
            }
        });
        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFeature();
            }
        });
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void edit(ArcGISFeature mSelectedArcGISFeature, Map<String, Object> attr) {
    }

    private void viewMoreInfo(ArcGISFeature mSelectedArcGISFeature, Map<String, Object> attr) {

        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        LinearLayout layout_info = layout.findViewById(R.id.layout_bs_info);
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
//                if (value != null) {
                if (field.getName().equals(Constant.FEATURE_ATTRIBUTE_ID_SUCO)) {
                    if (value != null)
                        ((TextView) layout.findViewById(R.id.txt_bs_id_su_co)).setText(value.toString());
                } else {
                    TextView tv = new TextView(layout_info.getContext());
                    tv.setPadding(0, 0, 0, 0);
                    if (value == null)
                        tv.setText(field.getAlias() + ":");
                    else
                        tv.setText(field.getAlias() + ": " + value.toString());
                    layout_info.addView(tv);
                }
//                }

            }
        }
        mBottomSheetDialog.setContentView(layout);
        mBottomSheetDialog.show();
    }

    private void deleteFeature() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
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
                                    ListenableFuture<List<FeatureEditResult>> serverResult = mServiceFeatureTable.applyEditsAsync();
                                    serverResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
                        }
                    }
                });
                if (mCallout != null)
                    mCallout.dismiss();
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

    private boolean UpdateFeature() {
        mSelectedArcGISFeature.loadAsync();

        // update the selected feature
        mSelectedArcGISFeature.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (mSelectedArcGISFeature.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                    Log.d(getResources().getString(R.string.app_name), "Error while loading feature");
                }

                // update the Attributes map with the new selected value for "typdamage"
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Cập nhật thông tin chỉ số");
                builder.setCancelable(false);
                LayoutInflater inflater = LayoutInflater.from(mainActivity);
                View dialogLayout = inflater.inflate(R.layout.layout_dialog_update_feature, null);

                List<String> attr = new ArrayList<String>(mAttr.keySet());
                final TextView txtMa = (TextView) dialogLayout.findViewById(R.id.txt_layout_edit_Ma);
                txtMa.setText(mAttr.get(attr.get(0)) == null ? "null" : mAttr.get(attr.get(0)).toString());
                final TextView txtObjectID = (TextView) dialogLayout.findViewById(R.id.txt_layout_qlds_objectID);
                txtObjectID.setText(mAttr.get(attr.get(1)) == null ? "null" : mAttr.get(attr.get(1)).toString());
                final EditText etxtTen = (EditText) dialogLayout.findViewById(R.id.etxt_layout_edit_Ten);
                etxtTen.setText(mAttr.get(attr.get(2)) == null ? "null" : mAttr.get(attr.get(2)).toString());
                builder.setView(dialogLayout)
                        .setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Lưu thay đổi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: lưu chỉnh sửa

                        mSelectedArcGISFeature.getAttributes().put("Ten", etxtTen.getText().toString());
                        try {
                            // update feature in the feature table
                            ListenableFuture<Void> mapViewResult = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
          /*mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(new Runnable() {*/
                            mapViewResult.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    // apply change to the server
                                    ListenableFuture<List<FeatureEditResult>> serverResult = mServiceFeatureTable.applyEditsAsync();

                                    serverResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "updating feature in the feature table failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();


            }
        });
        if (mCallout != null)
            mCallout.dismiss();
        return true;
    }

}
