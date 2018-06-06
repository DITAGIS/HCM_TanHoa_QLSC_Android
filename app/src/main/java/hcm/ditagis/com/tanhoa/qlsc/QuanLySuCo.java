package hcm.ditagis.com.tanhoa.qlsc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.adapter.FeatureViewMoreInfoAdapter;
import hcm.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter;
import hcm.ditagis.com.tanhoa.qlsc.async.EditAsync;
import hcm.ditagis.com.tanhoa.qlsc.libs.FeatureLayerDTG;
import hcm.ditagis.com.tanhoa.qlsc.utities.Config;
import hcm.ditagis.com.tanhoa.qlsc.utities.Constant;
import hcm.ditagis.com.tanhoa.qlsc.utities.ImageFile;
import hcm.ditagis.com.tanhoa.qlsc.utities.ListConfig;
import hcm.ditagis.com.tanhoa.qlsc.utities.MapViewHandler;
import hcm.ditagis.com.tanhoa.qlsc.utities.MySnackBar;
import hcm.ditagis.com.tanhoa.qlsc.utities.Popup;

public class QuanLySuCo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Uri mUri;
    private Popup popupInfos;
    private MapView mMapView;
    private Callout mCallout;
    private FeatureLayerDTG mFeatureLayerDTG;
    private MapViewHandler mMapViewHandler;
    private ListView mListViewSearch;
    private TraCuuAdapter mSearchAdapter;
    private LocationDisplay mLocationDisplay;
    private int requestCode = 2;
    private Point mCurrentPoint;
    private Geocoder mGeocoder;
    private GraphicsOverlay mGraphicsOverlay;
    private boolean isSearchingFeature = false;
    private TextView txtTimSuCo;
    private TextView txtTimDiaChi;
    private LinearLayout mLayoutTimKiem;
    private FloatingActionButton mFloatButtonLayer;
    private LinearLayout mFloatButtonLocation,
            mFloatActionButtonAddPoint, mFloatButtonClosePopup;
    private FloatingActionButton mFloatButtonAdd;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private LinearLayout mLinearLayoutCover;
    private boolean isOpenFab = false;
    private Animation mAnimationFabOpen, mAnimationFabClose, mAnimationClockwise, mAnimationAntiClockwise;

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    public void setFeatureViewMoreInfoAdapter(FeatureViewMoreInfoAdapter featureViewMoreInfoAdapter) {
        this.mFeatureViewMoreInfoAdapter = featureViewMoreInfoAdapter;
    }

    private FeatureViewMoreInfoAdapter mFeatureViewMoreInfoAdapter;

    public void setSelectedArcGISFeature(ArcGISFeature selectedArcGISFeature) {
        this.mSelectedArcGISFeature = selectedArcGISFeature;
    }

    private ArcGISFeature mSelectedArcGISFeature;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 55;
    private static final int REQUEST_ID_IMAGE_CAPTURE_POPUP = 44;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_su_co);
        // create an empty map instance
        setLicense();
        final ArcGISMap mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 10.7554041, 106.6546293, 12);
        mGeocoder = new Geocoder(this);
        mAnimationFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        mAnimationFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        mAnimationClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        mAnimationAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlock);
        //for camera
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.mListViewSearch = findViewById(R.id.lstview_search);
        //đưa listview search ra phía sau
        this.mListViewSearch.invalidate();
        List<TraCuuAdapter.Item> items = new ArrayList<>();
        this.mSearchAdapter = new TraCuuAdapter(QuanLySuCo.this, items);
        this.mListViewSearch.setAdapter(mSearchAdapter);
        this.mListViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TraCuuAdapter.Item item = ((TraCuuAdapter.Item) parent.getItemAtPosition(position));
                int objectID = item.getObjectID();
                if (objectID != -1) {
                    mMapViewHandler.queryByObjectID(objectID);
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                }
                //tìm kiếm địa chỉ
                else {
                    setViewPointCenterLongLat(new Point(item.getLongtitude(), item.getLatitude()));
                }
            }
        });
        requestPermisson();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mMapView = findViewById(R.id.mapView);


        // config feature layer service
        List<Config> configs = ListConfig.getInstance(this).getConfigs();
        mFeatureLayerDTGS = new ArrayList<>();
        for (Config config : configs) {
            ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(config.getUrl());

            FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
            if (config.getAlias().equals(getString(R.string.ALIAS_HANH_CHINH)))
                featureLayer.setOpacity(0.7f);
            featureLayer.setName(config.getAlias());
            featureLayer.setMaxScale(0);

            featureLayer.setMinScale(1000000);
            FeatureLayerDTG featureLayerDTG = new FeatureLayerDTG(featureLayer);
            featureLayerDTG.setOutFields(config.getOutField());
            featureLayerDTG.setQueryFields(config.getQueryField());
            featureLayerDTG.setTitleLayer(featureLayer.getName());
            featureLayerDTG.setUpdateFields(config.getUpdateField());
            mFeatureLayerDTG = featureLayerDTG;
            if (config.getName() != null && config.getName().equals(Constant.NAME_DIEMSUCO)) {
                featureLayer.setId(config.getName());
                popupInfos = new Popup(QuanLySuCo.this, serviceFeatureTable, mCallout);
                featureLayer.setPopupEnabled(true);
                setRendererSuCoFeatureLayer(featureLayer);
                mCallout = mMapView.getCallout();

                mMapViewHandler = new MapViewHandler(mFeatureLayerDTG, mCallout, mMapView, popupInfos, QuanLySuCo.this);
                mFeatureLayerDTG.getFeatureLayer().getFeatureTable().loadAsync();
            }

            mFeatureLayerDTGS.add(mFeatureLayerDTG);
            mMap.getOperationalLayers().add(featureLayer);

        }
        mMapView.setMap(mMap);
        mMapViewHandler.setFeatureLayerDTGs(mFeatureLayerDTGS);
        final List<FeatureLayerDTG> tmpFeatureLayerDTGs = new ArrayList<>();
        mMap.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LinearLayout linnearDisplayLayer = findViewById(R.id.linnearDisplayLayer);
                LinearLayout linnearDisplayLayer1 = findViewById(R.id.linnearDisplayLayer1);
                LayerList layers = mMap.getOperationalLayers();
                int states[][] = {{android.R.attr.state_checked}, {}};
                int colors[] = {R.color.colorTextColor_1, R.color.colorTextColor_1};
                for (final Layer layer : layers) {
                    final CheckBox checkBox = new CheckBox(linnearDisplayLayer.getContext());
                    checkBox.setText(layer.getName());
                    checkBox.setChecked(false);
                    layer.setVisible(false);
                    CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (buttonView.isChecked()) {
                                for (FeatureLayerDTG featureLayerDTG : mFeatureLayerDTGS)
                                    if (featureLayerDTG.getTitleLayer().contentEquals(checkBox.getText())
                                            && !tmpFeatureLayerDTGs.contains(featureLayerDTG)) {
                                        tmpFeatureLayerDTGs.add(featureLayerDTG);
                                        layer.setVisible(true);
                                        break;
                                    }

                            } else {
                                for (FeatureLayerDTG featureLayerDTG : tmpFeatureLayerDTGs)
                                    if (featureLayerDTG.getTitleLayer().contentEquals(checkBox.getText())
                                            && tmpFeatureLayerDTGs.contains(featureLayerDTG)) {
                                        tmpFeatureLayerDTGs.remove(featureLayerDTG);
                                        layer.setVisible(false);
                                        break;
                                    }

                            }
                            mMapViewHandler.setFeatureLayerDTGs(tmpFeatureLayerDTGs);

                        }
                    });
                    if (layer.getName().equals(getString(R.string.ALIAS_THUA_DAT))
                            || layer.getName().equals(getString(R.string.ALIAS_SONG_HO))
                            || layer.getName().equals(getString(R.string.ALIAS_GIAO_THONG))
                            || layer.getName().equals(getString(R.string.ALIAS_HANH_CHINH)))
                        linnearDisplayLayer1.addView(checkBox);
                    else
                        linnearDisplayLayer.addView(checkBox);
                }
//                for (int i = 0; i < linnearDisplayLayer.getChildCount(); i++) {
//                    View v = linnearDisplayLayer.getChildAt(i);
//                    if (v instanceof CheckBox) {
//                        if (((CheckBox) v).getText().equals(getString(R.string.ALIAS_DIEM_SU_CO)))
//                            ((CheckBox) v).setChecked(true);
//                        else ((CheckBox) v).setChecked(false);
//                    }
//                }
            }
        });
        changeStatusOfLocationDataSource();

        final EditText edit_latitude_vido = findViewById(R.id.edit_latitude_vido);
        final EditText edit_longtitude_kinhdo = findViewById(R.id.edit_longtitude_kinhdo);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                try {
                    mMapViewHandler.onSingleTapMapView(e);
                } catch (ArcGISRuntimeException ex) {
                    Log.d("", ex.toString());
                }
                return super.onSingleTapConfirmed(e);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                double[] location = mMapViewHandler.onScroll(e1, e2, distanceX, distanceY);
                edit_longtitude_kinhdo.setText(location[0] + "");
                edit_latitude_vido.setText(location[1] + "");
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                Point position = locationChangedEvent.getLocation().getPosition();
                edit_longtitude_kinhdo.setText(position.getX() + "");
                edit_latitude_vido.setText(position.getY() + "");
                setViewPointCenter(position);
            }
        });
        mGraphicsOverlay = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mGraphicsOverlay);

        findViewById(R.id.layout_layer_open_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_street_map).setOnClickListener(this);
        findViewById(R.id.layout_layer_topo).setOnClickListener(this);
        mFloatButtonLayer = findViewById(R.id.floatBtnLayer);
        mFloatButtonLayer.setOnClickListener(this);
        mFloatButtonAdd = findViewById(R.id.floatBtnView);
        mFloatButtonAdd.setOnClickListener(this);
        mFloatActionButtonAddPoint = findViewById(R.id.floatBtnAddPoint);
        mFloatActionButtonAddPoint.setOnClickListener(this);
        mLinearLayoutCover = findViewById(R.id.layout_cover_quan_ly_su_co);
        mLinearLayoutCover.setOnClickListener(this);
        findViewById(R.id.btn_add_feature_close).setOnClickListener(this);
        findViewById(R.id.btn_layer_close).setOnClickListener(this);
        findViewById(R.id.img_layvitri).setOnClickListener(this);
        mFloatButtonLocation = findViewById(R.id.floatBtnLocation);
        mFloatButtonLocation.setOnClickListener(this);
        mFloatButtonClosePopup = findViewById(R.id.floatBtnClosePopUp);
        mFloatButtonClosePopup.setOnClickListener(this);
        txtTimSuCo = findViewById(R.id.txt_tim_su_co);
        txtTimSuCo.setOnClickListener(this);
        txtTimDiaChi = findViewById(R.id.txt_tim_dia_chi);
        txtTimDiaChi.setOnClickListener(this);
        mLayoutTimKiem = findViewById(R.id.layout_tim_kiem);

        optionSearchFeature();
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

    private void setRendererSuCoFeatureLayer(FeatureLayer mSuCoTanHoaLayer) {
        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
        uniqueValueRenderer.getFieldNames().add("TrangThai");
//        SimpleMarkerSymbol defaultSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.BLACK, 15);
//        SimpleMarkerSymbol chuaxuly = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 15);
//        SimpleMarkerSymbol dangxyly = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.YELLOW, 15);
//        SimpleMarkerSymbol daxuly = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 15);

        PictureMarkerSymbol chuaXuLy = new PictureMarkerSymbol(getString(R.string.url_image_symbol_chuasuachua));
        chuaXuLy.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        chuaXuLy.setWidth(getResources().getInteger(R.integer.size_feature_renderer));
        PictureMarkerSymbol dangXuLy = new PictureMarkerSymbol(getString(R.string.url_image_symbol_dangsuachua));
        dangXuLy.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        dangXuLy.setWidth(getResources().getInteger(R.integer.size_feature_renderer));
        PictureMarkerSymbol daXuLy = new PictureMarkerSymbol(getString(R.string.url_image_symbol_dasuachua));
        daXuLy.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        daXuLy.setWidth(getResources().getInteger(R.integer.size_feature_renderer));
        PictureMarkerSymbol hoanThanh = new PictureMarkerSymbol(getString(R.string.url_image_symbol_hoanthanh));
        hoanThanh.setHeight(getResources().getInteger(R.integer.size_feature_renderer));
        hoanThanh.setWidth(getResources().getInteger(R.integer.size_feature_renderer));
        uniqueValueRenderer.setDefaultSymbol(chuaXuLy);
        uniqueValueRenderer.setDefaultLabel("Chưa xác định");

        List<Object> chuaXuLyValue = new ArrayList<>();
        chuaXuLyValue.add(0);
        List<Object> dangXuLyValue = new ArrayList<>();
        dangXuLyValue.add(1);
        List<Object> daXuLyValue = new ArrayList<>();
        daXuLyValue.add(2);

        List<Object> hoanThanhValue = new ArrayList<>();
        hoanThanhValue.add(3);
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Chưa xử lý", "Chưa xử lý", chuaXuLy, chuaXuLyValue));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Chưa xử lý", "Chưa xử lý", dangXuLy, dangXuLyValue));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Chưa xử lý", "Chưa xử lý", daXuLy, daXuLyValue));
        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
                "Chưa xử lý", "Chưa xử lý", hoanThanh, hoanThanhValue));
        mSuCoTanHoaLayer.setRenderer(uniqueValueRenderer);
    }

    private void changeStatusOfLocationDataSource() {
        mLocationDisplay = mMapView.getLocationDisplay();
//        changeStatusOfLocationDataSource();
        mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
            @Override
            public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {

                // If LocationDisplay started OK, then continue.
                if (dataSourceStatusChangedEvent.isStarted()) return;

                // No error is reported, then continue.
                if (dataSourceStatusChangedEvent.getError() == null) return;

                // If an error is found, handle the failure to start.
                // Check permissions to see if failure may be due to lack of permissions.
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(QuanLySuCo.this,
                        reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(QuanLySuCo.this,
                        reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(QuanLySuCo.this, reqPermissions, requestCode);
                }  // Report other unknown failure types to the user - for example, location services may not // be enabled on the device. //                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent //                            .getSource().getLocationDataSource().getError().getMessage()); //                    Toast.makeText(QuanLySuCo.this, message, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setViewPointCenter(Point position) {
        Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
        mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
    }

    private void setViewPointCenterLongLat(Point position) {
        Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWgs84());
        Geometry geometry1 = GeometryEngine.project(geometry, SpatialReferences.getWebMercator());
        Point point = geometry1.getExtent().getCenter();

        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20);
        Graphic graphic = new Graphic(point, symbol);
        mGraphicsOverlay.getGraphics().add(graphic);

        mMapView.setViewpointCenterAsync(point);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quan_ly_su_co, menu);
        SearchView mTxtSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mTxtSearch.setQueryHint(getString(R.string.title_search));
        mTxtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isSearchingFeature)
                    mMapViewHandler.querySearch(query, mListViewSearch, mSearchAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                } else if (!isSearchingFeature) {

                    try {
                        mSearchAdapter.clear();
                        List<Address> addressList = mGeocoder.getFromLocationName(newText, 5);
                        for (Address address : addressList) {
                            TraCuuAdapter.Item item = new TraCuuAdapter.Item(-1, "", 0, "", address.getAddressLine(0));
                            item.setLatitude(address.getLatitude());
                            item.setLongtitude(address.getLongitude());
                            mSearchAdapter.add(item);
                        }
                        mSearchAdapter.notifyDataSetChanged();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                mLayoutTimKiem.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mLayoutTimKiem.setVisibility(View.INVISIBLE);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_thongke:
                Intent intent = new Intent(this, ThongKeActivity.class);
                this.startActivity(intent);
                break;
            case R.id.nav_tracuu:
                intent = new Intent(this, TraCuuActivity.class);
                this.startActivityForResult(intent, 1);
                break;
            case R.id.nav_find_route:
                intent = new Intent(this, FindRouteActivity.class);
                this.startActivity(intent);
                break;
            case R.id.nav_setting:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivityForResult(intent, 1);
                break;
            case R.id.nav_logOut:
                this.finish();
                break;
            case R.id.nav_delete_searching:
                mGraphicsOverlay.getGraphics().clear();
                mSearchAdapter.clear();
                mSearchAdapter.notifyDataSetChanged();
                break;
            case R.id.nav_visible_float_button:
                if (mFloatButtonLayer.getVisibility() == View.VISIBLE) {
                    mFloatButtonLayer.setVisibility(View.INVISIBLE);
                    mFloatButtonAdd.setVisibility(View.INVISIBLE);
                    mFloatButtonLocation.setVisibility(View.INVISIBLE);
                } else {
                    mFloatButtonLayer.setVisibility(View.VISIBLE);
                    mFloatButtonAdd.setVisibility(View.VISIBLE);
                    mFloatButtonLocation.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestPermisson() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE}, REQUEST_ID_IMAGE_CAPTURE);
        }
//        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
//                this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();

        } else {
            Toast.makeText(QuanLySuCo.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private void optionSearchFeature() {
        this.isSearchingFeature = true;
        txtTimSuCo.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        txtTimSuCo.setBackgroundResource(R.drawable.layout_shape_basemap);

        txtTimDiaChi.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
        txtTimDiaChi.setBackgroundResource(R.drawable.layout_shape_basemap_none);
    }

    private void optionFindRoute() {
        this.isSearchingFeature = false;
        txtTimDiaChi.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        txtTimDiaChi.setBackgroundResource(R.drawable.layout_shape_basemap);

        txtTimSuCo.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
        txtTimSuCo.setBackgroundResource(R.drawable.layout_shape_basemap_none);


    }

    private void expandFloatActionButton() {
//        mLinearLayoutCover.startAnimation(mAnimationFabOpen);
        mLinearLayoutCover.setVisibility(View.VISIBLE);

        mFloatButtonAdd.startAnimation(mAnimationFabOpen);
        mFloatButtonLocation.startAnimation(mAnimationFabOpen);
        mFloatActionButtonAddPoint.startAnimation(mAnimationFabOpen);
        mFloatButtonClosePopup.startAnimation(mAnimationFabOpen);

        mFloatButtonAdd.startAnimation(mAnimationClockwise);

        mFloatButtonLocation.setClickable(true);
        mFloatActionButtonAddPoint.setClickable(true);
        mFloatButtonClosePopup.setClickable(true);
        isOpenFab = true;
    }

    private void collapseFloatActionButton() {
//        mLinearLayoutCover.startAnimation(mAnimationFabClose);
        mLinearLayoutCover.setVisibility(View.GONE);
        mFloatButtonAdd.startAnimation(mAnimationFabClose);
        mFloatButtonLocation.startAnimation(mAnimationFabClose);
        mFloatActionButtonAddPoint.startAnimation(mAnimationFabClose);
        mFloatButtonClosePopup.startAnimation(mAnimationFabClose);


        mFloatButtonAdd.startAnimation(mAnimationAntiClockwise);
        mFloatButtonLocation.setClickable(false);
        mFloatActionButtonAddPoint.setClickable(false);
        mFloatButtonClosePopup.setClickable(false);
        isOpenFab = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_tim_su_co:
                optionSearchFeature();
                break;
            case R.id.txt_tim_dia_chi:
                optionFindRoute();
                break;
            case R.id.floatBtnLayer:
                v.setVisibility(View.INVISIBLE);
                findViewById(R.id.layout_layer).setVisibility(View.VISIBLE);
                mCurrentPoint = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
                break;
            case R.id.layout_layer_open_street_map:
                mMapView.getMap().setMaxScale(1128.497175);
                mMapView.getMap().setBasemap(Basemap.createOpenStreetMap());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_open_street_map);
                mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE);

                setViewPointCenter(mCurrentPoint);
                break;
            case R.id.layout_layer_street_map:
                mMapView.getMap().setMaxScale(1128.497176);
                mMapView.getMap().setBasemap(Basemap.createStreets());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_street_map);

                setViewPointCenter(mCurrentPoint);
                break;
            case R.id.layout_layer_topo:
                mMapView.getMap().setMaxScale(5);
                mMapView.getMap().setBasemap(Basemap.createImageryWithLabels());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_topo);

                setViewPointCenter(mCurrentPoint);
                break;
            case R.id.btn_layer_close:
                findViewById(R.id.layout_layer).setVisibility(View.INVISIBLE);
                findViewById(R.id.floatBtnLayer).setVisibility(View.VISIBLE);
                break;
            case R.id.img_layvitri:
//                mMapViewHandler.capture();
                capture();
                break;
            case R.id.floatBtnAddPoint:
                findViewById(R.id.linear_addfeature).setVisibility(View.VISIBLE);
                findViewById(R.id.img_map_pin).setVisibility(View.VISIBLE);
//                findViewById(R.id.floatBtnAdd).setVisibility(View.GONE);
                mMapViewHandler.setClickBtnAdd(true);
                if (isOpenFab)
                    collapseFloatActionButton();
                break;
            case R.id.floatBtnClosePopUp:
                if (isOpenFab)
                    collapseFloatActionButton();
                if (mCallout != null && mCallout.isShowing())
                    mCallout.dismiss();
                //TODO close popup
                break;
            case R.id.floatBtnView:
                if (isOpenFab)
                    collapseFloatActionButton();
                else
                    expandFloatActionButton();

                break;
            case R.id.layout_cover_quan_ly_su_co:
                if (isOpenFab)
                    collapseFloatActionButton();
                break;
            case R.id.btn_add_feature_close:
                findViewById(R.id.linear_addfeature).setVisibility(View.GONE);
                findViewById(R.id.img_map_pin).setVisibility(View.GONE);
                findViewById(R.id.floatBtnView).setVisibility(View.VISIBLE);
                mMapViewHandler.setClickBtnAdd(false);
                break;
            case R.id.floatBtnLocation:
                if (isOpenFab)
                    collapseFloatActionButton();
                if (!mLocationDisplay.isStarted()) {
                    mLocationDisplay.startAsync();
                    setViewPointCenter(mLocationDisplay.getMapLocation());
                } else mLocationDisplay.stop();
                break;
        }
    }

    public void capture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());

        File photo = ImageFile.getFile(this);
//        this.mUri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", photo);
        this.mUri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.mUri);
//        this.mUri = Uri.fromFile(photo);
        startActivityForResult(cameraIntent, REQUEST_ID_IMAGE_CAPTURE);
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
            case R.id.layout_layer_open_street_map:
                findViewById(R.id.img_layer_open_street_map).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                findViewById(R.id.img_layer_street_map).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                findViewById(R.id.img_layer_topo).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_street_map:
                findViewById(R.id.img_layer_street_map).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                findViewById(R.id.img_layer_open_street_map).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                findViewById(R.id.img_layer_topo).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_topo:
                findViewById(R.id.img_layer_topo).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                findViewById(R.id.img_layer_open_street_map).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                findViewById(R.id.img_layer_street_map).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            final int objectid = data.getIntExtra(getString(R.string.ket_qua_objectid), 1);
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    mMapViewHandler.queryByObjectID(objectid);
                }
            }
        } catch (Exception ignored) {
        }

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE)

        {
            if (resultCode == RESULT_OK) {
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
                            mMapViewHandler.addFeature(image);
                        }
                    } catch (Exception ignored) {
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                MySnackBar.make(mMapView, "Hủy chụp ảnh", false);
            } else {
                MySnackBar.make(mMapView, "Lỗi khi chụp ảnh", false);
            }
        } else if (requestCode == REQUEST_ID_IMAGE_CAPTURE_POPUP) {
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
                            popupInfos.getDialog().dismiss();
                            EditAsync editAsync = new EditAsync(this, (ServiceFeatureTable) mFeatureLayerDTG.getFeatureLayer().getFeatureTable(), mSelectedArcGISFeature, true, image);
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
}