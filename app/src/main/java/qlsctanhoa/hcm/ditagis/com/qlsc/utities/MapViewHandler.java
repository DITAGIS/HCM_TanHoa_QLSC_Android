package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.R;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler {
    private final ArcGISMap mMap;
    private final FeatureLayer suCoTanHoaLayer;
    private Callout mCallout;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup popupInfos;
    private Context mContext;
    private static double DELTA_MOVE_Y = 7000;

    public MapViewHandler(ArcGISMap mMap, final FeatureLayer suCoTanHoaLayer, Callout mCallout, android.graphics.Point mClickPoint, ArcGISFeature mSelectedArcGISFeature, MapView mMapView, boolean isClickBtnAdd, ServiceFeatureTable mServiceFeatureTable, Popup popupInfos, Context mContext) {
        this.mCallout = mCallout;
        this.mClickPoint = mClickPoint;
        this.mSelectedArcGISFeature = mSelectedArcGISFeature;
        this.mMapView = mMapView;
        this.isClickBtnAdd = isClickBtnAdd;
        this.mServiceFeatureTable = mServiceFeatureTable;
        this.popupInfos = popupInfos;
        this.mContext = mContext;
        this.mMap = mMap;
        this.suCoTanHoaLayer = suCoTanHoaLayer;
    }

    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }


    public void onSingleTapMapView(MotionEvent e) {
        suCoTanHoaLayer.clearSelection();
        if (mCallout.isShowing()) {
            mCallout.dismiss();
        }
        mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        mSelectedArcGISFeature = null;
        // get the point that was clicked and convert it to a point in map coordinates
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        int tolerance = 10;
        double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
        // create objects required to do a selection with a query
        Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mMap.getSpatialReference());
        QueryParameters query = new QueryParameters();
        query.setGeometry(envelope);
        // add done loading listener to fire when the selection returns
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(suCoTanHoaLayer, mClickPoint, 5, false, 1);
        if (isClickBtnAdd) {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put(Constant.FEATURE_ATTRIBUTE_VITRI_SUCO, ""); // Coded Values: [1: Manatee] etc...
            attributes.put(Constant.FEATURE_ATTRIBUTE_TRANGTHAI_SUCO, 0); // Coded Values: [0: No] , [1: Yes]
            attributes.put(Constant.FEATURE_ATTRIBUTE_NGAYCAPNHAT_SUCO, Constant.DATE_FORMAT.format(new Date()));

            final Feature feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(clickPoint);
            ListenableFuture<Void> mapViewResult = mServiceFeatureTable.addFeatureAsync(feature);
            isClickBtnAdd = false;
            mapViewResult.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    mServiceFeatureTable.applyEditsAsync();
                }
            });
        } else {
            mMapView.setViewpointScaleAsync(144447.640625).addDoneListener(new Runnable() {
                @Override
                public void run() {
                    identifyFuture.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // call get on the future to get the result
                                IdentifyLayerResult layerResult = identifyFuture.get();
                                List<GeoElement> resultGeoElements = layerResult.getElements();
                                LayoutInflater layoutInflater = LayoutInflater.from(mContext);

                                if (resultGeoElements.size() > 0 && !isClickBtnAdd) {
                                    if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                        mSelectedArcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                                        // highlight the selected feature
                                        suCoTanHoaLayer.selectFeature(mSelectedArcGISFeature);
                                        Map<String, Object> attr = mSelectedArcGISFeature.getAttributes();

                                        LinearLayout linearLayout = popupInfos.createPopup(mSelectedArcGISFeature, attr);


                                        Envelope envelope = mSelectedArcGISFeature.getGeometry().getExtent();

                                        Envelope envelope1 = new Envelope(new Point(envelope.getXMin(), envelope.getYMin() + DELTA_MOVE_Y), new Point(envelope.getXMax(), envelope.getYMax() + DELTA_MOVE_Y));
                                        mMapView.setViewpointGeometryAsync(envelope1, 0);
                                        // show CallOut
                                        mCallout.setLocation(clickPoint);

                                        mCallout.setContent(linearLayout);
                                        mCallout.refresh();
                                        mCallout.show();
//                                    deleteFeature();
//                                    updateFeature();
                                    }
                                } else {
                                    Toast.makeText(mContext.getApplicationContext(), " click here", Toast.LENGTH_SHORT).show();
                                    // none of the features on the map were selected
                                    mCallout.dismiss();
                                }
                            } catch (Exception e) {
                                Log.e(mContext.getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                            }
                        }
                    });
                }
            });
        }
    }

    public void querySearch(String searchStr) {
        mCallout.dismiss();

        suCoTanHoaLayer.clearSelection();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        for (Field field : mServiceFeatureTable.getFields()) {
            switch (field.getFieldType()) {
                case OID:
                case INTEGER:
                case SHORT:
                    try {
                        int search = Integer.parseInt(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {

                    }
                    break;
                case FLOAT:
                case DOUBLE:
                    try {
                        double search = Double.parseDouble(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {

                    }
                    break;
                case TEXT:
                    builder.append(String.format("%s = '%s'", field.getName(), searchStr));
                    builder.append(" or ");
                    break;
            }
        }
        builder.append(" 1 = 2 ");
        queryParameters.setWhereClause(builder.toString());
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    if (result.iterator().hasNext()) {

                        Feature item = result.iterator().next();

                        Envelope extent = item.getGeometry().getExtent();

                        mMapView.setViewpointGeometryAsync(extent);

                        suCoTanHoaLayer.selectFeature(item);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
