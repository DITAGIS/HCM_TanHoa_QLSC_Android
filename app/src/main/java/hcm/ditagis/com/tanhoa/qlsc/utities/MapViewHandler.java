package hcm.ditagis.com.tanhoa.qlsc.utities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import hcm.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import hcm.ditagis.com.tanhoa.qlsc.async.SingleTapAddFeatureAsync;
import hcm.ditagis.com.tanhoa.qlsc.async.SingleTapMapViewAsync;
import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {

    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static double DELTA_MOVE_Y = 0;//7000;
    private final FeatureLayer suCoTanHoaLayer;
    LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private FeatureLayerDTG mFeatureLayerDTG;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup mPopUp;
    private Context mContext;


    public void setFeatureLayerDTGs(List<FeatureLayerDTG> mFeatureLayerDTGs) {
        this.mFeatureLayerDTGs = mFeatureLayerDTGs;
    }

    private List<FeatureLayerDTG> mFeatureLayerDTGs;

    public MapViewHandler(FeatureLayerDTG featureLayerDTG, MapView mMapView, Popup popupInfos, Context mContext) {
        this.mFeatureLayerDTG = featureLayerDTG;
        this.mMapView = mMapView;
        this.mServiceFeatureTable = (ServiceFeatureTable) featureLayerDTG.getFeatureLayer().getFeatureTable();
        this.mPopUp = popupInfos;
        this.mContext = mContext;
        this.suCoTanHoaLayer = featureLayerDTG.getFeatureLayer();
    }

    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }

    public void addFeature(byte[] image) {
        SingleTapAddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAddFeatureAsync(mContext, image, mServiceFeatureTable, loc, mMapView);
        Point add_point = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        singleTapAdddFeatureAsync.execute(add_point);
    }


    public double[] onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Point center = ((MapView) mMapView).getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
//        Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        if (isClickBtnAdd) {
            mMapView.setViewpointCenterAsync(clickPoint, 10);
        } else {
            suCoTanHoaLayer.clearSelection();
            mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
            mSelectedArcGISFeature = null;
            // get the point that was clicked and convert it to a point in map coordinates
            int tolerance = 10;
            double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
            // create objects required to do a selection with a query
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mMapView.getMap().getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            // add done loading listener to fire when the selection returns

            SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mContext, mFeatureLayerDTGs, mPopUp, mClickPoint, mMapView);
            singleTapMapViewAsync.execute(clickPoint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateString() {
        String timeStamp = Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());

        SimpleDateFormat writeDate = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
        writeDate.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        String timeStamp1 = writeDate.format(Calendar.getInstance().getTime());
        return timeStamp1;
    }

    private String getTimeID() {
        String timeStamp = Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public void queryByObjectID(int objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = feature.get();
                    if (result.iterator().hasNext()) {
                        mSelectedArcGISFeature = (ArcGISFeature) result.iterator().next();
                        mPopUp.setFeatureLayerDTG(mFeatureLayerDTG);
                        if (mSelectedArcGISFeature != null)
                            mPopUp.showPopup(mSelectedArcGISFeature);
                        else mPopUp.dimissCallout();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void querySearch(String searchStr, ListView listView, final TraCuuAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
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
                    builder.append(field.getName() + " like N'%" + searchStr + "%'");
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
                    Iterator iterator = result.iterator();
                    while (iterator.hasNext()) {
                        Feature item = (Feature) iterator.next();
                        Map<String, Object> attributes = item.getAttributes();
                        String format_date = "";
                        String[] split = attributes.get(Constant.IDSU_CO).toString().split("_");
                        try {
                            format_date = Constant.DATE_FORMAT.format((new GregorianCalendar(Integer.parseInt(split[3]), Integer.parseInt(split[2]), Integer.parseInt(split[1])).getTime()));
                        } catch (Exception e) {

                        }
                        String viTri = "";
                        try {
                            viTri = attributes.get(Constant.VI_TRI).toString();
                        } catch (Exception e) {

                        }
                        adapter.add(new TraCuuAdapter.Item(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()), attributes.get(Constant.IDSU_CO).toString(), Integer.parseInt(attributes.get(Constant.TRANG_THAI).toString()), format_date, viTri));
                        adapter.notifyDataSetChanged();

//                        queryByObjectID(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()));
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