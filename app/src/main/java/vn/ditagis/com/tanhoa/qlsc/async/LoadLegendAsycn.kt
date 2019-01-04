package vn.ditagis.com.tanhoa.qlsc.async

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import java.io.IOException
import java.io.InputStream
import java.net.URL

import vn.ditagis.com.tanhoa.qlsc.MainActivity
import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant

@SuppressLint("StaticFieldLeak")
class LoadLegendAsycn(private val mLayout: LinearLayout, private val mActivity: MainActivity, private val mDelegate: AsyncResponse) :
        AsyncTask<Void, Void, Void?>() {
    private var mDialog: Dialog? = null

    interface AsyncResponse {
        fun processFinish(output: Void?)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val layout = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null) as LinearLayout
        val txtTitle = layout.findViewById<TextView>(R.id.txt_progress_dialog_title)
        txtTitle.text = "Đang khởi tạo bản đồ..."
        mDialog = Dialog(mActivity)
        mDialog!!.setCancelable(false)
        mDialog!!.setContentView(layout)
        mDialog!!.show()
    }


    @SuppressLint("SetTextI18n")
    override fun doInBackground(vararg params: Void): Void? {
        val layoutFeatureBeNgam = mActivity.layoutInflater.inflate(R.layout.layout_legend, mLayout, false) as LinearLayout
        val imgBeNgam = layoutFeatureBeNgam.findViewById<ImageView>(R.id.img_layout_legend)
        val txtBeNgam = layoutFeatureBeNgam.findViewById<TextView>(R.id.txt_layout_legend)
        txtBeNgam.setTextColor(Color.BLACK)

        val layoutFeatureChuaXuLy = mActivity.layoutInflater.inflate(R.layout.layout_legend, mLayout, false) as LinearLayout
        val imgChuaXuLy = layoutFeatureChuaXuLy.findViewById<ImageView>(R.id.img_layout_legend)
        val txtChuaXuLy = layoutFeatureChuaXuLy.findViewById<TextView>(R.id.txt_layout_legend)
        txtChuaXuLy.setTextColor(Color.BLACK)

        val layoutFeatureDangXuLy = mActivity.layoutInflater.inflate(R.layout.layout_legend, mLayout, false) as LinearLayout
        val imgDangXuLy = layoutFeatureDangXuLy.findViewById<ImageView>(R.id.img_layout_legend)
        val txtDangXuLy = layoutFeatureDangXuLy.findViewById<TextView>(R.id.txt_layout_legend)
        txtDangXuLy.setTextColor(Color.BLACK)

        val layoutFeatureHoanThanh = mActivity.layoutInflater.inflate(R.layout.layout_legend, mLayout, false) as LinearLayout
        val imgHoanThanh = layoutFeatureHoanThanh.findViewById<ImageView>(R.id.img_layout_legend)
        val txtHoanThanh = layoutFeatureHoanThanh.findViewById<TextView>(R.id.txt_layout_legend)
        txtHoanThanh.setTextColor(Color.BLACK)


        try {
            val drawableBeNgam = Drawable.createFromStream(URL(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM).content as InputStream, "src")
            imgBeNgam.setImageDrawable(drawableBeNgam)
            txtBeNgam.text = "Chưa sửa chữa bể ngầm"

            val drawableChuaXuLy = Drawable.createFromStream(URL(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA).content as InputStream, "src")
            imgChuaXuLy.setImageDrawable(drawableChuaXuLy)
            txtChuaXuLy.text = "Chưa sửa chữa"

            val drawableDangXuLy = Drawable.createFromStream(URL(Constant.URLSymbol.URL_SYMBOL_DANG_SUA_CHUA).content as InputStream, "src")
            imgDangXuLy.setImageDrawable(drawableDangXuLy)
            txtDangXuLy.text = "Đang sửa chữa"

            val drawableHoanThanh = Drawable.createFromStream(URL(Constant.URLSymbol.URL_SYMBOL_HOAN_THANH).content as InputStream, "src")
            imgHoanThanh.setImageDrawable(drawableHoanThanh)
            txtHoanThanh.text = "Hoàn thành"


            mActivity.runOnUiThread {
                mLayout.addView(layoutFeatureChuaXuLy)
                mLayout.addView(layoutFeatureBeNgam)
                mLayout.addView(layoutFeatureDangXuLy)
                mLayout.addView(layoutFeatureHoanThanh)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(value: Void?) {
        //        if (khachHang != mLayout,false) {
        mDialog!!.dismiss()
        this.mDelegate.processFinish(value)
        //        }
    }
}
