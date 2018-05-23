package hcm.ditagis.com.tanhoa.qlsc.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.QuanLySuCo;
import hcm.ditagis.com.tanhoa.qlsc.ThongKeActivity;
import hcm.ditagis.com.tanhoa.qlsc.adapter.DanhSachSuCoAdapter;
import hcm.ditagis.com.tanhoa.qlsc.R;
import hcm.ditagis.com.tanhoa.qlsc.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QuerySuCoAsync extends AsyncTask<String, List<DanhSachSuCoAdapter.Item>, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable serviceFeatureTable;
    private DanhSachSuCoAdapter danhSachDiemDanhGiaAdapter;
    private TextView txtTongItem;

    public QuerySuCoAsync(ThongKeActivity thongKeActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, DanhSachSuCoAdapter adapter, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = thongKeActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.danhSachDiemDanhGiaAdapter = adapter;
        this.txtTongItem = txtTongItem;
        dialog = new ProgressDialog(thongKeActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    public QuerySuCoAsync(QuanLySuCo mainActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, DanhSachSuCoAdapter adapter, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = mainActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.danhSachDiemDanhGiaAdapter = adapter;
        this.txtTongItem = txtTongItem;
        dialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<DanhSachSuCoAdapter.Item> items = new ArrayList<>();
        final List<Feature> features = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = params[0];
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = queryResultListenableFuture.get();
                    Iterator iterator = result.iterator();

                    while (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        DanhSachSuCoAdapter.Item item = new DanhSachSuCoAdapter.Item();
                        Map<String, Object> attributes = feature.getAttributes();
                        item.setObjectID(attributes.get(mContext.getString(R.string.OBJECTID)).toString());
                        item.setIdSuCo(attributes.get(mContext.getString(R.string.IDSUCO)).toString());
                        Object ngayxayra = attributes.get(mContext.getString(R.string.NGAYXAYRA));
                        if (ngayxayra != null) {
                            String format_date = Constant.DATE_FORMAT.format(((Calendar) ngayxayra).getTime());
                            item.setNgayXayRa(format_date);
                        }
                        Object vitri = attributes.get(mContext.getString(R.string.VITRI));
                        if (vitri != null) item.setDiaChi(vitri.toString());
                        items.add(item);
                        features.add(feature);
                    }
                    delegate.processFinish(features);
                    publishProgress(items);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(List<DanhSachSuCoAdapter.Item>... values) {
        danhSachDiemDanhGiaAdapter.clear();
        danhSachDiemDanhGiaAdapter.setItems(values[0]);
        danhSachDiemDanhGiaAdapter.notifyDataSetChanged();
        if (txtTongItem != null)
            txtTongItem.setText(mContext.getString(R.string.nav_thong_ke_tong_diem) + values[0].size());
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        super.onProgressUpdate(values);

    }

    private String getValueAttributes(Feature feature, String fieldName) {
        if (feature.getAttributes().get(fieldName) != null)
            return feature.getAttributes().get(fieldName).toString();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog != null || dialog.isShowing()) dialog.dismiss();
        super.onPostExecute(result);

    }

}

