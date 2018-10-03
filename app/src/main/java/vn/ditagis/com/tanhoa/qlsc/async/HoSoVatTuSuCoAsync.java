package vn.ditagis.com.tanhoa.qlsc.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;


public class HoSoVatTuSuCoAsync extends AsyncTask<Object, Object, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private DApplication mApplication;
    private AsyncResponse mDelegate;
    private short mLoaiVatTu;

    public interface AsyncResponse {
        void processFinish(Object object);
    }

    public HoSoVatTuSuCoAsync(Activity activity, AsyncResponse response) {
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        mLoaiVatTu = mApplication.getLoaiVatTu();
        this.mDelegate = response;
        mServiceFeatureTable = mApplication.getDFeatureLayer.getServiceFeatureTableHoSoVatTuSuCo();
    }

    private void find(String idSuCo) {
        QueryParameters queryParameters = new QueryParameters();
        final List<HoSoVatTuSuCo> hoSoVatTuSuCos = new ArrayList<>();

        String queryClause = String.format("%s like '%%%s%%' and %s = %d", Constant.FIELD_VATTU.ID_SU_CO, idSuCo,
                Constant.FIELD_VATTU.LOAI_VAT_TU, mLoaiVatTu);
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture =
                this.mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();
                if (result.iterator().hasNext()) {
                    StringBuilder query = new StringBuilder();
                    List<HoSoVatTuSuCo> tempHoSoVatTuSuCo = new ArrayList<>();
                    for (Iterator it = result.iterator(); it.hasNext(); ) {
                        Feature feature = (Feature) it.next();
                        Map<String, Object> attributes = feature.getAttributes();
                        String maVatTu = attributes.get(Constant.FIELD_VATTU.MA_VAT_TU).toString();
                        query.append(String.format("%s = '%s' or ", Constant.FIELD_VATTU.MA_VAT_TU, maVatTu));
                        tempHoSoVatTuSuCo.add(new HoSoVatTuSuCo(attributes.get(Constant.FIELD_VATTU.ID_SU_CO).toString(),
                                Double.parseDouble(attributes.get(Constant.FIELD_VATTU.SO_LUONG).toString()),
                                maVatTu,
                                "",
                                ""));
                    }
                    query.append("1 = 0");
                    for (DLayerInfo dLayerInfo : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
                        if (dLayerInfo.getId().equals(Constant.ID_VAT_TU_TABLE)) {
                            final QueryParameters queryParametersVatTu = new QueryParameters();
                            queryParametersVatTu.setWhereClause(query.toString());
                            String url = dLayerInfo.getUrl();
                            if (!url.startsWith("http"))
                                url = "http:" + dLayerInfo.getUrl();
                            final ServiceFeatureTable serviceFeatureTableVatTu = new ServiceFeatureTable(url);
//
                            final ListenableFuture<FeatureQueryResult> featuresAsyncVatTu =
                                    serviceFeatureTableVatTu.queryFeaturesAsync(queryParametersVatTu,
                                            ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                            featuresAsyncVatTu.addDoneListener(() -> {
                                try {
                                    FeatureQueryResult features = featuresAsyncVatTu.get();
                                    Iterator<Feature> iterator = features.iterator();
                                    Feature item;
                                    while (iterator.hasNext()) {
                                        item = iterator.next();
                                        for (HoSoVatTuSuCo hoSoVatTuSuCo : tempHoSoVatTuSuCo) {
                                            if (hoSoVatTuSuCo.getMaVatTu().equals(item.getAttributes()
                                                    .get(Constant.FIELD_VATTU.MA_VAT_TU).toString())) {
                                                hoSoVatTuSuCos.add(new HoSoVatTuSuCo(hoSoVatTuSuCo.getIdSuCo(),
                                                        hoSoVatTuSuCo.getSoLuong(),
                                                        hoSoVatTuSuCo.getMaVatTu(),
                                                        item.getAttributes().get(Constant.FIELD_VATTU.TEN_VAT_TU).toString(),
                                                        item.getAttributes().get(Constant.FIELD_VATTU.DON_VI_TINH).toString()));
                                            }
                                        }
                                    }
                                    ListObjectDB.getInstance().setHoSoVatTuSuCos(hoSoVatTuSuCos);
                                    publishProgress(hoSoVatTuSuCos);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    publishProgress(hoSoVatTuSuCos);
                                }

                            });

                            break;
                        }
                    }

                } else {

                    publishProgress(hoSoVatTuSuCos);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                publishProgress(hoSoVatTuSuCos);
            }
        });
    }

    private void delete(String idSuCo) {
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = String.format("%s like '%%%s%%' and %s = %d", Constant.FIELD_VATTU.ID_SU_CO, idSuCo,
                Constant.FIELD_VATTU.LOAI_VAT_TU, mLoaiVatTu);
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = this.mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();

                mServiceFeatureTable.deleteFeaturesAsync(result).addDoneListener(() -> {
                    ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
                    listListenableFuture.addDoneListener(() -> {
                        ListObjectDB.getInstance().clearHoSoVatTuSuCos();
                        //Không cần kiểm tra xóa thành công,
                        // bởi vì có thể chưa có vật tư trước đó
                        insert(ListObjectDB.getInstance().getLstHoSoVatTuSuCoInsert());
                    });
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                publishProgress(false);
            }
        });
    }


    private void insert(List<HoSoVatTuSuCo> hoSoVatTuSuCos) {
        List<Feature> features = new ArrayList<>();
        for (HoSoVatTuSuCo hoSoVatTuSuCo : hoSoVatTuSuCos) {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put(Constant.FIELD_VATTU.ID_SU_CO, hoSoVatTuSuCo.getIdSuCo());
            attributes.put(Constant.FIELD_VATTU.MA_VAT_TU, hoSoVatTuSuCo.getMaVatTu());
            attributes.put(Constant.FIELD_VATTU.SO_LUONG, hoSoVatTuSuCo.getSoLuong());

            Feature feature = mServiceFeatureTable.createFeature();
            feature.getAttributes().put(Constant.FIELD_VATTU.ID_SU_CO, hoSoVatTuSuCo.getIdSuCo());
            feature.getAttributes().put(Constant.FIELD_VATTU.MA_VAT_TU, hoSoVatTuSuCo.getMaVatTu());
            feature.getAttributes().put(Constant.FIELD_VATTU.SO_LUONG, hoSoVatTuSuCo.getSoLuong());
            feature.getAttributes().put(Constant.FIELD_VATTU.LOAI_VAT_TU, mLoaiVatTu);
            features.add(feature);
        }
        mServiceFeatureTable.addFeaturesAsync(features).addDoneListener(() -> {
            ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
            listListenableFuture.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableFuture.get();
                    if (featureEditResults.size() > 0) {
                        ListObjectDB.getInstance().clearListHoSoVatTuSuCoChange();
                        publishProgress(true);
                    } else {
                        publishProgress(false);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    publishProgress(false);
                }
            });

        });


    }

    @Override
    protected Void doInBackground(Object... objects) {
        mServiceFeatureTable.loadAsync();
        mServiceFeatureTable.addDoneLoadingListener(() -> {
            if (mServiceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                if (objects != null && objects.length > 0) {
                    switch (Integer.parseInt(objects[0].toString())) {
                        case Constant.HOSOSUCO_METHOD.FIND:
                            if (objects.length > 1 && objects[1] instanceof String) {
                                find(objects[1].toString());
                            }
                            break;
                        case Constant.HOSOSUCO_METHOD.INSERT:
                            if (objects.length > 1 && objects[1] instanceof String) {
                                delete(objects[1].toString());
                            }
                            break;

                    }
                }
            } else {
                publishProgress(false);
                Log.e("Load table", "không loaded");
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

        this.mDelegate.processFinish(values[0]);
    }
}
