package vn.ditagis.com.tanhoa.qlsc.adapter

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

class FeatureViewInfoAdapter(private val mContext: Context, private val items: MutableList<Item>) : ArrayAdapter<FeatureViewInfoAdapter.Item>(mContext, 0, items) {

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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_viewinfo, null)
        }
        val item = items[position]

        val txtAlias = view!!.findViewById<View>(R.id.txt_viewinfo_alias) as TextView
        txtAlias.text = item.alias

        val txtValue = view.findViewById<View>(R.id.txt_viewinfo_value) as TextView
        txtValue.text = item.value
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
//        var isEdit: Boolean = false
//        var fieldType: Field.Type? = null
    }
}
