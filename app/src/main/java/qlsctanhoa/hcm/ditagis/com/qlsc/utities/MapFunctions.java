package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.QuanLySuCo;
import qlsctanhoa.hcm.ditagis.com.qlsc.R;
import qlsctanhoa.hcm.ditagis.com.qlsc.ThongKeActivity;

/**
 * Created by NGUYEN HONG on 3/8/2018.
 */

public class MapFunctions {
    private QuanLySuCo mQuanLySuCo;
    private ServiceFeatureTable mServiceFeatureTable;

    public MapFunctions(QuanLySuCo mQuanLySuCo, ServiceFeatureTable mServiceFeatureTable) {
        this.mQuanLySuCo = mQuanLySuCo;
        this.mServiceFeatureTable = mServiceFeatureTable;

    }

    public void thongKe() {
        final int[] tongloaitrangthai = {0, 0,0,0 };



        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause("1=1");
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    Iterator iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Feature item = (Feature) iterator.next();
                        tongloaitrangthai[0] += 1;
                        int trangthai = Integer.parseInt(item.getAttributes().get(Constant.FEATURE_ATTRIBUTE_TRANGTHAI_SUCO).toString());
                        if(trangthai == 0)
                            tongloaitrangthai[1] += 1;
                        else if(trangthai == 1)tongloaitrangthai[2] += 1;
                        else if(trangthai == 3)tongloaitrangthai[3] += 1;
                    }


                    final Intent intent = new Intent(mQuanLySuCo, ThongKeActivity.class);
                    intent.putExtra(mQuanLySuCo.getApplicationContext().getString(R.string.tongloaitrangthai),tongloaitrangthai);
                    mQuanLySuCo.startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });



    }
}
