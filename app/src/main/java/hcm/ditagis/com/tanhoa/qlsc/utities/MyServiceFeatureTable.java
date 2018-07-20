package hcm.ditagis.com.tanhoa.qlsc.utities;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.loadable.LoadStatus;

public class MyServiceFeatureTable {
    private ServiceFeatureTable SFTLayerHanhChinh;

    private MyServiceFeatureTable(final ArcGISMapImageLayer arcGISMapImageLayer) {
        final ArcGISMapImageSublayer layerHanhChinh = (ArcGISMapImageSublayer) arcGISMapImageLayer.getSublayers().get(3);
        layerHanhChinh.loadAsync();
        layerHanhChinh.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (layerHanhChinh.getLoadStatus() == LoadStatus.LOADED) {
                    SFTLayerHanhChinh = layerHanhChinh.getTable();
                }
            }
        });


    }

    private static MyServiceFeatureTable instance = null;

    public static MyServiceFeatureTable getInstance(ArcGISMapImageLayer arcGISMapImageLayer) {
        if (instance == null) {
            instance = new MyServiceFeatureTable(arcGISMapImageLayer);
        }
        return instance;
    }


    public ServiceFeatureTable getSFTLayerHanhChinh() {
        return SFTLayerHanhChinh;
    }
}
