package qlsctanhoa.hcm.ditagis.com.qlsc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.AutoScrollHelper;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.esri.arcgisruntime.geometry.Polygon;
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
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import java.util.ArrayList;
import java.util.List;

import qlsctanhoa.hcm.ditagis.com.qlsc.adapter.TraCuuAdapter;
import qlsctanhoa.hcm.ditagis.com.qlsc.libs.FeatureLayerDTG;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Config;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.MapFunctions;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.MapViewHandler;
import qlsctanhoa.hcm.ditagis.com.qlsc.utities.Popup;

public class QuanLySuCo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private Popup popupInfos;
    private MapView mMapView;
    private Callout mCallout;
    private android.graphics.Point mClickPoint;
    private ArcGISFeature mSelectedArcGISFeature;
    private ServiceFeatureTable mServiceFeatureTable;
    private FeatureLayer mSuCoTanHoaLayer;
    private List<FeatureLayerDTG> mFeatureLayerDTGS;
    private String mSelectedArcGISFeatureAttributeValue;
    private boolean isClickBtnAdd = false;

    private MapViewHandler mMapViewHandler;
    private MapFunctions mapFunctions;

    private static double LATITUDE = 10.7554041;
    private static double LONGTITUDE = 106.6546293;
    private static int LEVEL_OF_DETAIL = 12;

    private SearchView mTxtSearch;
    private ListView mListViewSearch;
    private TraCuuAdapter mSearchAdapter;

    private LocationDisplay mLocationDisplay;
    private int requestCode = 2;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 2;
    String[] reqPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_su_co);
        setLicense();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                mMapViewHandler.queryByObjectID(((TraCuuAdapter.Item) parent.getItemAtPosition(position)).getObjectID());
                mSearchAdapter.clear();
                mSearchAdapter.notifyDataSetChanged();
            }
        });

        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        findViewById(R.id.floatBtnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });


        mMapView = (MapView) findViewById(R.id.mapView);

        // create an empty map instance
        final ArcGISMap mMap = new ArcGISMap(Basemap.Type.OPEN_STREET_MAP, LATITUDE, LONGTITUDE, LEVEL_OF_DETAIL);
        mMapView.setMap(mMap);
        mFeatureLayerDTGS = new ArrayList<>();
        // config feature layer service
        ArrayList<Config> configs = Config.FeatureConfig.getConfigs();
        for (Config config : configs) {
            ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(config.getUrl());
            FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
            featureLayer.setName(config.getTitle());
//            featureLayer.setMinScale(config.getMinScale());
            featureLayer.setMaxScale(0);
            featureLayer.setMinScale(1000000);
            FeatureLayerDTG featureLayerDTG = new FeatureLayerDTG(featureLayer);
            featureLayerDTG.setOutFields(config.getOutField());
            featureLayerDTG.setQueryFields(config.getQueryField());
            featureLayerDTG.setTitleLayer(config.getTitle());
            featureLayerDTG.setUpdateFields(config.getUpdateField());
            mFeatureLayerDTGS.add(featureLayerDTG);
//            mMap.getOperationalLayers().add(featureLayer);
        }
        // set the map to be displayed in this view
        mCallout = mMapView.getCallout();

        mServiceFeatureTable = new ServiceFeatureTable(getResources().getString(R.string.service_feature_table));
        mSuCoTanHoaLayer = new FeatureLayer(mServiceFeatureTable);
        popupInfos = new Popup(QuanLySuCo.this, mServiceFeatureTable, mCallout, bottomSheetDialog);
        mSuCoTanHoaLayer.setPopupEnabled(true);
        mMap.getOperationalLayers().add(mSuCoTanHoaLayer);

        this.mapFunctions = new MapFunctions(this, mServiceFeatureTable);
        mMap.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                LinearLayout linnearDisplayLayer = (LinearLayout) findViewById(R.id.linnearDisplayLayer);
                LayerList layers = mMap.getOperationalLayers();
                int states[][] = {{android.R.attr.state_checked}, {}};
                int colors[] = {R.color.colorTextColor_1, R.color.colorTextColor_1};
                for (final Layer layer : layers) {
                    if (layer.getName().equals(Config.Title.title_diemsuco)) continue;
                    CheckBox checkBox = new CheckBox(linnearDisplayLayer.getContext());

                    if (layer.getName().trim().equals("")) {
                        checkBox.setText(Config.Title.title_diemsuco);
                    } else {
                        checkBox.setText(layer.getName());
                    }
                    checkBox.setChecked(true);
                    CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
                    linnearDisplayLayer.addView(checkBox);

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // TODO Auto-generated method stub

                            if (buttonView.isChecked()) {
                                layer.setVisible(true);
                            } else {
                                layer.setVisible(false);
                            }

                        }
                    });

                }
                for (int i = 0; i < linnearDisplayLayer.getChildCount(); i++) {
                    View v = linnearDisplayLayer.getChildAt(i);
                    if (v instanceof CheckBox) {
                        if (((CheckBox) v).getText().equals(Config.Title.title_diemsuco))
                            ((CheckBox) v).setChecked(true);
                        else ((CheckBox) v).setChecked(false);
                    }
                }
            }
        });
        changeStatusOfLocationDataSource();

        mMapViewHandler = new MapViewHandler(mFeatureLayerDTGS, mMap, mSuCoTanHoaLayer, mCallout, mClickPoint, mSelectedArcGISFeature, mMapView, isClickBtnAdd, mServiceFeatureTable, popupInfos, QuanLySuCo.this);
        final EditText edit_latitude = ((EditText) findViewById(R.id.edit_latitude));
        final EditText edit_longtitude = ((EditText) findViewById(R.id.edit_longtitude));
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

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Point center = ((MapView) mMapView).getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
                Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
                edit_longtitude.setText(project.getExtent().getCenter().getX() + "");
                edit_latitude.setText(project.getExtent().getCenter().getY() + "");
                Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                return super.onScale(detector);
            }
        });
        mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                Point position = locationChangedEvent.getLocation().getPosition();
                edit_longtitude.setText(position.getX()+"");
                edit_latitude.setText(position.getY()+"");
                Geometry geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator());
                mMapView.setViewpointCenterAsync(geometry.getExtent().getCenter());
            }
        });
        ((LinearLayout) findViewById(R.id.layout_layer_open_street_map)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.layout_layer_street_map)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.layout_layer_topo)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_layer_close)).setOnClickListener(this);
        ((FloatingActionButton) findViewById(R.id.floatBtnLayer)).setOnClickListener(this);
        this.findViewById(R.id.floatBtnAdd).setOnClickListener(this);
        findViewById(R.id.btn_add_feature_close).setOnClickListener(this);

        findViewById(R.id.img_layvitri).setOnClickListener(this);
        findViewById(R.id.floatBtnLocation).setOnClickListener(this);
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
                boolean permissionCheck1 = ContextCompat.checkSelfPermission(QuanLySuCo.this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED;
                boolean permissionCheck2 = ContextCompat.checkSelfPermission(QuanLySuCo.this, reqPermissions[1]) == PackageManager.PERMISSION_GRANTED;

                if (!(permissionCheck1 && permissionCheck2)) {
                    // If permissions are not already granted, request permission from the user.
                    ActivityCompat.requestPermissions(QuanLySuCo.this, reqPermissions, requestCode);
                } else {
                    // Report other unknown failure types to the user - for example, location services may not
                    // be enabled on the device.
//                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
//                            .getSource().getLocationDataSource().getError().getMessage());
//                    Toast.makeText(QuanLySuCo.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        mTxtSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mTxtSearch.setQueryHint(getString(R.string.title_search));
        mTxtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSuCoTanHoaLayer.clearSelection();
                mMapViewHandler.querySearch(query, mListViewSearch, mSearchAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            QuanLySuCo.this.mListViewSearch.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_thongke) {
            this.mapFunctions.thongKe();
        } else if (id == R.id.nav_tracuu) {
            final Intent intent = new Intent(this, TraCuuActivity.class);
            this.startActivityForResult(intent, 1);
        } else if (id == R.id.nav_setting) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivityForResult(intent, 1);
        } else if (id == R.id.nav_logOut) {
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void add() {
        Toast.makeText(mMapView.getContext().getApplicationContext(), getString(R.string.notify_add_feature), Toast.LENGTH_LONG).show();
        isClickBtnAdd = true;
        mMapViewHandler.setClickBtnAdd(true);
    }

    private void goHome() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();

        } else {
            Toast.makeText(QuanLySuCo.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatBtnLayer:
                v.setVisibility(View.INVISIBLE);
                ((LinearLayout) findViewById(R.id.layout_layer)).setVisibility(View.VISIBLE);
                break;
            case R.id.layout_layer_open_street_map:
                mMapView.getMap().setBasemap(Basemap.createOpenStreetMap());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_open_street_map);
                break;
            case R.id.layout_layer_street_map:
                mMapView.getMap().setBasemap(Basemap.createStreets());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_street_map);
                break;
            case R.id.layout_layer_topo:
                mMapView.getMap().setBasemap(Basemap.createImageryWithLabels());
                handlingColorBackgroundLayerSelected(R.id.layout_layer_topo);
                break;
            case R.id.btn_layer_close:
                ((LinearLayout) findViewById(R.id.layout_layer)).setVisibility(View.INVISIBLE);
                ((FloatingActionButton) findViewById(R.id.floatBtnLayer)).setVisibility(View.VISIBLE);
                break;
            case R.id.img_layvitri:
                mMapViewHandler.addFeature();
                break;
            case R.id.floatBtnAdd:
                ((LinearLayout) findViewById(R.id.linear_addfeature)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.img_map_pin)).setVisibility(View.VISIBLE);
                ((FloatingActionButton) findViewById(R.id.floatBtnAdd)).setVisibility(View.GONE);
                break;
            case R.id.btn_add_feature_close:
                ((LinearLayout) findViewById(R.id.linear_addfeature)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.img_map_pin)).setVisibility(View.GONE);
                ((FloatingActionButton) findViewById(R.id.floatBtnAdd)).setVisibility(View.VISIBLE);
                isClickBtnAdd = false;
                break;
            case R.id.floatBtnLocation:
                if (!mLocationDisplay.isStarted())
                    mLocationDisplay.startAsync();
                else mLocationDisplay.stop();
//                final Point clickPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
//                mMapView.setViewpointCenterAsync(clickPoint);
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void handlingColorBackgroundLayerSelected(int id) {
        switch (id) {
            case R.id.layout_layer_open_street_map:
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_street_map:
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                break;
            case R.id.layout_layer_topo:
                ((ImageView) findViewById(R.id.img_layer_topo)).setBackgroundResource(R.drawable.layout_shape_basemap);
                ((TextView) findViewById(R.id.txt_layer_topo)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                ((ImageView) findViewById(R.id.img_layer_open_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
                ((TextView) findViewById(R.id.txt_layer_open_street_map)).setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1));
                ((ImageView) findViewById(R.id.img_layer_street_map)).setBackgroundResource(R.drawable.layout_shape_basemap_none);
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
        } catch (Exception e) {
        }
    }
}