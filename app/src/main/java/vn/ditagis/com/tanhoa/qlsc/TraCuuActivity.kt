package vn.ditagis.com.tanhoa.qlsc

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import com.esri.arcgisruntime.data.Field

import com.esri.arcgisruntime.data.ServiceFeatureTable
import kotlinx.android.synthetic.main.date_time_picker.view.*
import kotlinx.android.synthetic.main.layout_dialog_update_feature_listview.*
import kotlinx.android.synthetic.main.layout_dialog_update_feature_listview.view.*
import kotlinx.android.synthetic.main.layout_viewmoreinfo_feature.*

import java.util.ArrayList

import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter

class TraCuuActivity : AppCompatActivity() {
    private val mServiceFeatureTable: ServiceFeatureTable? = null
    private var mLstFeatureType: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tra_cuu)

        //        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.service_feature_table));

        setContentView(R.layout.activity_tra_cuu)
        mLstFeatureType = ArrayList()
        for (i in 0 until mServiceFeatureTable!!.featureTypes.size) {
            mLstFeatureType!!.add(mServiceFeatureTable.featureTypes[i].name)
        }
        lstView_alertdialog_info.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> edit(parent, view, position) }
    }

    private fun edit(parent: AdapterView<*>, view: View, position: Int) {
        if (parent.getItemAtPosition(position) is vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter.Item) {
            val item = parent.getItemAtPosition(position) as FeatureViewMoreInfoAdapter.Item
            if (item.isEdit) {
                val builder = AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Light_Dialog_Alert)
                builder.setTitle("Cập nhật thuộc tính")
                builder.setMessage(item.alias)
                builder.setCancelable(false).setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
                val layout = this.layoutInflater.inflate(R.layout.layout_dialog_update_feature_listview, null) as android.widget.LinearLayout
                builder.setView(layout)

                val domain = mServiceFeatureTable!!.getField(item.fieldName!!).domain
                if (item.fieldName == mServiceFeatureTable.typeIdField) {
                    layout.layout_edit_viewmoreinfo_Spinner.visibility = View.VISIBLE
                    val adapter = android.widget.ArrayAdapter(layout.context,
                            android.R.layout.simple_list_item_1, mLstFeatureType!!)
                    layout.spin_edit_viewmoreinfo.adapter = adapter
                    if (item.value != null)
                        layout.spin_edit_viewmoreinfo.setSelection(mLstFeatureType!!.indexOf(item.value!!))
                } else if (domain != null) {
                    layout.layout_edit_viewmoreinfo_Spinner.visibility = View.VISIBLE
                    val codedValues = (domain as com.esri.arcgisruntime.data.CodedValueDomain).codedValues
                    if (codedValues != null) {
                        val codes = ArrayList<String>()
                        for (codedValue in codedValues)
                            codes.add(codedValue.name)
                        val adapter = android.widget.ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, codes)
                        layout.spin_edit_viewmoreinfo.adapter = adapter
                        if (item.value != null)
                            layout.spin_edit_viewmoreinfo.setSelection(codes.indexOf(item.value!!))

                    }
                } else
                    when (item.fieldType) {
                        Field.Type.DATE -> {
                            layout.layout_edit_viewmoreinfo_TextView.visibility = View.VISIBLE
                            layout.txt_edit_viewmoreinfo.text = item.value
                            layout.btn_edit_viewmoreinfo.setOnClickListener {
                                val dialogView = View.inflate(this@TraCuuActivity, R.layout.date_time_picker, null)
                                val alertDialog = android.app.AlertDialog.Builder(this@TraCuuActivity).create()
                                dialogView.date_time_set.setOnClickListener {
                                    val s = String.format("%02d_%02d_%d",
                                            dialogView.date_picker.dayOfMonth, dialogView.date_picker.month, dialogView.date_picker.year)

                                    txt_edit_viewmoreinfo.text = s
                                    alertDialog.dismiss()
                                }
                                alertDialog.setView(dialogView)
                                alertDialog.show()
                            }
                        }
                        Field.Type.TEXT -> {
                            layout.layout_edit_viewmoreinfo_Editext.visibility = View.VISIBLE
                            layout.etxt_edit_viewmoreinfo.setText(item.value)
                        }
                        Field.Type.SHORT -> {
                            layout.layout_edit_viewmoreinfo_Editext.visibility = View.VISIBLE
                            layout.etxt_edit_viewmoreinfo.inputType = android.text.InputType.TYPE_CLASS_NUMBER
                            layout.etxt_edit_viewmoreinfo.setText(item.value)
                        }
                        Field.Type.DOUBLE -> {
                            layout.layout_edit_viewmoreinfo_Editext.visibility = View.VISIBLE
                            layout.etxt_edit_viewmoreinfo.inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                            layout.etxt_edit_viewmoreinfo.setText(item.value)
                        }
                        else -> {
                        }
                    }
                builder.setPositiveButton("Cập nhật") { dialog, which ->
                    if (item.fieldName == mServiceFeatureTable.typeIdField || domain != null) {
                        item.value = layout.spin_edit_viewmoreinfo.selectedItem.toString()
                    } else {
                        when (item.fieldType) {
                            Field.Type.DATE -> item.value = txt_edit_viewmoreinfo.text.toString()
                            Field.Type.DOUBLE -> try {
                                val x = java.lang.Double.parseDouble(layout.etxt_edit_viewmoreinfo.text.toString())
                                item.value = layout.etxt_edit_viewmoreinfo.text.toString()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(this@TraCuuActivity, "Số liệu nhập vào không đúng định dạng!!!", android.widget.Toast.LENGTH_LONG).show()
                            }

                            Field.Type.TEXT -> item.value = layout.etxt_edit_viewmoreinfo.text.toString()
                            Field.Type.SHORT -> try {
                                val x = java.lang.Short.parseShort(layout.etxt_edit_viewmoreinfo.text.toString())
                                item.value = layout.etxt_edit_viewmoreinfo.text.toString()
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(this@TraCuuActivity, "Số liệu nhập vào không đúng định dạng!!!", android.widget.Toast.LENGTH_LONG).show()
                            }

                        }
                    }


                    dialog.dismiss()
                    val adapter = parent.adapter as FeatureViewMoreInfoAdapter
                    vn.ditagis.com.tanhoa.qlsc.async.NotifyDataSetChangeAsync(this@TraCuuActivity).execute(adapter)
                }

                val dialog = builder.create()
                dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
                dialog.show()

            }
        }

    }

}