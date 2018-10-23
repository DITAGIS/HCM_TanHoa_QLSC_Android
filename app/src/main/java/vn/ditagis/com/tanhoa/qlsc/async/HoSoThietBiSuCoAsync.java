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
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;


public class HoSoThietBiSuCoAsync extends AsyncTask<Object, Object, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private DApplication mApplication;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Object object);
    }

    public HoSoThietBiSuCoAsync(Activity activity, AsyncResponse response) {
        this.mActivity = activity;
        this.mApplication = (DApplication) activity.getApplication();
        this.mDelegate = response;
        mServiceFeatureTable = mApplication.getDFeatureLayer.getServiceFeatureTableHoSoThietBiSuCo();
    }

    private void find(String idSuCo) {
        QueryParameters queryParameters = new QueryParameters();
        final List<HoSoThietBiSuCo> hoSoThietBiSuCos = new ArrayList<>();

        String queryClause = String.format("%s like '%%%s%%'", Constant.FIELD_THIETBI.ID_SU_CO, idSuCo);
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture =
                this.mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();
                if (result.iterator().hasNext()) {
                    StringBuilder query = new StringBuilder();
                    List<HoSoThietBiSuCo> tempHoSoThietBiSuCos = new ArrayList<>();
                    for (Iterator it = result.iterator(); it.hasNext(); ) {
                        Feature feature = (Feature) it.next();
                        Map<String, Object> attributes = feature.getAttributes();
                        String maThietBi = attributes.get(Constant.FIELD_THIETBI.MA_THIET_BI).toString();
                        query.append(String.format("%s = '%s' or ", Constant.FIELD_THIETBI.MA_THIET_BI, maThietBi));
                        tempHoSoThietBiSuCos.add(new HoSoThietBiSuCo(attributes.get(Constant.FIELD_THIETBI.ID_SU_CO).toString(),
                                Double.parseDouble(attributes.get(Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH).toString()),
                                maThietBi, ""));
                    }
                    query.append("1 = 0");
                    for (DLayerInfo dLayerInfo : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
                        if (dLayerInfo.getId().equals(Constant.IDLayer.ID_SU_CO_THIET_BI_TABLE)) {
                            final QueryParameters queryParametersThietBi = new QueryParameters();
                            queryParametersThietBi.setWhereClause(query.toString());
                            String url = dLayerInfo.getUrl();
                            if (!url.startsWith("http"))
                                url = "http:" + dLayerInfo.getUrl();
                            final ServiceFeatureTable serviceFeatureTableThietBi = new ServiceFeatureTable(url);
//
                            final ListenableFuture<FeatureQueryResult> featuresAsyncThietBi =
                                    serviceFeatureTableThietBi.queryFeaturesAsync(queryParametersThietBi,
                                            ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                            featuresAsyncThietBi.addDoneListener(() -> {
                                try {
                                    FeatureQueryResult features = featuresAsyncThietBi.get();
                                    Iterator<Feature> iterator = features.iterator();
                                    Feature item;
                                    while (iterator.hasNext()) {
                                        item = iterator.next();
                                        for (HoSoThietBiSuCo hoSoThietBiSuCo : tempHoSoThietBiSuCos) {
                                            if (hoSoThietBiSuCo.getMaThietBi().equals(item.getAttributes()
                                                    .get(Constant.FIELD_THIETBI.MA_THIET_BI).toString())) {
                                                hoSoThietBiSuCos.add(new HoSoThietBiSuCo(hoSoThietBiSuCo.getIdSuCo(),
                                                        hoSoThietBiSuCo.getThoigianVanHanh(),
                                                        hoSoThietBiSuCo.getMaThietBi(),
                                                        item.getAttributes().get(Constant.FIELD_THIETBI.TEN_THIET_BI).toString()));
                                            }
                                        }

                                    }
                                    ListObjectDB.getInstance().setHoSoThietBiSuCos(hoSoThietBiSuCos);
                                    publishProgress(hoSoThietBiSuCos);
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                    publishProgress(hoSoThietBiSuCos);
                                }

                            });

                            break;
                        }
                    }

                } else {

                    publishProgress(hoSoThietBiSuCos);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                publishProgress(hoSoThietBiSuCos);
            }
        });
    }

    private void delete(String idSuCo) {
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = String.format("%s like '%%%s%%'", Constant.FIELD_THIETBI.ID_SU_CO, idSuCo);
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = this.mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();

                mServiceFeatureTable.deleteFeaturesAsync(result).addDoneListener(() -> {
                    ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
                    listListenableFuture.addDoneListener(() -> {
                        ListObjectDB.getInstance().clearHoSoThietBiSuCos();
                        //Không cần kiểm tra xóa thành công,
                        // bởi vì có thể chưa có vật tư trước đó
                        insert(ListObjectDB.getInstance().getLstHoSoThietBiSuCoInsert());
                    });
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                publishProgress(false);
            }
        });
    }


    private void insert(List<HoSoThietBiSuCo> hoSoThietBiSuCos) {
        List<Feature> features = new ArrayList<>();
        for (HoSoThietBiSuCo hoSoThietBiSuCo : hoSoThietBiSuCos) {
//            Map<String, Object> attributes = new HashMap<>();
//            attributes.put(Constant.FIELD_THIETBI.ID_SU_CO, hoSoThietBiSuCo.getIdSuCo());
//            attributes.put(Constant.FIELD_THIETBI.MA_THIET_BI, hoSoThietBiSuCo.getMaThietBi());
//            attributes.put(Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH,(int) hoSoThietBiSuCo.getThoigianVanHanh());

            Feature feature = mServiceFeatureTable.createFeature();
            feature.getAttributes().put(Constant.FIELD_THIETBI.ID_SU_CO, hoSoThietBiSuCo.getIdSuCo());
            feature.getAttributes().put(Constant.FIELD_THIETBI.MA_THIET_BI, hoSoThietBiSuCo.getMaThietBi());
            feature.getAttributes().put(Constant.FIELD_THIETBI.THOI_GIAN_VAN_HANH, hoSoThietBiSuCo.getThoigianVanHanh());
            features.add(feature);
        }
        mServiceFeatureTable.addFeaturesAsync(features).addDoneListener(() -> {
            ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
            listListenableFuture.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableFuture.get();
                    if (featureEditResults.size() > 0) {
                        ListObjectDB.getInstance().clearListHoSoThietBiSuCoChange();
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
