package vn.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.QueryParameters;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.MainActivity;
import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAttachmentsAdapter;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.utities.DFile;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class ViewAttachmentAsync extends AsyncTask<Void, Integer, Void> {
    private ProgressDialog mDialog;
    private MainActivity mMainActivity;
    private AlertDialog.Builder builder;
    private View layout;
    private ListView lstViewAttachment;

    public ViewAttachmentAsync(MainActivity context) {
        mMainActivity = context;
        mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mMainActivity.getString(R.string.async_dang_lay_hinh_anh_dinh_kem));
        mDialog.setCancelable(false);

        mDialog.show();

        builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        LayoutInflater layoutInflater = LayoutInflater.from(mMainActivity);
        layout = layoutInflater.inflate(R.layout.layout_viewmoreinfo_feature_attachment, null);
        lstViewAttachment = layout.findViewById(R.id.lstView_alertdialog_attachments);

    }

    @Override
    protected Void doInBackground(Void... params) {


        final FeatureViewMoreInfoAttachmentsAdapter attachmentsAdapter = new FeatureViewMoreInfoAttachmentsAdapter(mMainActivity, new ArrayList<FeatureViewMoreInfoAttachmentsAdapter.Item>());
        lstViewAttachment.setAdapter(attachmentsAdapter);

        DApplication mApplication = (DApplication) mMainActivity.getApplication();
        String queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString(),
                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(queryClause);
        new QueryServiceFeatureTableAsync(mMainActivity,
                ((DApplication) mMainActivity.getApplication()).getDFeatureLayer.getServiceFeatureTableSuCoThonTin(), output -> {
            ArcGISFeature arcGISFeature = (ArcGISFeature) output;
            final ListenableFuture<List<Attachment>> attachmentResults = arcGISFeature.fetchAttachmentsAsync();
            attachmentResults.addDoneListener(() -> {
                try {

                    final List<Attachment> attachments = attachmentResults.get();
                    // if selected feature has attachments, display them in a list fashion
                    if (!attachments.isEmpty()) {
                        //
                        for (final Attachment attachment : attachments) {
                            final FeatureViewMoreInfoAttachmentsAdapter.Item item = new FeatureViewMoreInfoAttachmentsAdapter.Item();
                            item.setName(attachment.getName());
                            item.setContentType(attachment.getContentType());
                            String contentType = attachment.getContentType().trim().toLowerCase();
                            if (contentType.contains("png")) {

                                final ListenableFuture<InputStream> inputStreamListenableFuture = attachment.fetchDataAsync();
                                inputStreamListenableFuture.addDoneListener(() -> {
                                    try {
                                        InputStream inputStream = inputStreamListenableFuture.get();
                                        item.setImg(IOUtils.toByteArray(inputStream));
                                        attachmentsAdapter.add(item);
                                        attachmentsAdapter.notifyDataSetChanged();
                                        //Kiểm tra nếu adapter có phần tử và attachment là phần tử cuối cùng thì show dialog

                                        if (attachments.size() == attachmentsAdapter.getCount())
                                            publishProgress(0);

                                    } catch (InterruptedException | ExecutionException | IOException e) {
                                        e.printStackTrace();
                                        publishProgress(0);
                                    }
                                });

                            } else {
                                final ListenableFuture<InputStream> inputStreamListenableFuture = attachment.fetchDataAsync();
                                inputStreamListenableFuture.addDoneListener(() -> {
                                    try {
                                        InputStream inputStream = inputStreamListenableFuture.get();
                                        File f = null;
                                        if (contentType.equals(Constant.FILE_TYPE.PDF)) {
                                            f = DFile.getPDFFile(mMainActivity, attachment.getName());
                                        } else if (contentType.equals(Constant.FILE_TYPE.DOC)) {
                                            f = DFile.getDocFile(mMainActivity, attachment.getName());
                                        }
                                        if (f != null) {
                                            OutputStream oos = new FileOutputStream(f, true);

                                            byte[] buf = new byte[8192];


                                            int c = 0;

                                            while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                                                oos.write(buf, 0, c);
                                                oos.flush();
                                            }

                                            item.setUrl(f.getPath());
                                            attachmentsAdapter.add(item);
                                            attachmentsAdapter.notifyDataSetChanged();
                                            oos.close();
                                            System.out.println("stop");
                                            inputStream.close();
                                        }
                                        if (attachments.size() == attachmentsAdapter.getCount())
                                            publishProgress(0);

                                    } catch (InterruptedException | ExecutionException | IOException e) {
                                        e.printStackTrace();
                                        publishProgress(0);
                                    }
                                });

                            }
                        }

                    } else {
                        publishProgress(0);
//                        MySnackBar.make(mCallout, "Không có file hình ảnh đính kèm", true);
                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                    publishProgress(0);
                }
            });
        }).execute(queryParameters);

        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == 0) {
//            if (mDialog != null && mDialog.isShowing()) {
//                mDialog.dismiss();
//            }
//        } else if (values[0] == -1) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                lstViewAttachment.setOnItemClickListener((adapterView, view, i, l) -> {
                    FeatureViewMoreInfoAttachmentsAdapter.Item item = (FeatureViewMoreInfoAttachmentsAdapter.Item)
                            adapterView.getItemAtPosition(i);
                    if (item.getUrl() != null) {

                        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (item.getContentType().equals(Constant.FILE_TYPE.PDF)) {
                            File file = DFile.getPDFFile(mMainActivity, item.getName());
                            Uri path = Uri.fromFile(file);
                            pdfOpenintent.setDataAndType(path, Constant.FILE_TYPE.PDF);
                        } else if (item.getContentType().equals(Constant.FILE_TYPE.DOC)) {
                            File file = DFile.getDocFile(mMainActivity, item.getName());
                            Uri path = Uri.fromFile(file);
                            pdfOpenintent.setDataAndType(path, Constant.FILE_TYPE.DOC);
                        }
                        try {
                            mMainActivity.startActivity(pdfOpenintent);
                        } catch (ActivityNotFoundException ignored) {

                        }
                    }
                });
                builder.setView(layout);
                builder.setCancelable(false);
                builder.setPositiveButton("Thoát", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();
            }
        }
        super.

                onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

