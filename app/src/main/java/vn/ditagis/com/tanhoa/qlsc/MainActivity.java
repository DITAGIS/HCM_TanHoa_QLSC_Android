package vn.ditagis.com.tanhoa.qlsc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.esri.arcgisruntime.util.ListenableList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter;
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import vn.ditagis.com.tanhoa.qlsc.async.EditAsync;
import vn.ditagis.com.tanhoa.qlsc.async.FindLocationAsycn;
import vn.ditagis.com.tanhoa.qlsc.async.LoadLegendAsycn;
import vn.ditagis.com.tanhoa.qlsc.async.PreparingByAPIAsycn;
import vn.ditagis.com.tanhoa.qlsc.async.QueryFeatureGetListGeometryAsync;
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableGetListAsync;
import vn.ditagis.com.tanhoa.qlsc.entities.Constant;
import vn.ditagis.com.tanhoa.qlsc.entities.DAddress;
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication;
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.ListObjectDB;
import vn.ditagis.com.tanhoa.qlsc.socket.LocationHelper;
import vn.ditagis.com.tanhoa.qlsc.utities.APICompleteAsync;
import vn.ditagis.com.tanhoa.qlsc.utities.CheckConnectInternet;
import vn.ditagis.com.tanhoa.qlsc.utities.MapViewHandler;
import vn.ditagis.com.tanhoa.qlsc.utities.MyServiceFeatureTable;
import vn.ditagis.com.tanhoa.qlsc.utities.MySnackBar;
import vn.ditagis.com.tanhoa.qlsc.utities.Popup;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {
    @BindView(R.id.floatBtnLayer)
    FloatingActionButton mFloatButtonLayer;
    @BindView(R.id.floatBtnLocation)
    FloatingActionButton mFloatButtonLocation;
    @BindView(R.id.llayout_main_layer)
    LinearLayout mLLayoutLayer;
    @BindView(R.id.linearDisplayLayerFeature)
    LinearLayout mLayoutDisplayLayerThematic;
    @BindView(R.id.linearDisplayLayerAdministration)
    LinearLayout mLayoutDisplayLayerAdministration;
    @BindView(R.id.linearDisplayLayerLegend)
    LinearLayout mLayoutLegend;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.lstview_search)
    ListView mListViewSearch;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.skbr_hanhchinh_app_bar_quan_ly_su_co)
    SeekBar mSeekBarAdministrator;
    @BindView(R.id.skbr_chuyende_app_bar_quan_ly_su_co)
    SeekBar mSeekBarThematic;
    @BindView(R.id.llayout_layer_open_street_map)
    LinearLayout mLLayoutOSM;
    @BindView(R.id.llayout_layer_street_map)
    LinearLayout mLLayoutSM;
    @BindView(R.id.llayout_layer_image)
    LinearLayout mLLayoutImage;
    @BindView(R.id.txt_layer_open_street_map)
    TextView mTxtOSM;
    @BindView(R.id.txt_layer_street_map)
    TextView mTxtSM;
    @BindView(R.id.txt_layer_image)
    TextView mTxtImageWithLabel;
    @BindView(R.id.img_layer_open_street_map)
    ImageView mImageOpenStreetMap;
    @BindView(R.id.img_layer_street_map)
    ImageView mImageStreetMap;
    @BindView(R.id.img_layer_image)
    ImageView mImageImageWithLabel;
    @BindView(R.id.btn_layer_close)
    Button mBtnLayerClose;
    @BindView(R.id.layout_tim_su_co)
    LinearLayout mLayoutTimSuCo;
    @BindView(R.id.layout_tim_dia_chi)
    LinearLayout mLayoutTimDiaChi;
    @BindView(R.id.layout_tim_kiem)
    LinearLayout mLayoutTimKiem;
    TextView mTxtHeaderTenNV;
    TextView mTxtHeaderDisplayName;

    private Uri mUri;
    private Popup mPopUp;
    private MapViewHandler mMapViewHandler;
    private TraCuuAdapter mSearchAdapter;
    private LocationDisplay mLocationDisplay;
    private GraphicsOverlay mGraphicsOverlay;
    private boolean mIsSearchingFeature = false;


    private Point mPointFindLocation;
    private Geocoder mGeocoder;
    private boolean mIsAddFeature;

    private SearchView mTxtSearchView;
    private ArcGISMapImageLayer mArcGISMapImageLayerAdministrator, mArcGISMapImageLayerThematic;
    private List<String> mListLayerID;
    private int states[][];
    private int colors[];
    private DApplication mApplication;
    private int mLoadedOnMap;
    @BindView(R.id.txt_appbar_info)
    TextView mTxtInfo;
    @BindView(R.id.llayout_info_app_bar)
    LinearLayout mLLayoutInfo;
    List<String> mIDSuCoList;

    public void setUri(Uri uri) {
        this.mUri = uri;
    }


    public void setFeatureViewMoreInfoAdapter(FeatureViewMoreInfoAdapter featureViewMoreInfoAdapter) {
        this.mFeatureViewMoreInfoAdapter = featureViewMoreInfoAdapter;
    }

    private LocationHelper mLocationHelper;
    private Location mLocation;
    private FeatureViewMoreInfoAdapter mFeatureViewMoreInfoAdapter;

    public void setSelectedArcGISFeature(ArcGISFeature selectedArcGISFeature) {
        this.mSelectedArcGISFeature = selectedArcGISFeature;

    }

    public void setIsAddFeature(boolean isAddFeature) {
        mIsAddFeature = isAddFeature;
    }

    private static final int REQUEST_SEARCH = 1;

    private ArcGISFeature mSelectedArcGISFeature;
    private boolean mIsFirstLocating = true, mIsShowComplete = false;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_su_co);
        mListLayerID = new ArrayList<>();
        ButterKnife.bind(this);
        if (mNavView != null && mNavView.getHeaderCount() > 0) {
            View header = mNavView.getHeaderView(0);
            mTxtHeaderTenNV = header.findViewById(R.id.txt_nav_header_tenNV);
            mTxtHeaderDisplayName = header.findViewById(R.id.txt_nav_header_displayname);
        }
        states = new int[][]{{android.R.attr.state_checked}, {}};
        colors = new int[]{R.color.colorTextColor_1, R.color.colorTextColor_1};
        mLLayoutLayer.setVisibility(View.INVISIBLE);
        mApplication = (DApplication) getApplication();
        requestPermisson();
        mIDSuCoList = new ArrayList<>();

    }

    private void init() {
        startGPS();
        startSignIn();
    }

    private void startGPS() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationHelper = new LocationHelper(this, (longtitude, latitude) -> {

        });
        mLocationHelper.checkpermission();
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
                mApplication.setmLocation(mLocation);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
                mLocationHelper.execute();

                mLocationHelper = new LocationHelper(MainActivity.this, (longtitude, latitude) -> {

                });
                mLocationHelper.checkpermission();
            }
        };
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        assert locationManager != null;
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }

    private void startSignIn() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_LOGIN);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void prepare() {
        mTxtInfo.setText(Html.fromHtml(getString(R.string.info_appbar_load_map_not_complete), Html.FROM_HTML_MODE_LEGACY));
        setLicense();
        mArcGISMapImageLayerAdministrator = mArcGISMapImageLayerThematic = null;
        mLayoutLegend.removeAllViews();
        LoadLegendAsycn loadLegendAsycn = new LoadLegendAsycn(this, mLayoutLegend,
                MainActivity.this, output -> {

        });
        loadLegendAsycn.execute();
        mListLayerID.clear();
        mGeocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        ArcGISMap mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 10.8035455, 106.6182534, 13);
        setSupportActionBar(mToolbar);
        //for camera begin
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //for camera end
        //đưa listview search ra phía sau
        mListViewSearch.invalidate();
        List<TraCuuAdapter.Item> items = new ArrayList<>();
        mSearchAdapter = new TraCuuAdapter(MainActivity.this, items);
        mListViewSearch.setAdapter(mSearchAdapter);
        mListViewSearch.setOnItemClickListener(MainActivity.this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,
                mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(MainActivity.this);
//        Menu menu = mNavView.getMenu();
//        MenuItem menuItem = menu.findItem(R.id.nav_version);
//        try {
//            menuItem.setTitle("Phiên bản: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        mMapView.setMap(mMap);

        mMapView.getMap().addDoneLoadingListener(this::handleArcgisMapDoneLoading);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                try {
                    if (mMapViewHandler != null)
                        mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mGraphicsOverlay.getGraphics().clear();
                if (mIsAddFeature && mMapViewHandler != null) {
                    //center is x, y
                    Point center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();

                    //project is long, lat
                    Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());

                    //geometry is x,y
                    Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
                    SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20);
                    Graphic graphic = new Graphic(center, symbol);

                    mGraphicsOverlay.getGraphics().add(graphic);

                    mPopUp.getCallout().setLocation(center);
                    mPointFindLocation = center;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
        mLocationDisplay.addLocationChangedListener(locationChangedEvent -> {
//                Point position = locationChangedEvent.getLocation().getPosition();
//                setViewPointCenter(position);
        });
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);
        mGraphicsOverlay.setRenderer(getRendererSuCo());

        mSeekBarAdministrator.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mArcGISMapImageLayerAdministrator.setOpacity((float) i / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarThematic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mArcGISMapImageLayerThematic.setOpacity((float) i / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mLLayoutOSM.setOnClickListener(this);
        mLLayoutSM.setOnClickListener(this);
        mLLayoutImage.setOnClickListener(this);


        mFloatButtonLayer.setOnClickListener(this);
        mBtnLayerClose.setOnClickListener(this);
        mFloatButtonLocation.setOnClickListener(this);
        mLayoutTimSuCo.setOnClickListener(this);
        mLayoutTimDiaChi.setOnClickListener(this);
        if (mApplication.getUserDangNhap() != null && mApplication.getUserDangNhap().getUserName() != null
                && mApplication.getUserDangNhap().getDisplayName() != null) {
            if (mTxtHeaderTenNV != null)
                mTxtHeaderTenNV.setText(mApplication.getUserDangNhap().getUserName());
            if (mTxtHeaderDisplayName != null)
                mTxtHeaderDisplayName.setText(mApplication.getUserDangNhap().getDisplayName());
        }
        optionSearchFeature();

    }

    private void setServices() {
        try {
            handleFeatureLoading();
            DLayerInfo dLayerInfoSuCo = setService_Table();
            new QueryServiceFeatureTableGetListAsync(MainActivity.this, output -> {
                setService_ArcGISImageLayer();
                mIDSuCoList = getListIDSuCoFromSuCoThongTins(output);
                setService_GraphicsOverLay(dLayerInfoSuCo, mIDSuCoList);
            }).execute();
        } catch (Exception e) {
            Log.e("Lỗi set service", e.toString());
        }

    }

    private void setService_ArcGISImageLayer() {
        for (DLayerInfo dLayerInfo : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (!dLayerInfo.isView() ||
                    //Bỏ áp lực, vì áp lực publish lên folder riêng, không thuộc TanHoaGis
                    dLayerInfo.getUrl().contains("ApLuc"))
                continue;
            String url = getUrlFromDLayerInfo(dLayerInfo.getUrl());
            if (dLayerInfo.getId().equals(getString(R.string.IDLayer_Basemap))
                    && mArcGISMapImageLayerAdministrator == null) {
                mArcGISMapImageLayerAdministrator = new ArcGISMapImageLayer(url);
                mArcGISMapImageLayerAdministrator.setId(dLayerInfo.getId());
                mMapView.getMap().getOperationalLayers().add(mArcGISMapImageLayerAdministrator);
                mArcGISMapImageLayerAdministrator.addDoneLoadingListener(() -> {
                    if (mArcGISMapImageLayerAdministrator.getLoadStatus() == LoadStatus.LOADED) {
                        MyServiceFeatureTable.getInstance(mArcGISMapImageLayerAdministrator);
                        ListenableList<ArcGISSublayer> sublayerList = mArcGISMapImageLayerAdministrator.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            addCheckBox((ArcGISMapImageSublayer) sublayer, states, colors, true);
                        }
                        mLoadedOnMap++;
                        if (mLoadedOnMap == 3)
                            handleFeatureDoneLoading();
                    }
                });
                mArcGISMapImageLayerAdministrator.loadAsync();

            } else if (mArcGISMapImageLayerThematic == null) {
                mArcGISMapImageLayerThematic = new ArcGISMapImageLayer(url.replaceFirst("FeatureServer(.*)", "MapServer"));
                mArcGISMapImageLayerThematic.setName(dLayerInfo.getTitleLayer());
                mArcGISMapImageLayerThematic.setId(dLayerInfo.getId());
//                    mArcGISMapImageLayerThematic.setMaxScale(0);
//                    mArcGISMapImageLayerThematic.setMinScale(10000000);
                mMapView.getMap().getOperationalLayers().add(mArcGISMapImageLayerThematic);
                mArcGISMapImageLayerThematic.addDoneLoadingListener(() -> {
                    if (mArcGISMapImageLayerThematic.getLoadStatus() == LoadStatus.LOADED) {
                        ListenableList<ArcGISSublayer> sublayerList = mArcGISMapImageLayerThematic.getSublayers();
                        for (ArcGISSublayer sublayer : sublayerList) {
                            addCheckBox((ArcGISMapImageSublayer) sublayer, states, colors, false);
                        }
                        mLoadedOnMap++;
                        if (mLoadedOnMap == 3)
                            handleFeatureDoneLoading();
                    }
                });
                mArcGISMapImageLayerThematic.loadAsync();
            }
        }
    }

    private DLayerInfo setService_Table() {
        DLayerInfo dLayerInfoSuCo = new DLayerInfo();
        for (DLayerInfo dLayerInfo : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
            if (!dLayerInfo.isView() ||
                    //Bỏ áp lực, vì áp lực publish lên folder riêng, không thuộc TanHoaGis
                    dLayerInfo.getUrl().contains("ApLuc"))
                continue;
            String url = getUrlFromDLayerInfo(dLayerInfo.getUrl());
            if (dLayerInfo.getId().equals(getString(R.string.IDLayer_DiemSuCo))) {
                dLayerInfoSuCo = dLayerInfo;

            } else if (dLayerInfo.getId().equals(Constant.IDLayer.ID_SU_CO_THONG_TIN_TABLE)) {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setId(dLayerInfo.getId());
                featureLayer.setName(dLayerInfo.getTitleLayer());
                mApplication.getDFeatureLayer.setLayerInfoDTG(dLayerInfo);
                mApplication.getDFeatureLayer.setServiceFeatureTableSuCoThongTin((ServiceFeatureTable) featureLayer.getFeatureTable());
            } else if (dLayerInfo.getId().equals(Constant.IDLayer.ID_HO_SO_VAT_TU_SU_CO_TABLE)) {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setId(dLayerInfo.getId());
                featureLayer.setName(dLayerInfo.getTitleLayer());
                mApplication.getDFeatureLayer.setServiceFeatureTableHoSoVatTuSuCo((ServiceFeatureTable) featureLayer.getFeatureTable());

            } else if (dLayerInfo.getId().equals(Constant.IDLayer.ID_SU_CO_THIET_BI_TABLE)) {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setId(dLayerInfo.getId());
                featureLayer.setName(dLayerInfo.getTitleLayer());
                mApplication.getDFeatureLayer.setServiceFeatureTableSuCoThietBi((ServiceFeatureTable) featureLayer.getFeatureTable());
            } else if (dLayerInfo.getId().equals(Constant.IDLayer.ID_HO_SO_THIET_BI_SU_CO_TABLE)) {
                ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
                FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
                featureLayer.setId(dLayerInfo.getId());
                featureLayer.setName(dLayerInfo.getTitleLayer());
                mApplication.getDFeatureLayer.setServiceFeatureTableHoSoThietBiSuCo((ServiceFeatureTable) featureLayer.getFeatureTable());
            }
        }
        return dLayerInfoSuCo;
    }

    private List<String> getListIDSuCoFromSuCoThongTins(List<Feature> features) {
        List<String> output = new ArrayList<>();
        for (Feature feature : features) {
            output.add(feature.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString());
        }
        return output;
    }

    @SuppressLint("DefaultLocale")
    private void setService_GraphicsOverLay(DLayerInfo dLayerInfo, List<String> idSuCoList) {
        String url = getUrlFromDLayerInfo(dLayerInfo.getUrl());
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);

        featureLayer.setName(dLayerInfo.getTitleLayer());
        featureLayer.setId(dLayerInfo.getId());
        featureLayer.setId(dLayerInfo.getId());
        featureLayer.setPopupEnabled(true);
        featureLayer.setVisible(true);
        featureLayer.setRenderer(getRendererSuCo());


//        builder.append("'')");
        featureLayer.setDefinitionExpression(getDefinitionWithoutComplete(idSuCoList));
        mMapView.getMap().getOperationalLayers().add(featureLayer);
        featureLayer.addDoneLoadingListener(() -> {
            if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                new QueryFeatureGetListGeometryAsync(MainActivity.this,
                        serviceFeatureTable, (List<Geometry> output) -> {
//                    for (Geometry geometry : output) {
//                        Graphic graphic = new Graphic(geometry.getExtent().getCenter());
//                        mGraphicsOverlay.getGraphics().add(graphic);
//                    }
                    for (DLayerInfo item : ListObjectDB.getInstance().getLstFeatureLayerDTG()) {
                        if (item.getId().equals(Constant.IDLayer.ID_SU_CO_THONG_TIN_TABLE)) {
                            Callout callout = mMapView.getCallout();
                            mApplication.getDFeatureLayer.setLayer(featureLayer);
                            mPopUp = new Popup(callout, MainActivity.this, mMapView,
                                    mLocationDisplay, mGeocoder, mArcGISMapImageLayerAdministrator);
//                    if (KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(getString(R.string.group_role_giamsat)))
//                        featureLayer.setVisible(false);
//                    Callout callout = mMapView.getCallout();
                            mMapViewHandler = new MapViewHandler(callout, mMapView, mPopUp, MainActivity.this);
                            mLoadedOnMap++;
                            if (mLoadedOnMap == 3)
                                handleFeatureDoneLoading();
                        }
                    }
                }).execute(idSuCoList);
            }
        });
    }

    private String getDefinitionWithoutComplete(List<String> idSuCoList) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s in (", Constant.FIELD_SUCO.ID_SUCO));
        for (int i = 0; i < idSuCoList.size(); i++) {
            String idSuCo = idSuCoList.get(i);
            if (i != idSuCoList.size() - 1)
                builder.append(String.format("'%s' ,", idSuCo));
            else if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.Role.GROUPROLE_TC)) {
                builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FIELD_SUCO.TRANG_THAI_THI_CONG, Constant.TRANG_THAI_SU_CO.HOAN_THANH));
            } else if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.Role.GROUPROLE_GS)) {
                builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FIELD_SUCO.TRANG_THAI_GIAM_SAT, Constant.TRANG_THAI_SU_CO.HOAN_THANH));
            } else {
                builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FIELD_SUCO.TRANG_THAI, Constant.TRANG_THAI_SU_CO.HOAN_THANH));
            }
        }
        return builder.toString();
    }

    private String getDefinitionWithComplete(List<String> idSuCoList) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s in (", Constant.FIELD_SUCO.ID_SUCO));
        for (int i = 0; i < idSuCoList.size(); i++) {
            String idSuCo = idSuCoList.get(i);
            if (i != idSuCoList.size() - 1)
                builder.append(String.format("'%s' ,", idSuCo));
            else
                builder.append(String.format("'%s' ) ", idSuCo));

        }
        return builder.toString();
    }

    private String getUrlFromDLayerInfo(String input) {
        String output = input;
        if (!input.startsWith("http"))
            output = "http:" + input;
        return output;
    }

    private void addCheckBox(final ArcGISMapImageSublayer layer, int[][] states, int[] colors,
                             boolean isAdministrator) {
        @SuppressLint("InflateParams") LinearLayout layoutFeature = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.layout_feature, null);
        final CheckBox checkBox = layoutFeature.findViewById(R.id.ckb_layout_feature);
        final TextView textView = layoutFeature.findViewById(R.id.txt_layout_feature);
        textView.setTextColor(MainActivity.this.getColor(android.R.color.black));
        textView.setText(layer.getName());
        checkBox.setChecked(false);
        layer.setVisible(false);
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (buttonView.isChecked()) {
                if (textView.getText().equals(layer.getName()))
                    layer.setVisible(true);


            } else {
                if (textView.getText().equals(layer.getName()))
                    layer.setVisible(false);
            }
        });
        if (!mListLayerID.contains(layer.getName())) {
            if (isAdministrator) mLayoutDisplayLayerAdministration.addView(layoutFeature);
            else mLayoutDisplayLayerThematic.addView(layoutFeature);
            mListLayerID.add(layer.getName());
        }
    }

    private void handleFeatureLoading() {
        mLoadedOnMap = 0;
        mLLayoutInfo.setVisibility(View.VISIBLE);
        mTxtInfo.setText(Html.fromHtml(getString(R.string.info_appbar_load_map_not_complete), Html.FROM_HTML_MODE_LEGACY));
        mFloatButtonLocation.setVisibility(View.GONE);
        mFloatButtonLayer.setVisibility(View.GONE);
    }

    private void handleFeatureDoneLoading() {
        mLLayoutInfo.setVisibility(View.INVISIBLE);
//        mTxtInfo.setText(Html.fromHtml(getString(R.string.info_appbar_load_map_complete), Html.FROM_HTML_MODE_LEGACY));
        mFloatButtonLocation.setVisibility(View.VISIBLE);
        mFloatButtonLayer.setVisibility(View.VISIBLE);
    }

    private void handleArcgisMapDoneLoading() {

        mLocationDisplay = mMapView.getLocationDisplay();
        mLocationDisplay.startAsync();
        mLayoutDisplayLayerThematic.removeAllViews();
        mLayoutDisplayLayerAdministration.removeAllViews();

        setServices();
    }

    private void setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license));
        //way 2
//        UserCredential credential = new UserCredential("thanle95", "Gemini111");
//
//// replace the URL with either the ArcGIS Online URL or your portal URL
//        final Portal portal = new Portal("https://than-le.maps.arcgis.com");
//        portal.setCredential(credential);
//
//// load portal and listen to done loading event
//        portal.loadAsync();
//        portal.addDoneLoadingListener(new Runnable() {
//            @Override
//            public void run() {
//                LicenseInfo licenseInfo = portal.getPortalInfo().getLicenseInfo();
//                // Apply the license at Standard level
//                ArcGISRuntimeEnvironment.setLicense(licenseInfo);
//            }
//        });
    }


    private UniqueValueRenderer getRendererSuCo() {
        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
        if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.Role.GROUPROLE_TC)) {
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.TRANG_THAI_THI_CONG);
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_THI_CONG);
        } else if (mApplication.getUserDangNhap().getGroupRole().equals(Constant.Role.GROUPROLE_GS)) {
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.TRANG_THAI_GIAM_SAT);
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN_GIAM_SAT);
        } else {
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.TRANG_THAI);
            uniqueValueRenderer.getFieldNames().add(Constant.FIELD_SUCO.HINH_THUC_PHAT_HIEN);
        }
        PictureMarkerSymbol chuaXuLySymbol = new PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA);
        chuaXuLySymbol.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        chuaXuLySymbol.setWidth(getResources().getInteger(R.integer.size_feature_renderer));

        PictureMarkerSymbol dangXuLySymbol = new PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_DANG_SUA_CHUA);
        dangXuLySymbol.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        dangXuLySymbol.setWidth(getResources().getInteger(R.integer.size_feature_renderer));


        PictureMarkerSymbol hoanThanhSymbol = new PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_HOAN_THANH);
        hoanThanhSymbol.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        hoanThanhSymbol.setWidth(getResources().getInteger(R.integer.size_feature_renderer));


        PictureMarkerSymbol beNgamSymbol = new PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM);
        beNgamSymbol.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        beNgamSymbol.setWidth(getResources().getInteger(R.integer.size_feature_renderer));

        uniqueValueRenderer.setDefaultSymbol(chuaXuLySymbol);
        uniqueValueRenderer.setDefaultLabel("Chưa xác định");

//        List<Object> chuaXuLyValue = new ArrayList<>();
//        chuaXuLyValue.add(Constant.TRANG_THAI_SU_CO.CHUA_XU_LY);
        List<Object> beNgamChuaXuLyValue = new ArrayList<>();
        beNgamChuaXuLyValue.add(Constant.TRANG_THAI_SU_CO.CHUA_XU_LY);
        beNgamChuaXuLyValue.add(1);
        //đang xử lý: begin
        List<Object> dangXuLyValue = new ArrayList<>();
        dangXuLyValue.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        List<Object> dangXuLyValue1 = new ArrayList<>();
        dangXuLyValue1.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue1.add(1);
        List<Object> dangXuLyValue2 = new ArrayList<>();
        dangXuLyValue2.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue2.add(2);

        List<Object> dangXuLyValue3 = new ArrayList<>();
        dangXuLyValue3.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue3.add(3);

        List<Object> dangXuLyValue4 = new ArrayList<>();
        dangXuLyValue4.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue4.add(4);

        List<Object> dangXuLyValue5 = new ArrayList<>();
        dangXuLyValue5.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue5.add(5);

        List<Object> dangXuLyValue6 = new ArrayList<>();
        dangXuLyValue6.add(Constant.TRANG_THAI_SU_CO.DANG_XU_LY);
        dangXuLyValue6.add(6);
        //đang xỷ lý: end


        //hoàn thành: begin
        List<Object> hoanThanhValue = new ArrayList<>();
        hoanThanhValue.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        List<Object> hoanThanhValue1 = new ArrayList<>();
        hoanThanhValue1.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue1.add(1);
        List<Object> hoanThanhValue2 = new ArrayList<>();
        hoanThanhValue2.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue2.add(2);

        List<Object> hoanThanhValue3 = new ArrayList<>();
        hoanThanhValue3.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue3.add(3);

        List<Object> hoanThanhValue4 = new ArrayList<>();
        hoanThanhValue4.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue4.add(4);

        List<Object> hoanThanhValue5 = new ArrayList<>();
        hoanThanhValue5.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue5.add(5);

        List<Object> hoanThanhValue6 = new ArrayList<>();
        hoanThanhValue6.add(Constant.TRANG_THAI_SU_CO.HOAN_THANH);
        hoanThanhValue6.add(6);

//
//        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
//                "Chưa xử lý", "Chưa xử lý", chuaXuLySymbol, chuaXuLyValue));

        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Chưa xử lý bể ngầm", "Chưa xử lý bể ngầm", beNgamSymbol, beNgamChuaXuLyValue));

        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue1));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue2));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue3));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue4));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue5));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue6));


        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue1));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue2));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue3));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue4));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue5));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue6));
//
        return uniqueValueRenderer;
    }

    public void requestPermisson() {
        boolean permissionCheck1 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck3 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck4 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[3]) == PackageManager.PERMISSION_GRANTED;

        if (!(permissionCheck1 && permissionCheck2 && permissionCheck3 && permissionCheck4)) {
            // If permissions are not already granted, request permission from the user.
            ActivityCompat.requestPermissions(this, Constant.RequestPermission.REQUEST_PERMISSIONS, Constant.RequestCode.REQUEST_CODE_PERMISSION);
        }  // Report other unknown failure types to the user - for example, location services may not // be enabled on the device. //                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent //                            .getSource().getLocationDataSource().getError().getMessage()); //                    Toast.makeText(QuanLySuCo.this, message, Toast.LENGTH_LONG).show();
        else {
            init();
        }
    }

    private void setViewPointCenter(final Point position) {
        if (mPopUp == null) {
            MySnackBar.make(mMapView, getString(R.string.load_map_not_complete), true);
        } else {
            final Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
            final ListenableFuture<Boolean> booleanListenableFuture = mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            booleanListenableFuture.addDoneListener(() -> {
                try {
                    if (booleanListenableFuture.get()) {
                        MainActivity.this.mPointFindLocation = position;
                    }
                    mPopUp.showPopupFindLocation(position);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            });
        }

    }

    private void setViewPointCenterLongLat(Point position, String location) {
        if (mPopUp == null) {
            MySnackBar.make(mMapView, getString(R.string.load_map_not_complete), true);
        } else {
            Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWgs84());
            Geometry geometry1 = GeometryEngine.project(geometry, SpatialReferences.getWebMercator());
            Point point = geometry1.getExtent().getCenter();
            mIsAddFeature = true;
            SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20);
            Graphic graphic = new Graphic(point, symbol);
            mGraphicsOverlay.getGraphics().add(graphic);

            mMapView.setViewpointCenterAsync(point);
            mPopUp.showPopupFindLocation(point, location);
            this.mPointFindLocation = point;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean isGranted = true;
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        if (isGranted) {
            init();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(MainActivity.this.getApplicationContext().getString(R.string.message_permission));
            builder.setPositiveButton(MainActivity.this.getApplicationContext().getString(R.string.message_btn_ok),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                        Intent intent = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        assert intent != null;
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void optionSearchFeature() {
        this.mIsSearchingFeature = true;
        mLayoutTimSuCo.setBackgroundResource(R.drawable.layout_border_bottom);
        mLayoutTimDiaChi.setBackgroundResource(R.drawable.layout_shape_basemap_none);
    }

    private void optionFindRoute() {
        this.mIsSearchingFeature = false;
        mLayoutTimDiaChi.setBackgroundResource(R.drawable.layout_border_bottom);
        mLayoutTimSuCo.setBackgroundResource(R.drawable.layout_shape_basemap_none);
    }

    private void deleteSearching() {
        mGraphicsOverlay.getGraphics().clear();
        mSearchAdapter.clear();
        mSearchAdapter.notifyDataSetChanged();
    }


    private void themDiemSuCoNoCapture() {
        try {
            if (mMapViewHandler == null)
                Toast.makeText(this, getString(R.string.load_map_not_complete), Toast.LENGTH_LONG).show();
            else {
                FindLocationAsycn findLocationAsycn = new FindLocationAsycn(this, false,
                        mGeocoder, output -> {
                    if (output != null) {

                        String subAdminArea = output.get(0).getSubAdminArea();
                        //nếu tài khoản có quyền truy cập vào
                        if (subAdminArea.equals(getString(R.string.QuanPhuNhuanName)) ||
                                subAdminArea.equals(getString(R.string.QuanTanBinhName)) ||
                                subAdminArea.equals(getString(R.string.QuanTanPhuName))) {
                            mApplication.getDiemSuCo.setPoint(mPointFindLocation);
                            mApplication.getDiemSuCo.setVitri(output.get(0).getLocation());
                            mApplication.getDiemSuCo.setQuan(subAdminArea);
                            mApplication.getDiemSuCo.setPhuong(output.get(0).getLocality());
                            Intent intent = new Intent(MainActivity.this, ThemSuCoActivity.class);
                            startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_ADD_FEATURE);
                            mTxtSearchView.setQuery("", true);

                        } else {
                            Toast.makeText(MainActivity.this, R.string.message_not_area_management, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, R.string.message_not_area_management, Toast.LENGTH_LONG).show();
                    }

                });
                Geometry project = GeometryEngine.project(mPointFindLocation, SpatialReferences.getWgs84());
                double[] location = {project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
                findLocationAsycn.setmLongtitude(location[0]);
                findLocationAsycn.setmLatitude(location[1]);
                findLocationAsycn.execute();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    private void visibleFloatActionButton() {
        if (mFloatButtonLayer.getVisibility() == View.VISIBLE) {
            mFloatButtonLayer.setVisibility(View.INVISIBLE);
            mFloatButtonLocation.setVisibility(View.INVISIBLE);
        } else {
            mFloatButtonLayer.setVisibility(View.VISIBLE);
            mFloatButtonLocation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        mApplication.getDiemSuCo.setPoint(null);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }  //            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quan_ly_su_co, menu);
        mTxtSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mTxtSearchView.setQueryHint(getString(R.string.title_search));
        mTxtSearchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mTxtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    if (mIsSearchingFeature && mMapViewHandler != null)
                        mMapViewHandler.querySearch(query, mSearchAdapter);
                    else if (query.length() > 0) {
                        deleteSearching();
                        FindLocationAsycn findLocationAsycn = new FindLocationAsycn(MainActivity.this,
                                true, mGeocoder, output -> {
                            if (output != null) {
                                mSearchAdapter.clear();
                                mSearchAdapter.notifyDataSetChanged();
                                if (output.size() > 0) {
                                    for (DAddress address : output) {
                                        TraCuuAdapter.Item item = new TraCuuAdapter.Item(-1, "", 0, "", address.getLocation());
                                        item.setLatitude(address.getLatitude());
                                        item.setLongtitude(address.getLongtitude());
                                        mSearchAdapter.add(item);
                                    }
                                    mSearchAdapter.notifyDataSetChanged();

                                    //                                    }
                                }
                            }

                        });
                        findLocationAsycn.execute(query);

                    }
                } catch (Exception e) {
                    Log.e("Lỗi tìm kiếm", e.toString());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.trim().length() > 0 && !mIsSearchingFeature) {
                    mIsAddFeature = true;
                } else {
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                    mGraphicsOverlay.getGraphics().clear();
                }
                return false;
            }
        });
        menu.findItem(R.id.action_search).

                setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        visibleFloatActionButton();
                        mLayoutTimKiem.setVisibility(View.VISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mLayoutTimKiem.setVisibility(View.INVISIBLE);
                        visibleFloatActionButton();
                        return true;
                    }
                });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void showHideComplete() {
        for (Layer layer : mMapView.getMap().getOperationalLayers()) {
            if (layer instanceof FeatureLayer && layer.getName().
                    equals(mApplication.getDFeatureLayer.getLayer().getName()) && mIDSuCoList != null) {
                if (mIsShowComplete) {
                    ((FeatureLayer) layer).setDefinitionExpression(getDefinitionWithoutComplete(mIDSuCoList));
                    mIsShowComplete = false;
                } else {
                    ((FeatureLayer) layer).setDefinitionExpression(getDefinitionWithComplete(mIDSuCoList));
                    mIsShowComplete = true;
                }
                layer.loadAsync();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
//            case R.id.nav_thongke:
//                Intent intent = new Intent(this, ThongKeActivity.class);
//                this.startActivity(intent);
//                break;
//            case R.id.nav_tracuu:
//                intent = new Intent(this, TraCuuActivity.class);
//                this.startActivityForResult(intent, 1);
//                break;
//            case R.id.nav_find_route:
//                intent = new Intent(this, FindRouteActivity.class);
//                this.startActivity(intent);
//                break;
//            case R.id.nav_setting:
//                intent = new Intent(this, SettingsActivity.class);
//                this.startActivityForResult(intent, 1);
//                break;
            case R.id.nav_change_password:
                Intent intentChangePassword = new Intent(this, DoiMatKhauActivity.class);
                startActivity(intentChangePassword);
                break;
            case R.id.nav_reload:
                if (CheckConnectInternet.isOnline(this)) {
                    mIsShowComplete = false;
                    if (mPopUp != null && mPopUp.getCallout() != null && mPopUp.getCallout().isShowing())
                        mPopUp.getCallout().dismiss();
                    prepare();
                }
                break;
            case R.id.nav_show_hide_complete:
                showHideComplete();
                break;
            case R.id.nav_logOut:
                mIsShowComplete = false;
                if (mPopUp != null && mPopUp.getCallout() != null && mPopUp.getCallout().isShowing())
                    mPopUp.getCallout().dismiss();
                startSignIn();
                break;
            case R.id.nav_list_task:
                Intent intentListTask = new Intent(MainActivity.this, ListTaskActivity.class);
                startActivityForResult(intentListTask, Constant.RequestCode.REQUEST_CODE_LIST_TASK);
                break;
            case R.id.nav_delete_searching:
                deleteSearching();
                break;
            case R.id.nav_visible_float_button:
                visibleFloatActionButton();
                break;
            default:
                break;
        }


        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickTextView(View v) {
        switch (v.getId()) {
            case R.id.txt_quanlysuco_legend:
                if (mLayoutLegend.getVisibility() == View.VISIBLE) {
                    mLayoutLegend.setVisibility(View.GONE);
                } else {
                    mLayoutLegend.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_quanlysuco_hanhchinh:

                if (mLayoutDisplayLayerAdministration.getVisibility() == View.VISIBLE) {
                    mSeekBarAdministrator.setVisibility(View.GONE);
                    mLayoutDisplayLayerAdministration.setVisibility(View.GONE);
                } else {
                    mSeekBarAdministrator.setVisibility(View.VISIBLE);
                    mLayoutDisplayLayerAdministration.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.txt_quanlysuco_dulieu:
                if (mLayoutDisplayLayerThematic.getVisibility() == View.VISIBLE) {
                    mLayoutDisplayLayerThematic.setVisibility(View.GONE);
                    mSeekBarThematic.setVisibility(View.GONE);
                } else {
                    mLayoutDisplayLayerThematic.setVisibility(View.VISIBLE);
                    mSeekBarThematic.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void handlingLocation() {
        if (mIsFirstLocating) {
            mIsFirstLocating = false;
            mLocationDisplay.stop();
            mLocationDisplay.startAsync();
            setViewPointCenter(mLocationDisplay.getMapLocation());
            mIsAddFeature = true;
        } else {
            if (mLocationDisplay.isStarted()) {
                mLocationDisplay.stop();
            }
            if (mPopUp.getCallout() != null && mPopUp.getCallout().isShowing())
                mPopUp.getCallout().dismiss();
            if (!mLocationDisplay.isStarted()) {
                mLocationDisplay.startAsync();
                setViewPointCenter(mLocationDisplay.getMapLocation());
                mIsAddFeature = true;
            }
        }
    }


    public void onClickCheckBox(View v) {
        if (v instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) v;
            switch (v.getId()) {
                case R.id.ckb_quanlysuco_hanhchinh:

                    for (int i = 0; i < mLayoutDisplayLayerAdministration.getChildCount(); i++) {
                        View view = mLayoutDisplayLayerAdministration.getChildAt(i);
                        if (view instanceof LinearLayout) {
                            LinearLayout layoutFeature = (LinearLayout) view;
                            for (int j = 0; j < layoutFeature.getChildCount(); j++) {
                                View view1 = layoutFeature.getChildAt(j);
                                if (view1 instanceof LinearLayout) {
                                    LinearLayout layoutCheckBox = (LinearLayout) view1;
                                    for (int k = 0; k < layoutCheckBox.getChildCount(); k++) {
                                        View view2 = layoutCheckBox.getChildAt(k);
                                        if (view2 instanceof CheckBox) {
                                            CheckBox checkBoxK = (CheckBox) view2;
                                            if (checkBox.isChecked())
                                                checkBoxK.setChecked(true);
                                            else checkBoxK.setChecked(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case R.id.ckb_quanlysuco_dulieu:
                    for (int i = 0; i < mLayoutDisplayLayerThematic.getChildCount(); i++) {
                        View view = mLayoutDisplayLayerThematic.getChildAt(i);
                        if (view instanceof LinearLayout) {
                            LinearLayout layoutFeature = (LinearLayout) view;
                            for (int j = 0; j < layoutFeature.getChildCount(); j++) {
                                View view1 = layoutFeature.getChildAt(j);
                                if (view1 instanceof LinearLayout) {
                                    LinearLayout layoutCheckBox = (LinearLayout) view1;
                                    for (int k = 0; k < layoutCheckBox.getChildCount(); k++) {
                                        View view2 = layoutCheckBox.getChildAt(k);
                                        if (view2 instanceof CheckBox) {
                                            CheckBox checkBoxK = (CheckBox) view2;
                                            if (checkBox.isChecked())
                                                checkBoxK.setChecked(true);
                                            else checkBoxK.setChecked(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_tim_su_co:
                optionSearchFeature();
                break;
            case R.id.layout_tim_dia_chi:
                optionFindRoute();
                break;
            case R.id.floatBtnLayer:
                v.setVisibility(View.INVISIBLE);
                mLLayoutLayer.setVisibility(View.VISIBLE);
//                mCurrentPoint = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
                break;
            case R.id.llayout_layer_open_street_map:
                mMapView.getMap().setMaxScale(1128.497175);
                mMapView.getMap().setBasemap(Basemap.createOpenStreetMap());
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_open_street_map);
                mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);

                break;
            case R.id.llayout_layer_street_map:
                mMapView.getMap().setMaxScale(1128.497176);
                mMapView.getMap().setBasemap(Basemap.createStreets());
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_street_map);

                break;
            case R.id.llayout_layer_image:
                mMapView.getMap().setMaxScale(getResources().getInteger(R.integer.MAX_SCALE_IMAGE_WITH_LABLES));
                mMapView.getMap().setBasemap(Basemap.createImageryWithLabels());
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_image);

                break;
            case R.id.btn_layer_close:
                mLLayoutLayer.setVisibility(View.INVISIBLE);
                mFloatButtonLayer.setVisibility(View.VISIBLE);
                break;
//            case R.id.img_chonvitri_themdiemsuco:
////                themDiemSuCo();
//                themDiemSuCoNoCapture();
//                break;
//            case R.id.btn_add_feature_close:
//                if (mMapViewHandler != null) {
//                    findViewById(R.id.llayout_info_app_bar).setVisibility(View.GONE);
//                    findViewById(R.id.img_map_pin).setVisibility(View.GONE);
//                    mMapViewHandler.setClickBtnAdd(false);
//                }
//                break;
            case R.id.floatBtnLocation:
                handlingLocation();

                break;
            case R.id.imgBtn_timkiemdiachi_themdiemsuco:
//                themDiemSuCo();
                themDiemSuCoNoCapture();
                break;

        }
    }

    @Nullable
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            assert in != null;
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            assert in != null;
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void handlingColorBackgroundLayerSelected(int id) {
        switch (id) {
            case R.id.llayout_layer_open_street_map:
                mImageOpenStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap);
                mTxtOSM.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                mImageStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtSM.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                mImageImageWithLabel.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtImageWithLabel.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.llayout_layer_street_map:
                mImageStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap);
                mTxtSM.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                mImageOpenStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtOSM.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                mImageImageWithLabel.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtImageWithLabel.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.llayout_layer_image:
                mImageImageWithLabel.setBackgroundResource(R.drawable.layout_shape_basemap);
                mTxtImageWithLabel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                mImageOpenStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtOSM.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                mImageStreetMap.setBackgroundResource(R.drawable.layout_shape_basemap_none);
                mTxtSM.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
        }
    }

    private void handlingListTaskActivityResult() {
        mIsAddFeature = false;
        if (mApplication.getDiemSuCo.getTrangThai() == Constant.TRANG_THAI_SU_CO.HOAN_THANH && !mIsShowComplete) {
        }
        //query sự cố theo idsuco, lấy objectid
        String selectedIDSuCo = mApplication.getDiemSuCo.getIdSuCo();
        mMapViewHandler.query(String.format("%s = '%s'", Constant.FIELD_SUCO.ID_SUCO, selectedIDSuCo));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case REQUEST_SEARCH:
                    final int objectid = data.getIntExtra(getString(R.string.ket_qua_objectid), 1);
                    if (resultCode == Activity.RESULT_OK) {
                        String selectedIDSuCo = mApplication.getDiemSuCo.getIdSuCo();
                        mMapViewHandler.query(String.format("%s = '%s'", Constant.FIELD_SUCO.ID_SUCO, selectedIDSuCo));
//                        mMapViewHandler.queryByObjectID(objectid);
                    }
                    break;
                case Constant.RequestCode.REQUEST_CODE_LOGIN:
                    if (Activity.RESULT_OK != resultCode) {
                        finish();
                        return;
                    } else {
                        mGeocoder = new Geocoder(this);
                        // create an empty map instance
                        final PreparingByAPIAsycn preparingAsycn = new PreparingByAPIAsycn(this, this::prepare);
                        if (CheckConnectInternet.isOnline(this))
                            preparingAsycn.execute();
                    }
                case Constant.RequestCode.REQUEST_CODE_ADD_FEATURE:
                    mIsAddFeature = false;
                    if (mApplication.getDiemSuCo.getPoint() != null) {
                        mMapViewHandler.addFeature(mApplication.getDiemSuCo.getPoint());
                        deleteSearching();
                        handlingLocation();
                    }
                    break;
                case Constant.RequestCode.REQUEST_CODE_LIST_TASK:
                    if (resultCode == Activity.RESULT_OK)
                        handlingListTaskActivityResult();
                    break;
            }
        } catch (Exception ignored) {
        }

        if (requestCode == getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_ADD_FEATURE)) {
            if (resultCode == RESULT_OK) {
//                this.mUri= data.getData();
                if (this.mUri != null) {
//                    Uri selectedImage = this.mUri;
//                    getContentResolver().notifyChange(selectedImage, null);
                    Bitmap bitmap = getBitmap(mUri.getPath());
                    try {
                        if (bitmap != null) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            byte[] image = outputStream.toByteArray();
                            Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
//                            mMapViewHandler.addFeature(image);

                            EditAsync editAsync = new EditAsync(this, mSelectedArcGISFeature,
                                    true, image, mPopUp.getListHoSoVatTuSuCo(), mPopUp.getmListHoSoVatTuThuHoiSuCo(),
                                    output -> {
                                        //
                                        if (output != null) {
                                            mPopUp.getCallout().dismiss();
                                            if (Short.parseShort(output.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString())
                                                    == Constant.TRANG_THAI_SU_CO.HOAN_THANH)
                                                new APICompleteAsync(mApplication, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString())
                                                        .execute();
                                            if (!output.canEditAttachments())
                                                MySnackBar.make(mPopUp.getmBtnLeft(), MainActivity.this.getString(R.string.message_cannot_edit_attachment), true);
                                        } else
                                            MySnackBar.make(mPopUp.getmBtnLeft(), MainActivity.this.getString(R.string.message_update_failed), true);
                                    });
                            editAsync.execute(mFeatureViewMoreInfoAdapter);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                MySnackBar.make(mMapView, "Hủy chụp ảnh", false);
            } else {
                MySnackBar.make(mMapView, "Lỗi khi chụp ảnh", false);
            }
        } else if (requestCode == getResources().getInteger(R.integer.REQUEST_ID_IMAGE_CAPTURE_POPUP)) {
            if (resultCode == RESULT_OK) {
//                this.mUri= data.getData();
                if (this.mUri != null) {
//                    Uri selectedImage = this.mUri;
//                    getContentResolver().notifyChange(selectedImage, null);
                    Bitmap bitmap = getBitmap(mUri.getPath());
                    try {
                        if (bitmap != null) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            byte[] image = outputStream.toByteArray();
                            Toast.makeText(this, "Đã lưu ảnh", Toast.LENGTH_SHORT).show();
//                            mMapViewHandler.addFeature(image);
//                            mPopUp.getDialog().dismiss();
                            EditAsync editAsync = new EditAsync(this, mSelectedArcGISFeature,
                                    true, image, mPopUp.getListHoSoVatTuSuCo(), mPopUp.getmListHoSoVatTuThuHoiSuCo(),
                                    output -> {
                                        if (output != null) {
                                            mPopUp.getCallout().dismiss();
                                            if (Short.parseShort(output.getAttributes().get(Constant.FIELD_SUCOTHONGTIN.TRANG_THAI).toString())
                                                    == Constant.TRANG_THAI_SU_CO.HOAN_THANH)
                                                new APICompleteAsync(mApplication, mApplication.getArcGISFeature().getAttributes().get(Constant.FIELD_SUCOTHONGTIN.ID_SUCO).toString())
                                                        .execute();
                                            if (!output.canEditAttachments())
                                                MySnackBar.make(mPopUp.getmBtnLeft(), MainActivity.this.getString(R.string.message_cannot_edit_attachment), true);
                                        } else {
                                            MySnackBar.make(mPopUp.getmBtnLeft(), MainActivity.this.getString(R.string.message_update_failed), true);
                                        }
                                    });
                            editAsync.execute(mFeatureViewMoreInfoAdapter);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                MySnackBar.make(mMapView, "Hủy chụp ảnh", false);
            } else {
                MySnackBar.make(mMapView, "Lỗi khi chụp ảnh", false);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TraCuuAdapter.Item item = ((TraCuuAdapter.Item) parent.getItemAtPosition(position));
        int objectID = item.getObjectID();
        if (objectID != -1 && mMapViewHandler != null) {
            mMapViewHandler.query(String.format("%s = '%s'", Constant.FIELD_SUCO.ID_SUCO, item.getId()));
//            mMapViewHandler.queryByObjectID(objectID);
            mSearchAdapter.clear();
            mSearchAdapter.notifyDataSetChanged();
        } else {

            setViewPointCenterLongLat(new Point(item.getLongtitude(), item.getLatitude()), item.getDiaChi());
            Log.d("Tọa độ tìm kiếm", String.format("[% ,.9f;% ,.9f]", item.getLongtitude(), item.getLatitude()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

