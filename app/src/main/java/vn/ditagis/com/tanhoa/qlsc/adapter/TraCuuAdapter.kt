package vn.ditagis.com.tanhoa.qlsc.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

import vn.ditagis.com.tanhoa.qlsc.R

/**
 * Created by ThanLe on 04/10/2017.
 */

class TraCuuAdapter(private val mContext: Context, val dItems: MutableList<Item>) : ArrayAdapter<TraCuuAdapter.Item>(mContext, 0, dItems) {

    fun getItems(): List<Item> {
        return dItems
    }

    override fun clear() {
        dItems.clear()
    }

    override fun getCount(): Int {
        return dItems.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_tracuu, null)
        }
        val item = dItems[position]

        val layout = view!!.findViewById<View>(R.id.layout_tracuu) as LinearLayout
        when (item.trangThai) {
            //chưa sửa chữa
            0 -> layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_chua_sua_chua))
            //đã sửa chữa
            1 -> layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_da_sua_chua))
            //đang sửa chữa
            2 -> layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_dang_sua_chua))
            3 -> layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryLight))
            4 -> layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite))
        }

        val txtID = view.findViewById<TextView>(R.id.txt_top)
        if (item.id == null || item.id!!.isEmpty())
            txtID.visibility = View.GONE
        else
            txtID.text = item.id

        val txtDiaChi = view.findViewById<View>(R.id.txt_bottom) as TextView
        if (item.diaChi == null || item.diaChi!!.isEmpty())
            txtDiaChi.visibility = View.GONE
        else
            txtDiaChi.text = item.diaChi

        val txtNgayCapNhat = view.findViewById<View>(R.id.txt_right) as TextView
        if (item.ngayGiaoViec == null || item.ngayGiaoViec!!.isEmpty())
            txtNgayCapNhat.visibility = View.GONE
        else
            txtNgayCapNhat.text = item.ngayGiaoViec


        return view
    }


    class Item {


        var objectID: Int = 0
        var id: String? = null
        var trangThai: Int = 0
        var ngayGiaoViec: String? = null
        var diaChi: String? = null
        var latitude: Double = 0.toDouble()
        var longtitude: Double = 0.toDouble()


        constructor(objectID: Int, id: String, trangThai: Int, ngayCapNhat: String, diaChi: String) {
            this.objectID = objectID
            this.id = id
            this.trangThai = trangThai
            this.ngayGiaoViec = ngayCapNhat
            this.diaChi = diaChi
        }

        constructor(objectID: Int, id: String, ngayCapNhat: String, diaChi: String) {
            this.objectID = objectID
            this.id = id
            this.ngayGiaoViec = ngayCapNhat
            this.diaChi = diaChi
        }

        override fun toString(): String {
            return "Item{" + "objectID=" + objectID + ", id='" + id + '\''.toString() + ", trangThai=" + trangThai + ", ngayGiaoViec='" + ngayGiaoViec + '\''.toString() + ", diaChi='" + diaChi + '\''.toString() + '}'.toString()
        }
    }
}