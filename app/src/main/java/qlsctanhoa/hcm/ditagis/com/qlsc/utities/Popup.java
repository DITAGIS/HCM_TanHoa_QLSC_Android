package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
                    case Constant.IDSU_CO:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_id_su_co)).setText(value.toString());
                        break;
                    case Constant.VI_TRI:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_vi_tri_su_co)).setText(value.toString());
                        break;
                    case Constant.TRANG_THAI:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_trang_thai)).setText(
                                    ((UniqueValueRenderer) this.mSelectedArcGISFeature
                                            .getFeatureTable()
                                            .getLayerInfo()
                                            .getDrawingInfo()
                                            .getRenderer())
                                            .getUniqueValues()
                                            .get(Integer.parseInt(value.toString()))
                                            .getLabel());
                        break;
                    case Constant.NGAY_CAP_NHAT:
                        if (value != null)
                            ((TextView) linearLayout.findViewById(R.id.txt_ngay_cap_nhat)).setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
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
//                viewMoreInfo_bottomsheet(mSelectedArcGISFeature, attr);
                viewMoreInfo(mSelectedArcGISFeature, attr);
            }
        });
        ((ImageButton) linearLayout.findViewById(R.id.imgBtn_Edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UpdateFeature();
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

    private void edit(final ArcGISFeature mSelectedArcGISFeature, final Map<String, Object> attr) {
        LayoutInflater inflater = LayoutInflater.from(this.mainActivity);//getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_capnhatsuco, null);
        final Dialog dialog = new Dialog(this.mainActivity);
        dialog.setContentView(linearLayout);
        dialog.setCancelable(false);
        final Spinner spinTrangThai = dialog.findViewById(R.id.spin_trang_thai);
        final EditText editViTri = dialog.findViewById(R.id.edit_vi_tri_su_co);
        final Button btnNgayCapNhat = dialog.findViewById(R.id.btn_ngay_cap_nhat);
        btnNgayCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final View dialogView = View.inflate(this,mainActivity, R.layout.date_time_picker, null);
//                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
//
//                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//
//                        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
//
//                        String s = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
//
//                        btnNgayCapNhat.setText(s);
//                        alertDialog.dismiss();
//                    }
//                });
//                alertDialog.setView(dialogView);
//                alertDialog.show();
            }
        });
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
//            LinearLayout layout = new LinearLayout(linearLayoutInfo.getContext());
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
                switch (field.getName()) {
                    case Constant.IDSU_CO:
                        if (value != null)
                            ((TextView) dialog.findViewById(R.id.txt_id_su_co)).setText(value.toString());
                        break;
                    case Constant.VI_TRI:
                        if (value != null)
                            editViTri.setText(value.toString());
                        break;
                    case Constant.TRANG_THAI:
                        if (value != null)
                            spinTrangThai.setSelection(Integer.parseInt(value.toString()));
                        break;
                    case Constant.NGAY_CAP_NHAT:
                        if (value != null)
                            btnNgayCapNhat.setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                }
            }
        }


        ((ImageButton) dialog.findViewById(R.id.imgBtn_capnhatsuco_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceFeatureTable.loadAsync();
                mServiceFeatureTable.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        mSelectedArcGISFeature.getAttributes().put(Constant.VI_TRI, editViTri.getText().toString());
                        mSelectedArcGISFeature.getAttributes().put(Constant.TRANG_THAI, spinTrangThai.getSelectedItemPosition());
                        try {
                            // update feature in the feature table
                            ListenableFuture<Void> mapViewResult = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
          /*mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(new Runnable() {*/
                            mapViewResult.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    // apply change to the server
                                    final ListenableFuture<List<FeatureEditResult>> serverResult = mServiceFeatureTable.applyEditsAsync();

                                    serverResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                dialog.dismiss();
                                                // check if server result successful
                                                List<FeatureEditResult> edits = serverResult.get();
                                                if (edits.size() > 0) {
                                                    if (!edits.get(0).hasCompletedWithErrors()) {
                                                    }
                                                } else {
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        ((ImageButton) dialog.findViewById(R.id.imgBtn_capnhatsuco_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void edit1(final ArcGISFeature mSelectedArcGISFeature, final Map<String, Object> attr) {
        LayoutInflater inflater = LayoutInflater.from(this.mainActivity);//getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_capnhatsuco, null);
        final Dialog dialog = new Dialog(this.mainActivity);
        dialog.setContentView(linearLayout);
        dialog.setCancelable(false);
        final Spinner spinTrangThai = dialog.findViewById(R.id.spin_trang_thai);
        final EditText editViTri = dialog.findViewById(R.id.edit_vi_tri_su_co);
        final Button btnNgayCapNhat = dialog.findViewById(R.id.btn_ngay_cap_nhat);
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
//            LinearLayout layout = new LinearLayout(linearLayoutInfo.getContext());
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
                switch (field.getName()) {
                    case Constant.IDSU_CO:
                        if (value != null)
                            ((TextView) dialog.findViewById(R.id.txt_id_su_co)).setText(value.toString());
                        break;
                    case Constant.VI_TRI:
                        if (value != null)
                            editViTri.setText(value.toString());
                        break;
                    case Constant.TRANG_THAI:
                        if (value != null)
                            spinTrangThai.setSelection(Integer.parseInt(value.toString()));
                        break;
                    case Constant.NGAY_CAP_NHAT:
                        if (value != null)
                            btnNgayCapNhat.setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                }
            }
        }


        ((ImageButton) dialog.findViewById(R.id.imgBtn_capnhatsuco_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceFeatureTable.loadAsync();
                mServiceFeatureTable.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSelectedArcGISFeature.getAttributes().put(Constant.VI_TRI, editViTri.getText().toString());
                            mSelectedArcGISFeature.getAttributes().put(Constant.TRANG_THAI, spinTrangThai.getSelectedItemPosition() + "");
                            Date date = null;
                            date = Constant.DATE_FORMAT.parse(btnNgayCapNhat.getText().toString());
                            Calendar c = Calendar.getInstance();
                            c.setTime(date);
//                            mSelectedArcGISFeature.getAttributes().put(Constant.NGAY_CAP_NHAT, c);

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
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                        } catch (ParseException e) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        ((ImageButton) dialog.findViewById(R.id.imgBtn_capnhatsuco_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void viewMoreInfo_bottomsheet(ArcGISFeature mSelectedArcGISFeature, Map<String, Object> attr) {

        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        LinearLayout layout_info = layout.findViewById(R.id.layout_alertdialog_info);
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
//                if (value != null) {
                if (field.getName().equals(Constant.IDSU_CO)) {
                    if (value != null)
                        ((TextView) layout.findViewById(R.id.txt_alertdialog_id_su_co)).setText(value.toString());
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

    private void viewMoreInfo(ArcGISFeature mSelectedArcGISFeature, Map<String, Object> attr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);

        TableLayout layout_info = layout.findViewById(R.id.layout_alertdialog_info);
        for (Field field : this.mSelectedArcGISFeature.getFeatureTable().getFields()) {
            if (field.getDomain() != null) {

            } else {
                Object value = attr.get(field.getName());
//                if (value != null) {
                if (field.getName().equals(Constant.IDSU_CO)) {
                    if (value != null)
                        ((TextView) layout.findViewById(R.id.txt_alertdialog_id_su_co)).setText(value.toString());
                } else {
                    TableRow row_layout = new TableRow(layout_info.getContext());
                    row_layout.setOrientation(LinearLayout.HORIZONTAL);
                    row_layout.setBackgroundResource(R.drawable.cell_shape);
                    TextView txtAlias = new TextView(layout_info.getContext());
                    txtAlias.setPadding(0, 0, 0, 0);
                    txtAlias.setWidth(400);
                    txtAlias.setText(field.getAlias() + ": ");
//                    txtAlias.setBackgroundResource(R.drawable.cell_shape);


                    TextView txtValue = new TextView(layout_info.getContext());
                    txtValue.setPadding(0, 0, 0, 0);
//                    txtValue.setBackgroundResource(R.drawable.cell_shape);
                    if (value != null)
                        switch (field.getFieldType()) {
                            case DATE:
                                txtValue.setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                                break;
                            case TEXT:
                                txtValue.setText(value.toString());
                                break;
                        }
                    row_layout.addView(txtAlias);
                    row_layout.addView(txtValue);

                    layout_info.addView(row_layout);
                }
//                }

            }
        }

        builder.setView(layout);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();


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
