package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.Intent;

import qlsctanhoa.hcm.ditagis.com.qlsc.QuanLySuCo;
import qlsctanhoa.hcm.ditagis.com.qlsc.ThongKeActivity;

/**
 * Created by NGUYEN HONG on 3/8/2018.
 */

public class MapFunctions {
    private QuanLySuCo mQuanLySuCo;
    public MapFunctions(QuanLySuCo mQuanLySuCo){
        this.mQuanLySuCo = mQuanLySuCo;
    }
    public void thongKe(){
        Intent intent = new Intent(this.mQuanLySuCo, ThongKeActivity.class);
        mQuanLySuCo.startActivity(intent);
    }
}
