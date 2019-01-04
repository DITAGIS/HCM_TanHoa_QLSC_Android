package vn.ditagis.com.tanhoa.qlsc.async

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter

/**
 * Created by ThanLe on 4/16/2018.
 */

class NotifyDataSetChangeTraCuuAsync(private val mActivity: Activity) : AsyncTask<TraCuuAdapter, Void, Void?>() {
    private val dialog: ProgressDialog?
    private val mContext: Context? = null

    init {
        dialog = ProgressDialog(mActivity, android.R.style.Theme_Material_Dialog_Alert)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        dialog!!.setMessage(mActivity.getString(R.string.async_dang_cap_nhat_giao_dien))
        dialog.setCancelable(false)

        dialog.show()

    }

    override fun doInBackground(vararg params: TraCuuAdapter): Void? {
        val adapter = params[0]
        try {
            Thread.sleep(500)
            mActivity.runOnUiThread { adapter.notifyDataSetChanged() }
        } catch (e: InterruptedException) {

        }


        return null
    }

    override fun onProgressUpdate(vararg values: Void) {
        super.onProgressUpdate(*values)

    }


    override fun onPostExecute(result: Void?) {
        if (dialog != null || dialog!!.isShowing)
            dialog.dismiss()
        super.onPostExecute(result)

    }

}
