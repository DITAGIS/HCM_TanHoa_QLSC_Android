package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
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
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import qlsctanhoa.hcm.ditagis.com.qlsc.R;
import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.TraCuuAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.libs.FeatureLayerDTG;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {

    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;
    private static double DELTA_MOVE_Y = 0;//7000;
    private final ArcGISMap mMap;
    private final FeatureLayer suCoTanHoaLayer;
    LocatorTask loc = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private FeatureLayerDTG mFeatureLayerDTG;
    private Callout mCallout;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private MapView mMapView;
    private boolean isClickBtnAdd = false;
    private ServiceFeatureTable mServiceFeatureTable;
    private Popup popupInfos;
    private Context mContext;
    private Uri mUri;

    public MapViewHandler(FeatureLayerDTG featureLayerDTG, Callout mCallout, MapView mMapView,
                          Popup popupInfos, Context mContext) {
        this.mFeatureLayerDTG = featureLayerDTG;
        this.mCallout = mCallout;
        this.mMapView = mMapView;
        this.mServiceFeatureTable = (ServiceFeatureTable) featureLayerDTG.getFeatureLayer().getFeatureTable();
        this.popupInfos = popupInfos;
        this.mContext = mContext;
        this.mMap = mMapView.getMap();
        this.suCoTanHoaLayer = featureLayerDTG.getFeatureLayer();
    }

    public void setClickBtnAdd(boolean clickBtnAdd) {
        isClickBtnAdd = clickBtnAdd;
    }

    public void addFeature(byte[] image) {
        SingleTapAdddFeatureAsync singleTapAdddFeatureAsync = new SingleTapAdddFeatureAsync(mContext, image);
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
            if (mCallout.isShowing()) {
                mCallout.dismiss();
            }
            mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
            mSelectedArcGISFeature = null;
            // get the point that was clicked and convert it to a point in map coordinates
            int tolerance = 10;
            double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
            // create objects required to do a selection with a query
            Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance, clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mMap.getSpatialReference());
            QueryParameters query = new QueryParameters();
            query.setGeometry(envelope);
            // add done loading listener to fire when the selection returns

            SingleTapMapViewAsync singleTapMapViewAsync = new SingleTapMapViewAsync(mContext);
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

    class SingleTapMapViewAsync extends AsyncTask<Point, Void, Void> {
        private ProgressDialog mDialog;
        private Context mContext;

        public SingleTapMapViewAsync(Context context) {
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
            final Point clickPoint = params[0];
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(suCoTanHoaLayer, mClickPoint, 5, false, 1);
            identifyFuture.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        IdentifyLayerResult layerResult = identifyFuture.get();
                        List<GeoElement> resultGeoElements = layerResult.getElements();
                        if (resultGeoElements.size() > 0) {
                            if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                mSelectedArcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                                // highlight the selected feature
                                FeatureLayer featureLayer = mFeatureLayerDTG.getFeatureLayer();
                                featureLayer.clearSelection();
                                if (mFeatureLayerDTG.getTitleLayer().equals("Điểm sự cố")) {
                                    featureLayer.selectFeature(mSelectedArcGISFeature);
                                    popupInfos.setFeatureLayerDTG(mFeatureLayerDTG);
                                    LinearLayout linearLayout = popupInfos.createPopup(mSelectedArcGISFeature);
                                    Envelope envelope = mSelectedArcGISFeature.getGeometry().getExtent();
                                    Envelope envelope1 = new Envelope(new Point(envelope.getXMin(), envelope.getYMin() + DELTA_MOVE_Y), new Point(envelope.getXMax(), envelope.getYMax() + DELTA_MOVE_Y));
                                    mMapView.setViewpointGeometryAsync(envelope1, 0);
                                    // show CallOut
                                    mCallout.setLocation(clickPoint);
                                    mCallout.setContent(linearLayout);
                                    popupInfos.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mCallout.refresh();
                                            mCallout.show();
                                        }
                                    });
                                }
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

    class SingleTapAdddFeatureAsync extends AsyncTask<Point, Void, Void> {
        private ProgressDialog mDialog;
        private Context mContext;
        private byte[] mImage;

        public SingleTapAdddFeatureAsync(Context context, byte[] image) {
            mImage = image;
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
            final Point clickPoint = params[0];
            final Feature feature = mServiceFeatureTable.createFeature();
            feature.setGeometry(clickPoint);
            final ListenableFuture<List<GeocodeResult>> listListenableFuture =
                    loc.reverseGeocodeAsync(clickPoint);
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
                        String timeID = "";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            dateTime = getDateString();
                            timeID = getTimeID();
                            searchStr = Constant.IDSU_CO + " like '%" + timeID + "'";
                        }
                        QueryParameters queryParameters = new QueryParameters();
                        queryParameters.setWhereClause(searchStr);
                        final ListenableFuture<FeatureQueryResult> featureQuery =
                                mServiceFeatureTable.queryFeaturesAsync(queryParameters);
                        final String finalDateTime = dateTime;
                        final String finalTimeID = timeID;
                        featureQuery.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                addFeatureAsync(featureQuery, feature, finalTimeID, finalDateTime);
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

        private void addFeatureAsync(ListenableFuture<FeatureQueryResult> featureQuery,
                                     Feature feature, String finalTimeID, String finalDateTime) {
            try {
                // lấy id lớn nhất
                int id_tmp;
                int id = 0;
                FeatureQueryResult result = featureQuery.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature item = (Feature) iterator.next();
                    id_tmp = Integer.parseInt(item.getAttributes().get(Constant.IDSU_CO).toString().split("_")[0]);
                    if (id_tmp > id) id = id_tmp;
                }
                id++;
                feature.getAttributes().put(Constant.IDSU_CO, id + "_" + finalTimeID);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Date date = Constant.DATE_FORMAT.parse(finalDateTime);
                    Calendar c = Calendar.getInstance();
                    feature.getAttributes().put(Constant.NGAY_CAP_NHAT, c);
                    feature.getAttributes().put(Constant.NGAY_THONG_BAO, c);
                }
                ListenableFuture<Void> mapViewResult = mServiceFeatureTable.addFeatureAsync(feature);
                mapViewResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mServiceFeatureTable.applyEditsAsync();
                        listListenableEditAsync.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                                    if (featureEditResults.size() > 0) {
                                        long objectId = featureEditResults.get(0).getObjectId();
                                        final QueryParameters queryParameters = new QueryParameters();
                                        final String query = "OBJECTID = " + objectId;
                                        queryParameters.setWhereClause(query);
                                        final ListenableFuture<FeatureQueryResult> feature = mServiceFeatureTable.queryFeaturesAsync(queryParameters);
                                        feature.addDoneListener(new Runnable() {
                                            @Override
                                            public void run() {
                                                addAttachment(feature);
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
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

        private void addAttachment(ListenableFuture<FeatureQueryResult> feature) {
            FeatureQueryResult result = null;
            try {
                result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    mSelectedArcGISFeature = (ArcGISFeature) item;
                    final String attachmentName = mContext.getString(R.string.attachment) + "_" + System.currentTimeMillis() + ".png";
                    final ListenableFuture<Attachment> addResult = mSelectedArcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName);
                    addResult.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            try {
                                Attachment attachment = addResult.get();
                                if (attachment.getSize() > 0) {
                                    final ListenableFuture<Void> tableResult = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
                                    tableResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            final ListenableFuture<List<FeatureEditResult>> updatedServerResult = mServiceFeatureTable.applyEditsAsync();
                                            updatedServerResult.addDoneListener(new Runnable() {
                                                @Override
                                                public void run() {
                                                    List<FeatureEditResult> edits = null;
                                                    try {
                                                        edits = updatedServerResult.get();
                                                        if (edits.size() > 0) {
                                                            if (!edits.get(0).hasCompletedWithErrors()) {
                                                                //attachmentList.add(fileName);
                                                                String s = mSelectedArcGISFeature.getAttributes().get("objectid").toString();
                                                                // update the attachment list view on the control panel
                                                            } else {
                                                            }
                                                        } else {
                                                        }
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    } catch (ExecutionException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (mDialog != null && mDialog.isShowing()) {
                                                        mDialog.dismiss();
                                                    }

                                                }
                                            });


                                        }
                                    });
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Envelope extent = item.getGeometry().getExtent();
                    mMapView.setViewpointGeometryAsync(extent);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


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