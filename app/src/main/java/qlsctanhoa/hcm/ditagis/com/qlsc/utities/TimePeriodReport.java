package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.ThongKeAdapter;

/**
 * Created by NGUYEN HONG on 4/26/2018.
 */

public class TimePeriodReport {
    private Calendar calendar;
    private Date today;
    private List<ThongKeAdapter.Item> items;

    public TimePeriodReport() {
        today = new Date();
        calendar = Calendar.getInstance();
        items = new ArrayList<>();
        items.add(new ThongKeAdapter.Item(1, "Tất cả", null, null,null));
        items.add(new ThongKeAdapter.Item(2, "Tháng này", dayToFirstYearString(getFirstDayofMonth()), dayToFirstYearString(getLastDayofMonth()),dayToFirstDayString(getFirstDayofMonth(),getLastDayofMonth())));
        items.add(new ThongKeAdapter.Item(3, "Tháng trước", dayToFirstYearString(getFirstDayofLastMonth()), dayToFirstYearString(getLastDayofLastMonth()),dayToFirstDayString(getFirstDayofLastMonth(),getLastDayofLastMonth())));
        items.add(new ThongKeAdapter.Item(4, "3 tháng gần nhất", dayToFirstYearString(getFirstDayofLast3Months()), dayToFirstYearString(getLastDayofLast3Months()),dayToFirstDayString(getFirstDayofLast3Months(),getLastDayofLast3Months())));
        items.add(new ThongKeAdapter.Item(5, "6 tháng gần nhất", dayToFirstYearString(getFirstDayofLast6Months()), dayToFirstYearString(getLastDayofLast6Months()),dayToFirstDayString(getFirstDayofLast6Months(),getLastDayofLast6Months())));
        items.add(new ThongKeAdapter.Item(6, "Năm nay", dayToFirstYearString(getFirstDayofYear()), dayToFirstYearString(getLastDayofYear()),dayToFirstDayString(getFirstDayofYear(),getLastDayofYear())));
        items.add(new ThongKeAdapter.Item(7, "Năm trước", dayToFirstYearString(getFirstDayoflLastYear()), dayToFirstYearString(getLastDayofLastYear()),dayToFirstDayString(getFirstDayoflLastYear(),getLastDayofLastYear())));
        items.add(new ThongKeAdapter.Item(8, "Tùy chỉnh",null,null,"-- - --"));
    }

    public List<ThongKeAdapter.Item> getItems() {
        return items;
    }

    public void setItems(List<ThongKeAdapter.Item> items) {
        this.items = items;
    }

    private String dayToFirstYearString(Date date) {
        return (String) DateFormat.format("yyyy-MM-dd 00:00:00", date);
    }

    private String dayToFirstDayString(Date firstDate, Date lastDate) {
        return (String) DateFormat.format("dd/MM/yyyy", firstDate) + " - " + (String) DateFormat.format("dd/MM/yyyy", lastDate);
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
