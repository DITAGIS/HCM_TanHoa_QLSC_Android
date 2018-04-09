package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;

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
    private QuanLySuCo mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private ServiceFeatureTable mServiceFeatureTable;
    private Callout mCallout;
    private Map<String, Object> mAttr;
    private BottomSheetDialog mBottomSheetDialog;
    private Dialog mDialogEdit;
    private EditAsync mEditAsync;

    public Popup(QuanLySuCo mainActivity, ServiceFeatureTable mServiceFeatureTable, Callout callout, BottomSheetDialog bottomSheetDialog) {
        this.mMainActivity = mainActivity;
        this.mServiceFeatureTable = mServiceFeatureTable;
        this.mCallout = callout;
        this.mBottomSheetDialog = bottomSheetDialog;
        this.mEditAsync = new EditAsync(mMainActivity);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public LinearLayout createPopup(final ArcGISFeature mSelectedArcGISFeature, final Map<String, Object> attr) {
        this.mSelectedArcGISFeature = mSelectedArcGISFeature;
//        LinearLayout linearLayout = new LinearLayout(this.mMainActivity.getApplicationContext());
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());//getLayoutInflater();
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
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity);//getLayoutInflater();
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_capnhatsuco, null);
        mDialogEdit = new Dialog(this.mMainActivity);
        mDialogEdit.setContentView(linearLayout);
        mDialogEdit.setCancelable(false);
        final Spinner spinTrangThai = mDialogEdit.findViewById(R.id.spin_trang_thai);
        final EditText editViTri = mDialogEdit.findViewById(R.id.edit_vi_tri_su_co);
        final Button btnNgayCapNhat = mDialogEdit.findViewById(R.id.btn_ngay_cap_nhat);
        final TextView txtNgayCapNhat = mDialogEdit.findViewById(R.id.txt_ngay_cap_nhat);
        btnNgayCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final View dialogView = View.inflate(mMainActivity, R.layout.date_time_picker, null);
//                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mMainActivity).create();
//
//                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Date date = Calendar.getInstance().getTime();
//                        date.toString();
////                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
////
////                        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
//
////                        String s = String.format("%02d", date.getDayOfMonth()) + "_" + String.format("%02d", (date.getMonth() + 1)) + "_" + date.getYear();
////
////                        btnNgayCapNhat.setText(s);
//                        alertDialog.dismiss();
//                    }
//                });
//                alertDialog.setView(dialogView);
//                alertDialog.show();
                Calendar calendar = Calendar.getInstance();
                txtNgayCapNhat.setText(Constant.DATE_FORMAT.format(calendar.getTime()));
//                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
//
//                        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

//                        String s = String.format("%02d", date.getDayOfMonth()) + "_" + String.format("%02d", (date.getMonth() + 1)) + "_" + date.getYear();
//
//                        btnNgayCapNhat.setText(s);
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
                            ((TextView) mDialogEdit.findViewById(R.id.txt_id_su_co)).setText(value.toString());
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
                            txtNgayCapNhat.setText(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                }
            }
        }
        ((ImageButton) mDialogEdit.findViewById(R.id.imgBtn_capnhatsuco_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditAsync.execute(editViTri.getText().toString(), spinTrangThai.getSelectedItemPosition() + "", txtNgayCapNhat.getText().toString());

            }
        });
        ((ImageButton) mDialogEdit.findViewById(R.id.imgBtn_capnhatsuco_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogEdit.dismiss();
            }
        });
        mDialogEdit.show();
    }

    private void viewMoreInfo_bottomsheet(ArcGISFeature mSelectedArcGISFeature, Map<String, Object> attr) {

        View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        View layout = mMainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);

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

    class EditAsync extends AsyncTask<String, Void, Void> {
        private ProgressDialog mDialog;
        private Context mContext;

        public EditAsync(Context context) {
            mContext = context;
            mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Đang xử lý...");
            mDialog.setCancelable(false);

            mDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            final String viTri = params[0];
            final short trangThai = Short.parseShort(params[1]);
            final String ngayCapNhat = params[2];

            mServiceFeatureTable.loadAsync();
            mServiceFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        mSelectedArcGISFeature.getAttributes().put(Constant.VI_TRI, viTri);
                        mSelectedArcGISFeature.getAttributes().put(Constant.TRANG_THAI, trangThai);
                        Date date = null;
                        date = Constant.DATE_FORMAT.parse(ngayCapNhat);
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        mSelectedArcGISFeature.getAttributes().put(Constant.NGAY_CAP_NHAT, c);
                        // update feature in the feature table
                        mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature).addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                mServiceFeatureTable.applyEditsAsync().addDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDialogEdit.dismiss();
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });

                    } catch (Exception e) {
                    }
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

}
