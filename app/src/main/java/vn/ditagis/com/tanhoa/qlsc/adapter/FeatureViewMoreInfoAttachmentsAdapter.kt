package vn.ditagis.com.tanhoa.qlsc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.entities.Constant

/**
 * Created by ThanLe on 04/10/2017.
 */

class FeatureViewMoreInfoAttachmentsAdapter(private val mContext: Context, private val items: MutableList<Item>) : ArrayAdapter<FeatureViewMoreInfoAttachmentsAdapter.Item>(mContext, 0, items) {

    fun getItems(): List<Item> {
        return items
    }

    override fun clear() {
        items.clear()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_viewmoreinfo_attachment, null)
        }
        val item = items[position]

        val txtValue = view!!.findViewById<TextView>(R.id.txt_viewmoreinfo_attachment_name)
        txtValue.text = item.name
        val imageView = view.findViewById<ImageView>(R.id.img_viewmoreinfo_attachment)
        if (item.contentType == Constant.FileType.PNG && item.img != null) {

            val bmp = BitmapFactory.decodeByteArray(item.img, 0, item.img!!.size)

            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width,
                    bmp.height, false))
        } else
            imageView.visibility = View.GONE
        return view
    }


    class Item {
        var name: String? = null
        internal var img: ByteArray? = null
        var url: String? = null
        var contentType: String? = null
    }
}
