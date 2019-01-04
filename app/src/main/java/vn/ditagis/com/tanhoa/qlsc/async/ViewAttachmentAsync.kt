package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ListView

import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters

import org.apache.commons.io.IOUtils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.MainActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAttachmentsAdapter
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.utities.DFile

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class ViewAttachmentAsync(private val mMainActivity: MainActivity) : AsyncTask<Void, Int?, Void?>() {
    private val mDialog: ProgressDialog?
    private var builder: AlertDialog.Builder? = null

    private var layout: View? = null
    private var lstViewAttachment: ListView? = null

    init {
        mDialog = ProgressDialog(mMainActivity, android.R.style.Theme_Material_Dialog_Alert)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog!!.setMessage(mMainActivity.getString(R.string.async_dang_lay_hinh_anh_dinh_kem))
        mDialog.setCancelable(false)

        mDialog.show()

        builder = AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        val layoutInflater = LayoutInflater.from(mMainActivity)
        layout = layoutInflater.inflate(R.layout.layout_viewmoreinfo_feature_attachment, null)
        lstViewAttachment = layout!!.findViewById(R.id.lstView_alertdialog_attachments)

    }

    override fun doInBackground(vararg params: Void): Void? {


        val attachmentsAdapter = FeatureViewMoreInfoAttachmentsAdapter(mMainActivity, ArrayList())
        lstViewAttachment!!.adapter = attachmentsAdapter

        val mApplication = mMainActivity.application as DApplication
        val queryClause = String.format("%s = '%s' and %s = '%s'",
                Constant.FieldSuCoThongTin.ID_SUCO, mApplication.arcGISFeature!!.attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString(),
                Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.userDangNhap!!.userName)
        val queryParameters = QueryParameters()
        queryParameters.whereClause = queryClause
        QueryServiceFeatureTableAsync(mMainActivity,
                (mMainActivity.application as DApplication).getDFeatureLayer.serviceFeatureTableSuCoThongTin!!,
                object : QueryServiceFeatureTableAsync.AsyncResponse {
                    override fun processFinish(output: Feature?) {
                        val arcGISFeature = output as ArcGISFeature
                        val attachmentResults = arcGISFeature.fetchAttachmentsAsync()
                        attachmentResults.addDoneListener {
                            try {

                                val attachments = attachmentResults.get()
                                // if selected feature has attachments, display them in a list fashion
                                if (!attachments.isEmpty()) {
                                    //
                                    for (attachment in attachments) {
                                        val item = FeatureViewMoreInfoAttachmentsAdapter.Item()
                                        item.name = attachment.name
                                        item.contentType = attachment.contentType
                                        val contentType = attachment.contentType.trim { it <= ' ' }.toLowerCase()
                                        if (contentType == Constant.FileType.PNG) {

                                            val inputStreamListenableFuture = attachment.fetchDataAsync()
                                            inputStreamListenableFuture.addDoneListener {
                                                try {
                                                    val inputStream = inputStreamListenableFuture.get()
                                                    item.img = IOUtils.toByteArray(inputStream)
                                                    attachmentsAdapter.add(item)
                                                    attachmentsAdapter.notifyDataSetChanged()
                                                    //Kiểm tra nếu adapter có phần tử và attachment là phần tử cuối cùng thì show dialog

                                                    if (attachments.size == attachmentsAdapter.count)
                                                        publishProgress(0)

                                                } catch (e: InterruptedException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                } catch (e: ExecutionException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                }
                                            }

                                        } else {
                                            val inputStreamListenableFuture = attachment.fetchDataAsync()
                                            inputStreamListenableFuture.addDoneListener {
                                                try {
                                                    val inputStream = inputStreamListenableFuture.get()
                                                    var f: File? = null
                                                    if (contentType == Constant.FileType.PDF) {
                                                        f = DFile.getPDFFile(mMainActivity, attachment.name)
                                                    } else if (contentType == Constant.FileType.DOC) {
                                                        f = DFile.getDocFile(mMainActivity, attachment.name)
                                                    }
                                                    if (f != null) {
                                                        val oos = FileOutputStream(f, true)

                                                        val buf = ByteArray(8192)


                                                        var c: Int

                                                        while (true) {
                                                            c = inputStream.read(buf, 0, buf.size)
                                                            if (c <= 0)
                                                                break
                                                            oos.write(buf, 0, c)
                                                            oos.flush()
                                                        }
                                                        item.url = f.path
                                                        attachmentsAdapter.add(item)
                                                        attachmentsAdapter.notifyDataSetChanged()
                                                        oos.close()
                                                        println("stop")
                                                        inputStream.close()
                                                    }
                                                    if (attachments.size == attachmentsAdapter.count)
                                                        publishProgress(0)

                                                } catch (e: InterruptedException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                } catch (e: ExecutionException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                    publishProgress(0)
                                                }
                                            }

                                        }
                                    }

                                } else {
                                    publishProgress(0)
                                    //                        MySnackBar.make(mCallout, "Không có file hình ảnh đính kèm", true);
                                }

                            } catch (e: Exception) {
                                Log.e("ERROR", e.message)
                                publishProgress(0)
                            }
                        }
                    }

                }).execute(queryParameters)

        return null
    }


     override fun onProgressUpdate(vararg values: Int?) {
        if (values[0] == 0) {
            //            if (mDialog != null && mDialog.isShowing()) {
            //                mDialog.dismiss();
            //            }
            //        } else if (values[0] == -1) {
            if (mDialog != null && mDialog.isShowing) {
                mDialog.dismiss()
                lstViewAttachment!!.setOnItemClickListener { adapterView, _, i, _ ->
                    val item = adapterView.getItemAtPosition(i) as FeatureViewMoreInfoAttachmentsAdapter.Item
                    if (item.url != null) {

                        val pdfOpenintent = Intent(Intent.ACTION_VIEW)
                        pdfOpenintent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        if (item.contentType == Constant.FileType.PDF) {
                            val file = DFile.getPDFFile(mMainActivity, item.name!!)
                            val path = Uri.fromFile(file)
                            pdfOpenintent.setDataAndType(path, Constant.FileType.PDF)
                        } else if (item.contentType == Constant.FileType.DOC) {
                            val file = DFile.getDocFile(mMainActivity, item.name!!)
                            val path = Uri.fromFile(file)
                            pdfOpenintent.setDataAndType(path, Constant.FileType.DOC)
                        }
                        try {
                            mMainActivity.startActivity(pdfOpenintent)
                        } catch (ignored: ActivityNotFoundException) {

                        }

                    }
                }
                builder!!.setView(layout)
                builder!!.setCancelable(false)
                builder!!.setPositiveButton("Thoát") { dialog, _ -> dialog.dismiss() }
                val dialog = builder!!.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialog.show()
            }
        }
        super.onProgressUpdate(*values)

    }

}

