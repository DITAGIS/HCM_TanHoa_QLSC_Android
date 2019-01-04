package vn.ditagis.com.tanhoa.qlsc.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.esri.arcgisruntime.data.Field

import vn.ditagis.com.tanhoa.qlsc.R

/**
 * Created by ThanLe on 04/10/2017.
 */

class FeatureViewMoreInfoAdapter(private val mContext: Context, var dItems: MutableList<Item>?) : ArrayAdapter<FeatureViewMoreInfoAdapter.Item>(mContext, 0, dItems) {

    fun getItems(): List<Item>? {
        return dItems
    }

//    fun addAll(dItems: MutableList<Item>) {
//        this.dItems = dItems
//    }

    override fun clear() {
        dItems!!.clear()
    }

    override fun getCount(): Int {
        return dItems!!.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_viewmoreinfo, null)
        }
        val item = dItems!![position]

        val txtAlias = view!!.findViewById<TextView>(R.id.txt_viewmoreinfo_alias)
        txtAlias.text = item.alias

        val txtValue = view.findViewById<TextView>(R.id.txt_viewmoreinfo_value)
        if (item.fieldName == "ViTri" || item.fieldName == "GhiChu" || item.fieldName == "GhiChuVatTu") {
            txtValue.width = 550
        }
        txtValue.text = item.value
        if (item.isEdit) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent_1))
            view.findViewById<View>(R.id.img_viewmoreinfo_edit).visibility = View.VISIBLE
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorBackground_1))
            view.findViewById<View>(R.id.img_viewmoreinfo_edit).visibility = View.INVISIBLE

        }
        if (item.value == null)
            txtValue.visibility = View.GONE
        else
            txtValue.visibility = View.VISIBLE
        return view
    }


    class Item {
        var alias: String? = null
        var value: String? = null
        var fieldName: String? = null
        var isEdit: Boolean = false
        var fieldType: Field.Type? = null
        var isEdited: Boolean = false

        constructor()

        constructor(alias: String, value: String, fieldName: String, isEdit: Boolean, fieldType: Field.Type, isEdited: Boolean) {
            this.alias = alias
            this.value = value
            this.fieldName = fieldName
            this.isEdit = isEdit
            this.fieldType = fieldType
            this.isEdited = isEdited
        }
    }
}
