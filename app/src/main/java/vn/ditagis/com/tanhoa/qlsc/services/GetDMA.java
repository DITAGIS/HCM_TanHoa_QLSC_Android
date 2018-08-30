package vn.ditagis.com.tanhoa.qlsc.services;

import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;


public class GetDMA extends AsyncTask<Void, Boolean, Void> {
    private Context mContext;

    private AsyncResponse mDelegate;

    public interface AsyncResponse {

        void processFinish();
    }

    public GetDMA(Context context, AsyncResponse delegate) {
        this.mContext = context;
        this.mDelegate = delegate;
    }


    private void getMaDMAFromService() {

        String layerInfoVatTu = mContext.getString(R.string.LayerInfo_DMA);

        for (DLayerInfo dLayerInfo : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (dLayerInfo.getId().equals(layerInfoVatTu)) {
                final QueryParameters queryParameters = new QueryParameters();
                queryParameters.setWhereClause("1=1");
                String url = dLayerInfo.getUrl();
                if (!url.startsWith("http"))
                    url = "http:" + dLayerInfo.getUrl();
                final ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
//
                final ListenableFuture<FeatureQueryResult> feature = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                feature.addDoneListener(() -> {
                    final List<String> dmaList = new ArrayList<>();
                    try {
                        FeatureQueryResult result = feature.get();
                        Iterator<Feature> iterator = result.iterator();
                        Feature item;
                        while (iterator.hasNext()) {
                            item = iterator.next();
                            Object dma = item.getAttributes().get(mContext.getString(R.string.field_DMA_maDMA));
                            if (dma != null)
                                dmaList.add(dma.toString());
                        }
                        ListObjectDB.getInstance().setDmas(dmaList);
                        publishProgress(true);

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        publishProgress();
                    }


                });

                break;
            }
        }

    }


    @Override
    protected Void doInBackground(Void... voids) {
        getMaDMAFromService();
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if(values!= null && values.length >0 && values[0])
            mDelegate.processFinish();
    }
}
