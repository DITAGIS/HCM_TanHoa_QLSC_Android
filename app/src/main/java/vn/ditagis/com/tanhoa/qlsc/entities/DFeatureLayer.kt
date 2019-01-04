package vn.ditagis.com.tanhoa.qlsc.entities


import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

class DFeatureLayer {


    var layer: FeatureLayer? = null
    var layerInfoDTG: DLayerInfo? = null
    var serviceFeatureTableSuCoThongTin: ServiceFeatureTable? = null
    var serviceFeatureTableHoSoVatTuSuCo: ServiceFeatureTable? = null
    var serviceFeatureTableSuCoThietBi: ServiceFeatureTable? = null
    var serviceFeatureTableHoSoThietBiSuCo: ServiceFeatureTable? = null

}
