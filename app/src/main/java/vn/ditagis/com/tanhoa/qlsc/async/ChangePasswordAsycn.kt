package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log

import org.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

class ChangePasswordAsycn(@SuppressLint("StaticFieldLeak") private val mActivity: Activity, //    private Dialog mDialog;
                          private val mDelegate: AsyncResponse) : AsyncTask<String, Void, Boolean?>() {
    private val mApplication: DApplication = mActivity.application as DApplication

    interface AsyncResponse {
        fun processFinish(output: Boolean?)
    }

    //    override fun onPreExecute() {
//        super.onPreExecute()
//        //        LinearLayout layout = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_dialog, null);
//        //        TextView txtTitle = layout.findViewById(R.id.txt_progress_dialog_title);
//        //        TextView txtMessage = layout.findViewById(R.id.txt_progress_dialog_message);
//        //        txtTitle.setText("Vui lòng đợi");
//        //        txtMessage.setText(mActivity.getApplicationContext().getString(R.string.change_password_message));
//        //        this.mDialog = new Dialog(mActivity);
//        //        this.mDialog.setContentView(layout);
//        //        this.mDialog.setCancelable(false);
//        //        this.mDialog.show();
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun doInBackground(vararg params: String): Boolean? {
        val pin = params[0]
        val newPin = params[1]
        try {
            val apiURL = Constant.UrlApi.CHANGE_PASSWORD
            val url = URL(apiURL)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = true
                conn.instanceFollowRedirects = false
                conn.requestMethod = Constant.HTTPRequest.POST_METHOD

                val cred = JSONObject()
                cred.put("OldPassword", pin)
                cred.put("NewPassword", newPin)


                conn.setRequestProperty(Constant.HTTPRequest.AUTHORIZATION, mApplication.userDangNhap!!.token)
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.useCaches = false
                val wr = OutputStreamWriter(conn.outputStream)
                wr.write(cred.toString())
                wr.flush()

                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val builder = StringBuilder()
                var line: String
                while (true) {
                    line = bufferedReader.readLine()
                    if (line == null)
                        break
                    builder.append(line)
                }
                return builder.toString().contains("true")
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("Lỗi đổi mật khẩu", e.toString())
        }

        return false
    }

    override fun onPostExecute(value: Boolean?) {
        //        if (khachHang != null) {
        //        mDialog.dismiss();
        this.mDelegate.processFinish(value)
        //        }
    }
}
