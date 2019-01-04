package vn.ditagis.com.tanhoa.qlsc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import vn.ditagis.com.tanhoa.qlsc.R

/**
 * Created by ThanLe on 04/10/2017.
 */
class ThongKeAdapter(private val mContext: Context, private val items: MutableList<Item>) : ArrayAdapter<ThongKeAdapter.Item>(mContext, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_thoigian_thongke, null)
        }
        val item = items[position]
//        val layout = convertView!!.findViewById<View>(R.id.layout_tracuu) as LinearLayout
        val txt_thongke_mota = view!!.findViewById<View>(R.id.txt_thongke_mota) as TextView
        txt_thongke_mota.text = item.mota
        val txt_thongke_thoigian = view.findViewById<View>(R.id.txt_thongke_thoigian) as TextView
        if (item.thoigianhienthi != null) {
            txt_thongke_thoigian.text = item.thoigianhienthi
        }
        val imageView = view.findViewById<View>(R.id.img_selectTime) as ImageView
        if (item.isChecked) {
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.INVISIBLE
        }
        return view
    }

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


    class Item {
        var id: Int = 0
        lateinit var mota: String
        var thoigianbatdau: String? = null
        var thoigianketthuc: String? = null
        var thoigianhienthi: String? = null
        var isChecked = false

        constructor()

        constructor(id: Int, mota: String, thoigianbatdau: String?, thoigianketthuc: String?, thoigianhienthi: String?) {
            this.id = id
            this.mota = mota
            this.thoigianbatdau = thoigianbatdau
            this.thoigianketthuc = thoigianketthuc
            this.thoigianhienthi = thoigianhienthi
        }
    }
}
