package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject
import vn.ditagis.com.tanhoa.qlsc.R

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat

import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.VersionInfo
import java.io.Reader


class CheckVersionAsycn(@field:SuppressLint("StaticFieldLeak")
                        private val mActivity: Activity, private val mDelegate: CheckVersionAsycn.AsyncResponse)
    : AsyncTask<String, Void, VersionInfo?>() {
    private var mDialog: Dialog? = null

    interface AsyncResponse {
        fun processFinish(versionInfo: VersionInfo?)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang kiểm tra phiên bản..."
        mDialog = Dialog(mActivity)
        mDialog!!.setCancelable(false)
        mDialog!!.setContentView(layout)
        mDialog!!.show()
    }

    override fun doInBackground(vararg params: String): VersionInfo? {
        var versionInfo: VersionInfo? = null
        if (params.isNotEmpty())
            try {
                val url = URL(String.format(Constant.UrlApi.CHECK_VERSION, params[0]))
                val conn = url.openConnection() as HttpURLConnection
                try {
                    conn.requestMethod = "GET"
                    conn.connect()
                    val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream) as Reader?)
                    val stringBuilder = StringBuilder()
                    val line: String
                    while (true) {
                        line = bufferedReader.readLine()
                        if (line == null)
                            break
                        stringBuilder.append(line)
                        break
                    }
                    bufferedReader.close()
                    versionInfo = parse(stringBuilder.toString())
                } catch (e1: Exception) {
                    Log.e("Lỗi check version", e1.toString())
                } finally {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message, e)

            } finally {
                return versionInfo
            }
        return versionInfo
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(JSONException::class)
    private fun parse(data: String?): VersionInfo? {
        if (data == null)
            return null
        var versionInfo: VersionInfo? = null
        val myData = "{ \"version\": [$data]}"
        val jsonData = JSONObject(myData)
        val jsonRoutes = jsonData.getJSONArray("version")
        for (i in 0 until jsonRoutes.length()) {
            val jsonRoute = jsonRoutes.getJSONObject(i)
            val versionCode = jsonRoute.getString("VersionCode")
            val type = jsonRoute.getString("Type")
            val link = jsonRoute.getString("Link")
            val date = jsonRoute.getString("Date")
            try {
                versionInfo = VersionInfo(versionCode, type, link, SimpleDateFormat("yyyy-MM-dd").parse(date))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
        return versionInfo
    }

    override fun onPostExecute(versionInfo: VersionInfo?) {
        //        if (user != null) {
        mDialog!!.dismiss()
        this.mDelegate.processFinish(versionInfo)
        //        }
    }

}
