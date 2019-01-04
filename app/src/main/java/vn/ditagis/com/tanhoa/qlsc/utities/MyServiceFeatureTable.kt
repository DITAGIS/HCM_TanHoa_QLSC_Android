package vn.ditagis.com.tanhoa.qlsc.utities

import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer
import com.esri.arcgisruntime.loadable.LoadStatus

class MyServiceFeatureTable private constructor(arcGISMapImageLayer: ArcGISMapImageLayer) {
    var sftLayerHanhChinh: ServiceFeatureTable? = null
        private set

    init {
        val layerHanhChinh = arcGISMapImageLayer.sublayers[3] as ArcGISMapImageSublayer
        layerHanhChinh.loadAsync()
        layerHanhChinh.addDoneLoadingListener {
            if (layerHanhChinh.loadStatus == LoadStatus.LOADED) {
                sftLayerHanhChinh = layerHanhChinh.table
            }
        }


    }

    companion object {

        var instance: MyServiceFeatureTable? = null

        fun getInstance(arcGISMapImageLayer: ArcGISMapImageLayer): MyServiceFeatureTable? {
            if (instance == null) {
                instance = MyServiceFeatureTable(arcGISMapImageLayer)
            }
            return instance
        }
    }
}
