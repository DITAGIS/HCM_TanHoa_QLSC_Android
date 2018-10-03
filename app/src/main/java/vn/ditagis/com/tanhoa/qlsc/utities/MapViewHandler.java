package vn.ditagis.com.tanhoa.qlsc.utities;

import android.app.Activity;
import android.view.MotionEvent;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vn.ditagis.com.tanhoa.qlsc.R;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableAsync;
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapAddFeatureAsync;
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapMapViewAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;

/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static double DELTA_MOVE_Y = 0;//7000;
    private FeatureLayer suCoTanHoaLayerThiCong;
    private Callout mCallout;
    private android.graphics.Point mClickPoint;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup mPopUp;
    private Activity mActivity;
    private DApplication mApplication;

    public MapViewHandler(Callout callout, MapView mapView,
                          Popup popupInfos, Activity activity) {
        this.mActivity = activity;
        mApplication = (DApplication) activity.getApplication();
        this.mCallout = callout;
        this.mMapView = mapView;
        if (mApplication.getDFeatureLayer.getLayer() != null) {
            this.mServiceFeatureTable = (ServiceFeatureTable) mApplication.getDFeatureLayer.getLayer().getFeatureTable();
            this.suCoTanHoaLayerThiCong = mApplication.getDFeatureLayer.getLayer();
        }
        this.mPopUp = popupInfos;
//        this.isThiCong = KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(mActivity.getString(R.string.group_role_thicong));
    }


    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }

    public void addFeature(Point pointFindLocation) {
        mClickPoint = mMapView.locationToScreen(pointFindLocation);

        SingleTapAddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAddFeatureAsync(mActivity,
                mServiceFeatureTable, output -> {
            if (output != null) {
                if (mCallout != null && mCallout.isShowing())
                    mCallout.dismiss();
//                mPopUp.showPopup((ArcGISFeature) output, true);
            }
        });
        singleTapAdddFeatureAsync.execute();
    }

    public double[] onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Point center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
        mClickPoint = new android.graphics.Point((int) e2.getX(), (int) e2.getY());
//        Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
        return location;
    }

    public void onSingleTapMapView(MotionEvent e) {
        final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
        mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        if (isClickBtnAdd) {
            mMapView.setViewpointCenterAsync(clickPoint);
        } else {

            SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mActivity, mPopUp, mClickPoint, mMapView);
            singleTapMapViewAsync.execute(clickPoint);
        }
    }


//    public void queryByObjectID(int objectID) {
//        final QueryParameters queryParameters = new QueryParameters();
//        final String query = "OBJECTID = " + objectID;
//        queryParameters.setWhereClause(query);
//        final ListenableFuture<FeatureQueryResult> feature;
//        feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
//        feature.addDoneListener(() -> {
//            try {
//                FeatureQueryResult result = feature.get();
//                if (result.iterator().hasNext()) {
//                    Feature item = result.iterator().next();
//                    Envelope extent = item.getGeometry().getExtent();
//mApplication.setGeometry(item.getGeometry());
//                    mMapView.setViewpointGeometryAsync(extent);
//                    suCoTanHoaLayerThiCong.selectFeature(item);
//                    if (mApplication.getDFeatureLayer.getLayer() != null) {
//                        mSelectedArcGISFeature = (ArcGISFeature) item;
//                        if (mSelectedArcGISFeature != null) {
//                            mApplication.setArcGISFeature(mSelectedArcGISFeature);
//                            mPopUp.showPopup(mSelectedArcGISFeature, false);
//                        }
//                    }
//                }
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        });
//    }

    public void query(String query) {

        final QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature;
        feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    if (item.getGeometry() != null) {
                        Envelope extent = item.getGeometry().getExtent();
                        mApplication.setGeometry(item.getGeometry());
                        mMapView.setViewpointGeometryAsync(extent);
                    }
                    suCoTanHoaLayerThiCong.selectFeature(item);
                    if (mApplication.getDFeatureLayer.getLayer() != null) {
                        String queryClause = String.format("%s = '%s' and %s = '%s'",
                                Constant.FIELD_SUCOTHONGTIN.ID_SUCO, item.getAttributes().get(Constant.FIELD_SUCO.ID_SUCO).toString(),
                                Constant.FIELD_SUCOTHONGTIN.NHAN_VIEN, mApplication.getUserDangNhap().getUserName());
                        QueryParameters queryParameters1 = new QueryParameters();
                        queryParameters1.setWhereClause(queryClause);
                        new QueryServiceFeatureTableAsync(mActivity,
                                mApplication.getDFeatureLayer.getServiceFeatureTableSuCoThongTin(), output -> {
                            if (output != null) {
                                mApplication.setArcGISFeature((ArcGISFeature) output);
                                mPopUp.showPopup();
                            }
                        }).execute(queryParameters1);

                    }
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }


    public void querySearch(String searchStr, final TraCuuAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        mCallout.dismiss();

        suCoTanHoaLayerThiCong.clearSelection();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        builder.append("DiaChi  like N'%").append(searchStr).append("%'")
                .append(" or IDSuCo like '%").append(searchStr).append("%'");
        queryParameters.setWhereClause(builder.toString());
        final ListenableFuture<FeatureQueryResult> featureQueryResult = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResult.addDoneListener(() -> {
            try {
                FeatureQueryResult result = featureQueryResult.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature item = (Feature) iterator.next();
                    Map<String, Object> attributes = item.getAttributes();
                    String format_date = "";
                    String[] split = attributes.get(Constant.FIELD_SUCO.ID_SUCO).toString().split("_");
                    try {
                        format_date = Constant.DATE_FORMAT.format((new GregorianCalendar(Integer.parseInt(split[3]), Integer.parseInt(split[2]), Integer.parseInt(split[1])).getTime()));
                    } catch (Exception ignored) {

                    }
                    String viTri = "";
                    try {
                        viTri = attributes.get(Constant.FIELD_SUCO.DIA_CHI).toString();
                    } catch (Exception ignored) {

                    }
                    int objectID = Integer.parseInt(attributes.get(mActivity.getString(R.string.Field_OBJECTID)).toString());
                    boolean isFound = false;
                    for (TraCuuAdapter.Item itemTraCuu : adapter.getItems()) {
                        if (itemTraCuu.getObjectID() == objectID) {
                            isFound = true;
                            break;
                        }
                    }
                    if (!isFound)
                        adapter.add(new TraCuuAdapter.Item(objectID,
                                attributes.get(Constant.FIELD_SUCO.ID_SUCO).toString(),
                                Integer.parseInt(attributes.get(Constant.FIELD_SUCO.TRANG_THAI).toString()), format_date, viTri));
                    adapter.notifyDataSetChanged();

//                        queryByObjectID(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()));
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }
}

