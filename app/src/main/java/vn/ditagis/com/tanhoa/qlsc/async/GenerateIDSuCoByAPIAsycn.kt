package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import vn.ditagis.com.tanhoa.qlsc.entities.DApplication


class GenerateIDSuCoByAPIAsycn internal constructor(@field:SuppressLint("StaticFieldLeak")
                                                    private val mActivity: Activity, private val mDelegate: AsyncResponse)
    : AsyncTask<String, Void, String?>() {
    private val mApplication: DApplication = mActivity.application as DApplication

    interface AsyncResponse {
        fun processFinish(output: String?)
    }

    override fun doInBackground(vararg params: String): String? {
        //Tránh gặp lỗi networkOnMainThread nên phải dùng asyncTask
        var id = ""
        try {
            val url = URL(params[0])
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", mApplication.userDangNhap!!.token)
                conn.connect()
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val line = bufferedReader.readLine()
                id = line.replace("\"", "")
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            publishProgress()
            Log.e("Lỗi lấy IDSuCo", e.toString())
        }

        return id
    }

    override fun onPostExecute(value: String?) {
        this.mDelegate.processFinish(value)
    }


}
