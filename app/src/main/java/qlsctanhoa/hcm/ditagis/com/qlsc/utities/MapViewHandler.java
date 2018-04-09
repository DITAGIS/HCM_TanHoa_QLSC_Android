package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.R;
import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.TraCuuAdapter;


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
    private static double DELTA_MOVE_Y = 0;//7000;
    private AddAsync mAddasync;
    LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");

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
        this.mAddasync = new AddAsync(mContext);
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
            this.mAddasync.execute(clickPoint);
        } else {
//            mMapView.setViewpointScaleAsync(144447.640625).addDoneListener(new Runnable() {
//                @Override
//                public void run() {
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
                            // none of the features on the map were selected
                            mCallout.dismiss();
                        }
                    } catch (Exception e) {
                        Log.e(mContext.getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
                    }
                }
            });
//                }
//            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateString() {
        String timeStamp = Constant.DATE_FORMAT.format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public void queryByObjectID(int objectID) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause("OBJECTID = " + objectID);
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

    public void querySearch(String searchStr, ListView listView, final TraCuuAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
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


                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    class AddAsync extends AsyncTask<Point, Void, Void> {
        private ProgressDialog mDialog;
        private Context mContext;

        public AddAsync(Context context) {
            mContext = context;
            mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Đang xử lý...");
            mDialog.setCancelable(false);

            mDialog.show();

        }

        @Override
        protected Void doInBackground(Point... params) {
            Point clickPoint = params[0];
            final Feature feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(clickPoint);

            final ListenableFuture<List<GeocodeResult>> listListenableFuture = loc.reverseGeocodeAsync(clickPoint);
            listListenableFuture.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<GeocodeResult> geocodeResults = listListenableFuture.get();
                        if (geocodeResults.size() > 0) {

                            GeocodeResult geocodeResult = geocodeResults.get(0);
                            Map<String, Object> attrs = new HashMap<>();
                            for (String key : geocodeResult.getAttributes().keySet()) {
                                attrs.put(key, geocodeResult.getAttributes().get(key));
                            }
                            String address = geocodeResult.getAttributes().get("LongLabel").toString();
                            feature.getAttributes().put(Constant.VI_TRI, address);
                        }
                        Short intObj = new Short((short) 0);

                        feature.getAttributes().put(Constant.TRANG_THAI, intObj);

                        String searchStr = "";
                        String dateTime = "";

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            dateTime = getDateString();
                            searchStr = Constant.IDSU_CO + " like '%" + dateTime + "'";
                        }
                        QueryParameters queryParameters = new QueryParameters();

                        queryParameters.setWhereClause(searchStr);
                        final ListenableFuture<FeatureQueryResult> featureQuery = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
                        final String finalDateTime = dateTime;
                        featureQuery.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    int id_tmp = 0;
                                    int id = 0;
                                    FeatureQueryResult result = featureQuery.get();
                                    Iterator iterator = result.iterator();
                                    while (iterator.hasNext()) {

                                        Feature item = (Feature) iterator.next();
                                        id_tmp = Integer.parseInt(item.getAttributes().get(Constant.IDSU_CO).toString().split("_")[0]);
                                        if (id_tmp > id)
                                            id = id_tmp;
                                    }
                                    id++;
                                    feature.getAttributes().put(Constant.IDSU_CO, id + "_" + finalDateTime);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Date date = Constant.DATE_FORMAT.parse(finalDateTime);
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(date);
                                        feature.getAttributes().put(Constant.NGAY_CAP_NHAT, c);
                                        feature.getAttributes().put(Constant.NGAY_THONG_BAO, c);
                                    }
                                    ListenableFuture<Void> mapViewResult = mServiceFeatureTable.addFeatureAsync(feature);
                                    isClickBtnAdd = false;
                                    mapViewResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            mServiceFeatureTable.applyEditsAsync().addDoneListener(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (mDialog != null && mDialog.isShowing()) {
                                                        mDialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });


                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } catch (ExecutionException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }

}
