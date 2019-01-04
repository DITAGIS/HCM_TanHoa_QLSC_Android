package vn.ditagis.com.tanhoa.qlsc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import vn.ditagis.com.tanhoa.qlsc.R

/**
 * Created by ThanLe on 04/10/2017.
 */

class ThietBiAdapter(private val mContext: Context, private val items: MutableList<Item>) : ArrayAdapter<ThietBiAdapter.Item>(mContext, 0, items) {

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

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_tracuu, null)
        }
        val item = items[position]

        val txtTop = view!!.findViewById<TextView>(R.id.txt_top)
        if (item.tenThietBi == null || item.tenThietBi.isEmpty())
            txtTop.visibility = View.GONE
        else
            txtTop.text = item.tenThietBi


        val txtRight = view.findViewById<View>(R.id.txt_right) as TextView
        txtRight.text = item.soLuong.toString() + " phút"

        val txtBottom = view.findViewById<TextView>(R.id.txt_bottom)
        txtBottom.visibility = View.GONE
        return view
    }


    class Item(val tenThietBi: String?, val soLuong: Double, val maThietBi: String)
}