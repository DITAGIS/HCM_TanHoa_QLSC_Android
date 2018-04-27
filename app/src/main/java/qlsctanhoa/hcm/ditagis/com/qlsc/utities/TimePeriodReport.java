package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import qlsctanhoa.hcm.ditagis.com.qlsc.R;
import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.ThongKeAdapter;

/**
 * Created by NGUYEN HONG on 4/26/2018.
 */

public class TimePeriodReport {
    private Calendar calendar;
    private Date today;
    private List<ThongKeAdapter.Item> items;
    private Context mContext;

    public TimePeriodReport(Context context) {
        mContext = context;
        today = new Date();
        calendar = Calendar.getInstance();
        items = new ArrayList<>();
        items.add(new ThongKeAdapter.Item(1, "Tất cả", null, null, null));
        items.add(new ThongKeAdapter.Item(2, "Tháng này", startDayToFirstYearString(getFirstDayofMonth()), endDayToFirstYearString(getLastDayofMonth()), dayToFirstDayString(getFirstDayofMonth(), getLastDayofMonth())));
        items.add(new ThongKeAdapter.Item(3, "Tháng trước", startDayToFirstYearString(getFirstDayofLastMonth()), endDayToFirstYearString(getLastDayofLastMonth()), dayToFirstDayString(getFirstDayofLastMonth(), getLastDayofLastMonth())));
        items.add(new ThongKeAdapter.Item(4, "3 tháng gần nhất", startDayToFirstYearString(getFirstDayofLast3Months()), endDayToFirstYearString(getLastDayofLast3Months()), dayToFirstDayString(getFirstDayofLast3Months(), getLastDayofLast3Months())));
        items.add(new ThongKeAdapter.Item(5, "6 tháng gần nhất", startDayToFirstYearString(getFirstDayofLast6Months()), endDayToFirstYearString(getLastDayofLast6Months()), dayToFirstDayString(getFirstDayofLast6Months(), getLastDayofLast6Months())));
        items.add(new ThongKeAdapter.Item(6, "Năm nay", startDayToFirstYearString(getFirstDayofYear()), endDayToFirstYearString(getLastDayofYear()), dayToFirstDayString(getFirstDayofYear(), getLastDayofYear())));
        items.add(new ThongKeAdapter.Item(7, "Năm trước", startDayToFirstYearString(getFirstDayoflLastYear()), endDayToFirstYearString(getLastDayofLastYear()), dayToFirstDayString(getFirstDayoflLastYear(), getLastDayofLastYear())));
        items.add(new ThongKeAdapter.Item(8, "Tùy chỉnh", null, null, "-- - --"));
    }

    public List<ThongKeAdapter.Item> getItems() {
        return items;
    }

    public void setItems(List<ThongKeAdapter.Item> items) {
        this.items = items;
    }

    private String startDayToFirstYearString(Date date) {
        return (String) DateFormat.format(mContext.getString(R.string.format_startday_yearfirst), date);
    }

    private String endDayToFirstYearString(Date date) {
        return (String) DateFormat.format(mContext.getString(R.string.format_endday_yearfirst), date);
    }

    private String dayToFirstDayString(Date firstDate, Date lastDate) {
        return (String) DateFormat.format(mContext.getString(R.string.format_time_day_month_year), firstDate) + " - " + (String) DateFormat.format(mContext.getString(R.string.format_time_day_month_year), lastDate);
    }

    private Date getFirstDayofMonth() {
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofMonth() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLastMonth() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLastMonth() {
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLast3Months() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, -2);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLast3Months() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofLast6Months() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, -5);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLast6Months() {
        calendar.setTime(today);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date getFirstDayofYear() {
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date getLastDayofYear() {
        calendar.setTime(today);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.MONTH, 11);
        return calendar.getTime();
    }

    private Date getFirstDayoflLastYear() {
        calendar.setTime(today);
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date getLastDayofLastYear() {
        calendar.setTime(today);
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.MONTH, 11);
        return calendar.getTime();
    }
}
