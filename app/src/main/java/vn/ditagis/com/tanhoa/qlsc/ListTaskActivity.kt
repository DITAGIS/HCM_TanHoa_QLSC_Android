package vn.ditagis.com.tanhoa.qlsc

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_list_task.*
import kotlinx.android.synthetic.main.layout_dialog.view.*

import java.util.Objects

import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.fragment.listtask.ListTaskFragment
import vn.ditagis.com.tanhoa.qlsc.fragment.listtask.SearchFragment

class ListTaskActivity : AppCompatActivity() {

    private var mListTaskFragment: ListTaskFragment? = null
    private var mSearchFragment: SearchFragment? = null

    private var mApplication: DApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task)
        mApplication = application as DApplication
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(true)
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowHomeEnabled(true)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container_basemap.adapter = sectionsPagerAdapter
        container_basemap.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs_basemap))
        tabs_basemap.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container_basemap))
        mListTaskFragment = ListTaskFragment(this@ListTaskActivity, layoutInflater)
        mSearchFragment = SearchFragment(this@ListTaskActivity, layoutInflater)

        container_basemap.setCurrentItem(0, true)
    }

    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> mSearchFragment
                1 -> mListTaskFragment
                else -> null
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    fun itemClick(adapter: AdapterView<*>, position: Int) {
        val item = adapter.getItemAtPosition(position) as TraCuuAdapter.Item
        val layout = layoutInflater.inflate(R.layout.layout_dialog,
                clayout__list_task__root_view as CoordinatorLayout, false) as LinearLayout
        layout.txt_dialog_title.text = getString(R.string.message_title_confirm)
        layout.txt_dialog_message.text = getString(R.string.message_click_list_task, item.id)

        val builder = AlertDialog.Builder(this@ListTaskActivity)
        builder.setView(layout)
        builder.setCancelable(false)
                .setPositiveButton(R.string.message_btn_ok) { _, _ ->
                    mApplication!!.getDiemSuCo!!.idSuCo = item.id
                    mApplication!!.getDiemSuCo!!.trangThai = item.trangThai.toShort()
                    goHome()
                }.setNegativeButton(R.string.message_btn_cancel) { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        goHome()
    }


    private fun goHome() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}
