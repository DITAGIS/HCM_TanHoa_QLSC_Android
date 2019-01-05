package vn.ditagis.com.tanhoa.qlsc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask.execute
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.CompoundButtonCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.ArcGISRuntimeException
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.UniqueValueRenderer
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient

import java.util.ArrayList
import java.util.Locale
import java.util.concurrent.ExecutionException

import kotlinx.android.synthetic.main.activity_quan_ly_su_co.*
import kotlinx.android.synthetic.main.app_bar_quan_ly_su_co.*
import kotlinx.android.synthetic.main.content_quan_ly_su_co.*
import kotlinx.android.synthetic.main.layout_feature.view.*
import kotlinx.android.synthetic.main.nav_header_quan_ly_su_co.view.*
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter
import vn.ditagis.com.tanhoa.qlsc.async.*
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DAddress
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication
import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo
import vn.ditagis.com.tanhoa.qlsc.socket.LocationHelper
import vn.ditagis.com.tanhoa.qlsc.utities.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private var mPopUp: Popup? = null
    private var mapViewHandler: MapViewHandler? = null
    private var mSearchAdapter: TraCuuAdapter? = null
    private var mLocationDisplay: LocationDisplay? = null
    private var mGraphicsOverlay: GraphicsOverlay? = null
    private var mIsSearchingFeature = false
    private var mFeatureLayer: FeatureLayer? = null

    private var mPointFindLocation: Point? = null
    private var mGeocoder: Geocoder? = null
    private var mIsAddFeature: Boolean = false
    private var mDLayerInfo: DLayerInfo? = null
    private var mTxtSearchView: SearchView? = null
    private var mArcGISMapImageLayerAdministrator: ArcGISMapImageLayer? = null
    private var mArcGISMapImageLayerThematic: ArcGISMapImageLayer? = null
    private var mListLayerID: MutableList<String>? = null
    private var states: Array<IntArray>? = null
    private var colors: IntArray? = null
    private var mApplication: DApplication? = null
    private var mLoadedOnMap: Int = 0
    internal var mIDSuCoList: List<String>? = null
    internal lateinit var mSuCoList: List<Feature>
    private var doubleBackToExitPressedOnce = false

    private var mLocationHelper: LocationHelper? = null
    private var mLocation: Location? = null


    private var mIsFirstLocating = true
    private var mIsShowComplete = false


    private//        List<Object> chuaXuLyValue = new ArrayList<>();
    //        chuaXuLyValue.add(Constant.TrangThaiSuCo.CHUA_XU_LY);
    //đang xử lý: begin
    //đang xỷ lý: end
    //hoàn thành: begin
    //
    //        uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue(
    //                "Chưa xử lý", "Chưa xử lý", chuaXuLySymbol, chuaXuLyValue));
    //
    val rendererSuCo: UniqueValueRenderer
        @RequiresApi(api = Build.VERSION_CODES.O)
        get() {
            val uniqueValueRenderer = UniqueValueRenderer()
            when (mApplication!!.userDangNhap!!.groupRole) {
                Constant.Role.GROUPROLE_TC -> {
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.TRANG_THAI_THI_CONG)
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.HINH_THUC_PHAT_HIEN_THI_CONG)
                }
                Constant.Role.GROUPROLE_GS -> {
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.TRANG_THAI_GIAM_SAT)
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.HINH_THUC_PHAT_HIEN_GIAM_SAT)
                }
                else -> {
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.TRANG_THAI)
                    uniqueValueRenderer.fieldNames.add(Constant.FieldSuCo.HINH_THUC_PHAT_HIEN)
                }
            }
            val chuaXuLySymbol = PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA)
            chuaXuLySymbol.height = resources.getInteger(R.integer.size_feature_renderer).toFloat()
            chuaXuLySymbol.width = resources.getInteger(R.integer.size_feature_renderer).toFloat()

            val dangXuLySymbol = PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_DANG_SUA_CHUA)
            dangXuLySymbol.height = resources.getInteger(R.integer.size_feature_renderer).toFloat()
            dangXuLySymbol.width = resources.getInteger(R.integer.size_feature_renderer).toFloat()


            val hoanThanhSymbol = PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_HOAN_THANH)
            hoanThanhSymbol.height = resources.getInteger(R.integer.size_feature_renderer).toFloat()
            hoanThanhSymbol.width = resources.getInteger(R.integer.size_feature_renderer).toFloat()


            val beNgamSymbol = PictureMarkerSymbol(Constant.URLSymbol.URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM)
            beNgamSymbol.height = resources.getInteger(R.integer.size_feature_renderer).toFloat()
            beNgamSymbol.width = resources.getInteger(R.integer.size_feature_renderer).toFloat()

            uniqueValueRenderer.defaultSymbol = chuaXuLySymbol
            uniqueValueRenderer.defaultLabel = "Chưa xác định"
            val beNgamChuaXuLyValue = ArrayList<Any>()
            beNgamChuaXuLyValue.add(Constant.TrangThaiSuCo.CHUA_XU_LY)
            beNgamChuaXuLyValue.add(1)
            val dangXuLyValue = ArrayList<Any>()
            dangXuLyValue.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            val dangXuLyValue1 = ArrayList<Any>()
            dangXuLyValue1.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue1.add(1)
            val dangXuLyValue2 = ArrayList<Any>()
            dangXuLyValue2.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue2.add(2)

            val dangXuLyValue3 = ArrayList<Any>()
            dangXuLyValue3.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue3.add(3)

            val dangXuLyValue4 = ArrayList<Any>()
            dangXuLyValue4.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue4.add(4)

            val dangXuLyValue5 = ArrayList<Any>()
            dangXuLyValue5.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue5.add(5)

            val dangXuLyValue6 = ArrayList<Any>()
            dangXuLyValue6.add(Constant.TrangThaiSuCo.DANG_XU_LY)
            dangXuLyValue6.add(6)
            val hoanThanhValue = ArrayList<Any>()
            hoanThanhValue.add(Constant.TrangThaiSuCo.HOAN_THANH)
            val hoanThanhValue1 = ArrayList<Any>()
            hoanThanhValue1.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue1.add(1)
            val hoanThanhValue2 = ArrayList<Any>()
            hoanThanhValue2.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue2.add(2)

            val hoanThanhValue3 = ArrayList<Any>()
            hoanThanhValue3.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue3.add(3)

            val hoanThanhValue4 = ArrayList<Any>()
            hoanThanhValue4.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue4.add(4)

            val hoanThanhValue5 = ArrayList<Any>()
            hoanThanhValue5.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue5.add(5)

            val hoanThanhValue6 = ArrayList<Any>()
            hoanThanhValue6.add(Constant.TrangThaiSuCo.HOAN_THANH)
            hoanThanhValue6.add(6)

            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Chưa xử lý bể ngầm", "Chưa xử lý bể ngầm", beNgamSymbol, beNgamChuaXuLyValue))

            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue1))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue2))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue3))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue4))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue5))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Đang xử lý", "Đang xử lý", dangXuLySymbol, dangXuLyValue6))


            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue1))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue2))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue3))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue4))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue5))
            uniqueValueRenderer.uniqueValues.add(UniqueValueRenderer.UniqueValue(
                    "Hoàn thành", "Hoàn thành", hoanThanhSymbol, hoanThanhValue6))
            return uniqueValueRenderer
        }

    fun setIsAddFeature(isAddFeature: Boolean) {
        mIsAddFeature = isAddFeature
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quan_ly_su_co)
        mListLayerID = ArrayList()
        mApplication = application as DApplication

        states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        colors = intArrayOf(R.color.colorTextColor_1, R.color.colorTextColor_1)
        llayout_main_layer!!.visibility = View.INVISIBLE

        requestPermisson()
        mIDSuCoList = ArrayList()

    }

    private fun init() {
        startGPS()
        startSignIn()
    }

    private fun startGPS() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mLocationHelper = LocationHelper(this, object : LocationHelper.AsyncResponse {
            override fun processFinish(longtitude: Double, latitude: Double) {
            }
        })
        mLocationHelper!!.checkPermission()
        val listener = object : LocationListener {
            @RequiresApi(api = Build.VERSION_CODES.O)
            override fun onLocationChanged(location: Location) {
                mLocation = location
                mApplication!!.setmLocation(mLocation!!)
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}

            override fun onProviderEnabled(s: String) {

            }

            override fun onProviderDisabled(s: String) {
                //                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //                startActivity(i);
                mLocationHelper!!.execute()

                mLocationHelper = LocationHelper(this@MainActivity, object : LocationHelper.AsyncResponse {
                    override fun processFinish(longtitude: Double, latitude: Double) {
                    }
                })
                mLocationHelper!!.checkPermission()
            }
        }
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationManager.requestLocationUpdates("gps", 5000, 0f, listener)
    }

    private fun startSignIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_LOGIN)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private fun prepare() {
        if (nav_view != null && nav_view!!.headerCount > 0) {
            val header = nav_view!!.getHeaderView(0)
            try {
                nav_view!!.menu.add(1, 1, 1, packageManager.getPackageInfo(packageName, 0).versionName)
            } catch (ignored: PackageManager.NameNotFoundException) {

            }

            if (mApplication!!.userDangNhap != null && mApplication!!.userDangNhap!!.userName != null
                    && mApplication!!.userDangNhap!!.displayName != null) {
                if (header.txt_nav_header_tenNV != null)
                    header.txt_nav_header_tenNV!!.text = mApplication!!.userDangNhap!!.userName
                if (header.txt_nav_header_displayname != null)
                    header.txt_nav_header_displayname!!.text = mApplication!!.userDangNhap!!.displayName
            }
        }
        txt_appbar_info!!.text = Html.fromHtml(getString(R.string.info_appbar_load_map_not_complete), Html.FROM_HTML_MODE_LEGACY)
        setLicense()
        mArcGISMapImageLayerThematic = null
        mArcGISMapImageLayerAdministrator = mArcGISMapImageLayerThematic
        linearDisplayLayerLegend!!.removeAllViews()
        val loadLegendAsycn = LoadLegendAsycn(linearDisplayLayerLegend!!,
                this@MainActivity, object : LoadLegendAsycn.AsyncResponse {
            override fun processFinish(output: Void?) {
            }
        })
        loadLegendAsycn.execute()
        mListLayerID!!.clear()
        mGeocoder = Geocoder(this.applicationContext, Locale.getDefault())
        val mMap = ArcGISMap(Basemap.Type.OPEN_STREET_MAP, 10.8035455, 106.6182534, 13)
        setSupportActionBar(toolbar)
        //for camera begin
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        //for camera end
        //đưa listview search ra phía sau
        lstview_search!!.invalidate()
        val items = ArrayList<TraCuuAdapter.Item>()
        mSearchAdapter = TraCuuAdapter(this@MainActivity, items)
        lstview_search!!.adapter = mSearchAdapter
        lstview_search!!.onItemClickListener = this@MainActivity
        val toggle = ActionBarDrawerToggle(this@MainActivity,
                drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout!!.addDrawerListener(toggle)
        toggle.syncState()
        nav_view!!.setNavigationItemSelectedListener(this@MainActivity)
        //        Menu menu = nav_view.getMenu();
        //        MenuItem menuItem = menu.findItem(R.id.nav_version);
        //        try {
        //            menuItem.setTitle("Phiên bản: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        //        } catch (PackageManager.NameNotFoundException e) {
        //            e.printStackTrace();
        //        }

        mapView!!.map = mMap

        mapView!!.map.addDoneLoadingListener { this.handleArcgisMapDoneLoading() }
        mapView!!.onTouchListener = object : DefaultMapViewOnTouchListener(this, mapView) {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                try {
                    if (mapViewHandler != null)
                        mapViewHandler!!.onSingleTapMapView(e!!)
                } catch (ex: ArcGISRuntimeException) {
                    Log.d("", ex.toString())
                }

                return super.onSingleTapConfirmed(e)
            }

            @SuppressLint("SetTextI18n")
            override fun onScroll(e1: MotionEvent, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                mGraphicsOverlay!!.graphics.clear()
                if (mIsAddFeature && mapViewHandler != null) {
                    //center is x, y
                    val geometry = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry
                    val center = geometry.extent.center
                    //project is long, lat
                    //                    Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());

                    //geometry is x,y
                    //                    Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
                    val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20f)
                    val graphic = Graphic(geometry, symbol)

                    mGraphicsOverlay!!.graphics.add(graphic)

                    mPopUp!!.callout!!.location = center
                    mPointFindLocation = center
                }
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

        }
        mLocationDisplay!!.addLocationChangedListener {
            //                Point position = locationChangedEvent.getLocation().getPosition();
            //                setViewPointCenter(position);
        }
        mGraphicsOverlay = GraphicsOverlay()
        mapView!!.graphicsOverlays.add(mGraphicsOverlay)
        mGraphicsOverlay!!.renderer = rendererSuCo

        skbr_hanhchinh_app_bar_quan_ly_su_co!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                mArcGISMapImageLayerAdministrator!!.opacity = i.toFloat() / 100
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        skbr_chuyende_app_bar_quan_ly_su_co!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                mArcGISMapImageLayerThematic!!.opacity = i.toFloat() / 100
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        llayout_layer_open_street_map!!.setOnClickListener(this)
        llayout_layer_street_map!!.setOnClickListener(this)
        llayout_layer_image!!.setOnClickListener(this)


        floatBtnLayer!!.setOnClickListener(this)
        btn_layer_close!!.setOnClickListener(this)
        floatBtnLocation!!.setOnClickListener(this)
        layout_tim_su_co!!.setOnClickListener(this)
        layout_tim_dia_chi!!.setOnClickListener(this)

        optionSearchFeature()

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setServices() {
        try {
            handleFeatureLoading()
            mDLayerInfo = setServiceTable()
            QueryServiceFeatureTableGetListAsync(this@MainActivity,
                    object : QueryServiceFeatureTableGetListAsync.AsyncResponse {
                        override fun processFinish(output: List<Feature>?) {
                            setServiceArcGISImageLayer()
                            mSuCoList = output!!
                            mIDSuCoList = getListIDSuCoFromSuCoThongTins(output)
                            setServiceGraphicsOverLay(mDLayerInfo!!, mIDSuCoList)
                        }

                    }).execute()
        } catch (e: Exception) {
            Log.e("Lỗi set service", e.toString())
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setServiceArcGISImageLayer() {
        for (dLayerInfo in mApplication!!.lstFeatureLayerDTG!!) {
            if (!dLayerInfo.isView ||
                    //Bỏ áp lực, vì áp lực publish lên folder riêng, không thuộc TanHoaGis
                    dLayerInfo.url.contains("ApLuc"))
                continue
            val url = getUrlFromDLayerInfo(dLayerInfo.url)
            if (dLayerInfo.id == getString(R.string.IDLayer_Basemap) && mArcGISMapImageLayerAdministrator == null) {
                mArcGISMapImageLayerAdministrator = ArcGISMapImageLayer(url)
                mArcGISMapImageLayerAdministrator!!.id = dLayerInfo.id
                mapView!!.map.operationalLayers.add(mArcGISMapImageLayerAdministrator)
                mArcGISMapImageLayerAdministrator!!.addDoneLoadingListener {
                    if (mArcGISMapImageLayerAdministrator!!.loadStatus == LoadStatus.LOADED) {
                        val sublayerList = mArcGISMapImageLayerAdministrator!!.sublayers
                        for (sublayer in sublayerList) {
                            addCheckBox(sublayer as ArcGISMapImageSublayer, states, colors, true)
                        }
                        mLoadedOnMap++
                        if (mLoadedOnMap == 3)
                            handleFeatureDoneLoading()
                    }
                }
                mArcGISMapImageLayerAdministrator!!.loadAsync()

            } else if (mArcGISMapImageLayerThematic == null) {
                mArcGISMapImageLayerThematic = ArcGISMapImageLayer(url.replaceFirst("FeatureServer(.*)".toRegex(), "MapServer"))
                mArcGISMapImageLayerThematic!!.name = dLayerInfo.titleLayer
                mArcGISMapImageLayerThematic!!.id = dLayerInfo.id
                //                    mArcGISMapImageLayerThematic.setMaxScale(0);
                //                    mArcGISMapImageLayerThematic.setMinScale(10000000);
                mapView!!.map.operationalLayers.add(mArcGISMapImageLayerThematic)
                mArcGISMapImageLayerThematic!!.addDoneLoadingListener {
                    if (mArcGISMapImageLayerThematic!!.loadStatus == LoadStatus.LOADED) {
                        val sublayerList = mArcGISMapImageLayerThematic!!.sublayers
                        for (sublayer in sublayerList) {
                            addCheckBox(sublayer as ArcGISMapImageSublayer, states, colors, false)
                        }
                        mLoadedOnMap++
                        if (mLoadedOnMap == 3)
                            handleFeatureDoneLoading()
                    }
                }
                mArcGISMapImageLayerThematic!!.loadAsync()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setServiceTable(): DLayerInfo {
        var dLayerInfoSuCo = DLayerInfo()
        for (dLayerInfo in mApplication!!.lstFeatureLayerDTG!!) {
            if (!dLayerInfo.isView ||
                    //Bỏ áp lực, vì áp lực publish lên folder riêng, không thuộc TanHoaGis
                    dLayerInfo.url.contains("ApLuc"))
                continue
            val url = getUrlFromDLayerInfo(dLayerInfo.url)
            when {
                dLayerInfo.id == getString(R.string.IDLayer_DiemSuCo) -> dLayerInfoSuCo = dLayerInfo
                dLayerInfo.id == Constant.IDLayer.ID_SU_CO_THONG_TIN_TABLE -> {
                    val serviceFeatureTable = ServiceFeatureTable(url)
                    val featureLayer = FeatureLayer(serviceFeatureTable)
                    featureLayer.id = dLayerInfo.id
                    featureLayer.name = dLayerInfo.titleLayer
                    mApplication!!.getDFeatureLayer.layerInfoDTG = dLayerInfo
                    mApplication!!.getDFeatureLayer.serviceFeatureTableSuCoThongTin = featureLayer.featureTable as ServiceFeatureTable
                }
                dLayerInfo.id == Constant.IDLayer.ID_HO_SO_VAT_TU_SU_CO_TABLE -> {
                    val serviceFeatureTable = ServiceFeatureTable(url)
                    val featureLayer = FeatureLayer(serviceFeatureTable)
                    featureLayer.id = dLayerInfo.id
                    featureLayer.name = dLayerInfo.titleLayer
                    mApplication!!.getDFeatureLayer.serviceFeatureTableHoSoVatTuSuCo = featureLayer.featureTable as ServiceFeatureTable

                }
                dLayerInfo.id == Constant.IDLayer.ID_SU_CO_THIET_BI_TABLE -> {
                    val serviceFeatureTable = ServiceFeatureTable(url)
                    val featureLayer = FeatureLayer(serviceFeatureTable)
                    featureLayer.id = dLayerInfo.id
                    featureLayer.name = dLayerInfo.titleLayer
                    mApplication!!.getDFeatureLayer.serviceFeatureTableSuCoThietBi = featureLayer.featureTable as ServiceFeatureTable
                }
                dLayerInfo.id == Constant.IDLayer.ID_HO_SO_THIET_BI_SU_CO_TABLE -> {
                    val serviceFeatureTable = ServiceFeatureTable(url)
                    val featureLayer = FeatureLayer(serviceFeatureTable)
                    featureLayer.id = dLayerInfo.id
                    featureLayer.name = dLayerInfo.titleLayer
                    mApplication!!.getDFeatureLayer.serviceFeatureTableHoSoThietBiSuCo = featureLayer.featureTable as ServiceFeatureTable
                }
            }
        }
        return dLayerInfoSuCo
    }

    private fun getListIDSuCoFromSuCoThongTins(features: List<Feature>): List<String> {
        val output = ArrayList<String>()
        for (feature in features) {
            output.add(feature.attributes[Constant.FieldSuCoThongTin.ID_SUCO].toString())
        }
        return output
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    private fun setServiceGraphicsOverLay(dLayerInfo: DLayerInfo, idSuCoList: List<String>?) {
        val url = getUrlFromDLayerInfo(dLayerInfo.url)
        val serviceFeatureTable = ServiceFeatureTable(url)
        mFeatureLayer = FeatureLayer(serviceFeatureTable)

        mFeatureLayer!!.name = dLayerInfo.titleLayer
        mFeatureLayer!!.id = dLayerInfo.id
        mFeatureLayer!!.id = dLayerInfo.id
        mFeatureLayer!!.isPopupEnabled = true
        mFeatureLayer!!.isVisible = true
        mFeatureLayer!!.renderer = rendererSuCo


        //        builder.append("'')");
        mFeatureLayer!!.definitionExpression = getDefinitionWithoutComplete(idSuCoList!!)
        mapView!!.map.operationalLayers.add(mFeatureLayer)
        mFeatureLayer!!.addDoneLoadingListener {
            if (mFeatureLayer!!.loadStatus == LoadStatus.LOADED) {
                QueryFeatureGetListGeometryAsync(this@MainActivity,
                        serviceFeatureTable,
                        object : QueryFeatureGetListGeometryAsync.AsyncResponse {
                            override fun processFinish(output: List<Geometry>?) {
                                //                    for (Geometry geometry : output) {
                                //                        Graphic graphic = new Graphic(geometry.getExtent().getCenter());
                                //                        mGraphicsOverlay.getGraphics().add(graphic);
                                //                    }
                                for (item in mApplication!!.lstFeatureLayerDTG!!) {
                                    if (item.id == Constant.IDLayer.ID_SU_CO_THONG_TIN_TABLE) {
                                        val callout = mapView!!.callout
                                        mApplication!!.getDFeatureLayer.layer = mFeatureLayer
                                        mPopUp = Popup(callout, this@MainActivity, mapView!!, mGeocoder!!)
                                        //                    if (KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(getString(R.string.group_role_giamsat)))
                                        //                        featureLayer.setVisible(false);
                                        //                    Callout callout = mapView.getCallout();
                                        mapViewHandler = MapViewHandler(callout, mapView!!, mPopUp!!, this@MainActivity)
                                        mLoadedOnMap++
                                        if (mLoadedOnMap == 3)
                                            handleFeatureDoneLoading()
                                    }
                                }
                            }
                        }).execute(idSuCoList)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getDefinitionWithoutComplete(idSuCoList: List<String>): String {
        val builder = StringBuilder()
        builder.append(String.format("%s in (", Constant.FieldSuCo.ID_SUCO))
        for (i in idSuCoList.indices) {
            val idSuCo = idSuCoList[i]
            when {
                i != idSuCoList.size - 1 -> builder.append(String.format("'%s' ,", idSuCo))
                mApplication!!.userDangNhap!!.groupRole == Constant.Role.GROUPROLE_TC -> builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FieldSuCo.TRANG_THAI_THI_CONG, Constant.TrangThaiSuCo.HOAN_THANH))
                mApplication!!.userDangNhap!!.groupRole == Constant.Role.GROUPROLE_GS -> builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FieldSuCo.TRANG_THAI_GIAM_SAT, Constant.TrangThaiSuCo.HOAN_THANH))
                else -> builder.append(String.format("'%s' ) and %s <> %d", idSuCo, Constant.FieldSuCo.TRANG_THAI, Constant.TrangThaiSuCo.HOAN_THANH))
            }
        }
        return builder.toString()
    }

    private fun getDefinitionWithComplete(idSuCoList: List<String>): String {
        val builder = StringBuilder()
        builder.append(String.format("%s in (", Constant.FieldSuCo.ID_SUCO))
        for (i in idSuCoList.indices) {
            val idSuCo = idSuCoList[i]
            if (i != idSuCoList.size - 1)
                builder.append(String.format("'%s' ,", idSuCo))
            else
                builder.append(String.format("'%s' ) ", idSuCo))

        }
        return builder.toString()
    }

    private fun getUrlFromDLayerInfo(input: String): String {
        var output = input
        if (!input.startsWith("http"))
            output = "http:$input"
        return output
    }

    private fun addCheckBox(layer: ArcGISMapImageSublayer, states: Array<IntArray>?, colors: IntArray?,
                            isAdministrator: Boolean) {
        @SuppressLint("InflateParams") val layoutFeature = layoutInflater
                .inflate(R.layout.layout_feature, null) as LinearLayout
        layoutFeature.txt_layout_feature.setTextColor(this@MainActivity.getColor(android.R.color.black))
        layoutFeature.txt_layout_feature.text = layer.name
        layoutFeature.ckb_layout_feature.isChecked = false
        layer.isVisible = false
        CompoundButtonCompat.setButtonTintList(layoutFeature.ckb_layout_feature, ColorStateList(states, colors))
        layoutFeature.ckb_layout_feature.setOnCheckedChangeListener { buttonView, _ ->

            if (buttonView.isChecked) {
                if (layoutFeature.txt_layout_feature.text == layer.name)
                    layer.isVisible = true


            } else {
                if (layoutFeature.txt_layout_feature.text == layer.name)
                    layer.isVisible = false
            }
        }
        if (!mListLayerID!!.contains(layer.name)) {
            if (isAdministrator)
                linearDisplayLayerAdministration!!.addView(layoutFeature)
            else
                linearDisplayLayerFeature!!.addView(layoutFeature)
            mListLayerID!!.add(layer.name)
        }
    }

    private fun handleFeatureLoading() {
        mLoadedOnMap = 0
        llayout_info_app_bar!!.visibility = View.VISIBLE
        txt_appbar_info!!.text = Html.fromHtml(getString(R.string.info_appbar_load_map_not_complete), Html.FROM_HTML_MODE_LEGACY)
        floatBtnLocation!!.visibility = View.GONE
        floatBtnLayer!!.visibility = View.GONE
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handleFeatureDoneLoading() {
        llayout_info_app_bar!!.visibility = View.INVISIBLE
        //        txt_appbar_info.setText(Html.fromHtml(getString(R.string.info_appbar_load_map_complete), Html.FROM_HTML_MODE_LEGACY));
        floatBtnLocation!!.visibility = View.VISIBLE
        floatBtnLayer!!.visibility = View.VISIBLE


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handleArcgisMapDoneLoading() {

        mLocationDisplay = mapView!!.locationDisplay
        mLocationDisplay!!.startAsync()
        linearDisplayLayerFeature!!.removeAllViews()
        linearDisplayLayerAdministration!!.removeAllViews()

        setServices()
    }

    private fun setLicense() {
        //way 1
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license))
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

    private fun requestPermisson() {
        val permissionCheck1 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
        val permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED
        val permissionCheck3 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED
        val permissionCheck4 = ContextCompat.checkSelfPermission(this,
                Constant.RequestPermission.REQUEST_PERMISSIONS[3]) == PackageManager.PERMISSION_GRANTED

        if (!(permissionCheck1 && permissionCheck2 && permissionCheck3 && permissionCheck4)) {
            // If permissions are not already granted, request permission from the user.
            ActivityCompat.requestPermissions(this, Constant.RequestPermission.REQUEST_PERMISSIONS, Constant.RequestCode.REQUEST_CODE_PERMISSION)
        }  // Report other unknown failure types to the user - for example, location services may not // be enabled on the device. //                    String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent //                            .getSource().getLocationDataSource().getError().getMessage()); //                    Toast.makeText(QuanLySuCo.this, message, Toast.LENGTH_LONG).show();
        else {
            init()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setViewPointCenter(position: Point) {
        if (mPopUp == null) {
            MySnackBar.make(mapView!!, getString(R.string.load_map_not_complete), true)
        } else {
            val geometry = GeometryEngine.project(position, SpatialReferences.getWebMercator())
            val booleanListenableFuture = mapView!!.setViewpointCenterAsync(geometry.extent.center)
            booleanListenableFuture.addDoneListener {
                try {
                    if (booleanListenableFuture.get()) {
                        this@MainActivity.mPointFindLocation = position
                    }
                    mPopUp!!.showPopupFindLocation(position)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }


            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setViewPointCenterLongLat(position: Point, location: String?) {
        if (mPopUp == null) {
            MySnackBar.make(mapView!!, getString(R.string.load_map_not_complete), true)
        } else {
            val geometry = GeometryEngine.project(position, SpatialReferences.getWgs84())
            val geometry1 = GeometryEngine.project(geometry, SpatialReferences.getWebMercator())
            val point = geometry1.extent.center
            mIsAddFeature = true
            val symbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 20f)
            val graphic = Graphic(point, symbol)
            mGraphicsOverlay!!.graphics.add(graphic)

            mapView!!.setViewpointCenterAsync(point)
            mPopUp!!.showPopupFindLocation(point, location!!)
            this.mPointFindLocation = point
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        var isGranted = true
        for (i in grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
                break
            }
        }
        if (isGranted) {
            init()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(this@MainActivity.applicationContext.getString(R.string.message_permission))
            builder.setPositiveButton(this@MainActivity.applicationContext.getString(R.string.message_btn_ok)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
                val intent = baseContext.packageManager
                        .getLaunchIntentForPackage(baseContext.packageName)!!
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun optionSearchFeature() {
        this.mIsSearchingFeature = true
        layout_tim_su_co!!.setBackgroundResource(R.drawable.layout_border_bottom)
        layout_tim_dia_chi!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
    }

    private fun optionFindRoute() {
        this.mIsSearchingFeature = false
        layout_tim_dia_chi!!.setBackgroundResource(R.drawable.layout_border_bottom)
        layout_tim_su_co!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
    }

    private fun deleteSearching() {
        mGraphicsOverlay!!.graphics.clear()
        mSearchAdapter!!.clear()
        mSearchAdapter!!.notifyDataSetChanged()
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun themDiemSuCoNoCapture() {
        try {
            if (mapViewHandler == null)
                Toast.makeText(this, getString(R.string.load_map_not_complete), Toast.LENGTH_LONG).show()
            else {
                val findLocationAsycn = FindLocationAsycn(this, false,
                        mGeocoder!!, object : FindLocationAsycn.AsyncResponse {
                    override fun processFinish(output: List<DAddress>?) {
                        if (output != null) {

                            val subAdminArea = output[0].subAdminArea
                            //nếu tài khoản có quyền truy cập vào
                            if (subAdminArea == getString(R.string.QuanPhuNhuanName) || subAdminArea == getString(R.string.QuanTanBinhName) || subAdminArea == getString(R.string.QuanTanPhuName)) {
                                mApplication!!.getDiemSuCo!!.point = mPointFindLocation
                                mApplication!!.getDiemSuCo!!.vitri = output[0].location
                                mApplication!!.getDiemSuCo!!.quan = subAdminArea
                                mApplication!!.getDiemSuCo!!.phuong = output[0].locality
                                if (mPopUp!!.callout != null && mPopUp!!.callout!!.isShowing)
                                    mPopUp!!.callout!!.dismiss()

                                val intent = Intent(this@MainActivity, ThemSuCoActivity::class.java)
                                startActivityForResult(intent, Constant.RequestCode.REQUEST_CODE_ADD_FEATURE)
                                mTxtSearchView!!.setQuery("", true)

                            } else {
                                Toast.makeText(this@MainActivity, R.string.message_not_area_management, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@MainActivity, R.string.message_not_area_management, Toast.LENGTH_LONG).show()
                        }
                    }
                })
                val project = GeometryEngine.project(mPointFindLocation!!, SpatialReferences.getWgs84())
                val location = doubleArrayOf(project.extent.center.x, project.extent.center.y)
                findLocationAsycn.setmLongtitude(location[0])
                findLocationAsycn.setmLatitude(location[1])
                findLocationAsycn.execute()
            }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
        }

    }

    private fun visibleFloatActionButton() {
        if (floatBtnLayer!!.visibility == View.VISIBLE) {
            floatBtnLayer!!.visibility = View.INVISIBLE
            floatBtnLocation!!.visibility = View.INVISIBLE
        } else {
            floatBtnLayer!!.visibility = View.VISIBLE
            floatBtnLocation!!.visibility = View.VISIBLE
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBackPressed() {
        mApplication!!.getDiemSuCo!!.point = null
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Nhấn \"Trở về\" một lần nữa để thoát", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds dItems to the action bar if it is present.
        menuInflater.inflate(R.menu.quan_ly_su_co, menu)
        mTxtSearchView = menu.findItem(R.id.action_search).actionView as SearchView
        mTxtSearchView!!.queryHint = getString(R.string.title_search)
        mTxtSearchView!!.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        mTxtSearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                try {
                    if (mIsSearchingFeature && mapViewHandler != null)
                        mapViewHandler!!.querySearch(query, mSearchAdapter!!)
                    else if (query.isNotEmpty()) {
                        deleteSearching()
                        val findLocationAsycn = FindLocationAsycn(this@MainActivity,
                                true, mGeocoder!!,
                                object : FindLocationAsycn.AsyncResponse {
                                    override fun processFinish(output: List<DAddress>?) {
                                        if (output != null) {
                                            mSearchAdapter!!.clear()
                                            mSearchAdapter!!.notifyDataSetChanged()
                                            if (output.isNotEmpty()) {
                                                for (address in output) {
                                                    val item = TraCuuAdapter.Item(-1, "", 0, "", address.location)
                                                    item.latitude = address.latitude
                                                    item.longtitude = address.longtitude
                                                    mSearchAdapter!!.add(item)
                                                }
                                                mSearchAdapter!!.notifyDataSetChanged()

                                                //                                    }
                                            }
                                        }

                                    }

                                })
                        findLocationAsycn.execute(query)

                    }
                } catch (e: Exception) {
                    Log.e("Lỗi tìm kiếm", e.toString())
                }

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                if (newText.trim { it <= ' ' }.isNotEmpty() && !mIsSearchingFeature) {
                    mIsAddFeature = true
                } else {
                    mSearchAdapter!!.clear()
                    mSearchAdapter!!.notifyDataSetChanged()
                    mGraphicsOverlay!!.graphics.clear()
                }
                return false
            }
        })
        menu.findItem(R.id.action_search).setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                visibleFloatActionButton()
                layout_tim_kiem!!.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                layout_tim_kiem!!.visibility = View.INVISIBLE
                visibleFloatActionButton()
                return true
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showHideComplete() {
        for (layer in mapView!!.map.operationalLayers) {
            if (layer is FeatureLayer && layer.getName() == mApplication!!.getDFeatureLayer.layer!!.name && mIDSuCoList != null) {
                if (mIsShowComplete) {
                    layer.definitionExpression = getDefinitionWithoutComplete(mIDSuCoList!!)
                    mIsShowComplete = false
                } else {
                    layer.definitionExpression = getDefinitionWithComplete(mIDSuCoList!!)
                    mIsShowComplete = true
                }
                layer.loadAsync()
            }
        }
    }

    private fun showListTask() {
        val intentListTask = Intent(this@MainActivity, ListTaskActivity::class.java)
        startActivityForResult(intentListTask, Constant.RequestCode.REQUEST_CODE_LIST_TASK)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
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
            R.id.nav_change_password -> {
                val intentChangePassword = Intent(this, DoiMatKhauActivity::class.java)
                startActivity(intentChangePassword)
            }
            R.id.nav_reload -> refresh()
            R.id.nav_show_hide_complete -> showHideComplete()
            R.id.nav_logOut -> {
                mIsShowComplete = false
                if (mPopUp != null && mPopUp!!.callout != null && mPopUp!!.callout!!.isShowing)
                    mPopUp!!.callout!!.dismiss()
                Preference.instance.deletePreferences(getString(R.string.preference_password))
                startSignIn()
            }
            R.id.nav_list_task -> showListTask()
            R.id.nav_delete_searching -> deleteSearching()
            R.id.nav_visible_float_button -> visibleFloatActionButton()
            else -> {
            }
        }


        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }

    fun onClickTextView(v: View) {
        when (v.id) {
            R.id.txt_quanlysuco_legend -> if (linearDisplayLayerLegend!!.visibility == View.VISIBLE) {
                linearDisplayLayerLegend!!.visibility = View.GONE
            } else {
                linearDisplayLayerLegend!!.visibility = View.VISIBLE
            }
            R.id.txt_quanlysuco_hanhchinh ->

                if (linearDisplayLayerAdministration!!.visibility == View.VISIBLE) {
                    skbr_hanhchinh_app_bar_quan_ly_su_co!!.visibility = View.GONE
                    linearDisplayLayerAdministration!!.visibility = View.GONE
                } else {
                    skbr_hanhchinh_app_bar_quan_ly_su_co!!.visibility = View.VISIBLE
                    linearDisplayLayerAdministration!!.visibility = View.VISIBLE
                }
            R.id.txt_quanlysuco_dulieu -> if (linearDisplayLayerFeature!!.visibility == View.VISIBLE) {
                linearDisplayLayerFeature!!.visibility = View.GONE
                skbr_chuyende_app_bar_quan_ly_su_co!!.visibility = View.GONE
            } else {
                linearDisplayLayerFeature!!.visibility = View.VISIBLE
                skbr_chuyende_app_bar_quan_ly_su_co!!.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handlingLocation() {
        if (mIsFirstLocating) {
            mIsFirstLocating = false
            mLocationDisplay!!.stop()
            mLocationDisplay!!.startAsync()
            setViewPointCenter(mLocationDisplay!!.mapLocation)
            mIsAddFeature = true
        } else {
            if (mLocationDisplay!!.isStarted) {
                mLocationDisplay!!.stop()
            }
            if (mPopUp!!.callout != null && mPopUp!!.callout!!.isShowing)
                mPopUp!!.callout!!.dismiss()
            if (!mLocationDisplay!!.isStarted) {
                mLocationDisplay!!.startAsync()
                setViewPointCenter(mLocationDisplay!!.mapLocation)
                mIsAddFeature = true
            }
        }
    }


    fun onClickCheckBox(v: View) {
        if (v is CheckBox) {
            when (v.getId()) {
                R.id.ckb_quanlysuco_hanhchinh ->

                    for (i in 0 until linearDisplayLayerAdministration!!.childCount) {
                        val view = linearDisplayLayerAdministration!!.getChildAt(i)
                        if (view is LinearLayout) {
                            for (j in 0 until view.childCount) {
                                val view1 = view.getChildAt(j)
                                if (view1 is LinearLayout) {
                                    for (k in 0 until view1.childCount) {
                                        val view2 = view1.getChildAt(k)
                                        if (view2 is CheckBox) {
                                            view2.isChecked = v.isChecked
                                        }
                                    }
                                }
                            }
                        }
                    }
                R.id.ckb_quanlysuco_dulieu -> for (i in 0 until linearDisplayLayerFeature!!.childCount) {
                    val view = linearDisplayLayerFeature!!.getChildAt(i)
                    if (view is LinearLayout) {
                        for (j in 0 until view.childCount) {
                            val view1 = view.getChildAt(j)
                            if (view1 is LinearLayout) {
                                for (k in 0 until view1.childCount) {
                                    val view2 = view1.getChildAt(k)
                                    if (view2 is CheckBox) {
                                        view2.isChecked = v.isChecked
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.layout_tim_su_co -> optionSearchFeature()
            R.id.layout_tim_dia_chi -> optionFindRoute()
            R.id.floatBtnLayer -> {
                v.visibility = View.INVISIBLE
                llayout_main_layer!!.visibility = View.VISIBLE
            }
            R.id.llayout_layer_open_street_map -> {
                mapView!!.map.maxScale = 1128.497175
                mapView!!.map.basemap = Basemap.createOpenStreetMap()
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_open_street_map)
                mapView!!.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE)
            }
            R.id.llayout_layer_street_map -> {
                mapView!!.map.maxScale = 1128.497176
                mapView!!.map.basemap = Basemap.createStreets()
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_street_map)
            }
            R.id.llayout_layer_image -> {
                mapView!!.map.maxScale = resources.getInteger(R.integer.MAX_SCALE_IMAGE_WITH_LABLES).toDouble()
                mapView!!.map.basemap = Basemap.createImageryWithLabels()
                handlingColorBackgroundLayerSelected(R.id.llayout_layer_image)
            }
            R.id.btn_layer_close -> {
                llayout_main_layer!!.visibility = View.INVISIBLE
                floatBtnLayer!!.visibility = View.VISIBLE
            }
            //            case R.id.img_chonvitri_themdiemsuco:
            ////                themDiemSuCo();
            //                themDiemSuCoNoCapture();
            //                break;
            //            case R.id.btn_add_feature_close:
            //                if (mapViewHandler != null) {
            //                    findViewById(R.id.llayout_info_app_bar).setVisibility(View.GONE);
            //                    findViewById(R.id.img_map_pin).setVisibility(View.GONE);
            //                    mapViewHandler.setClickBtnAdd(false);
            //                }
            //                break;
            R.id.floatBtnLocation -> handlingLocation()
            R.id.imgBtn_timkiemdiachi_themdiemsuco ->
                //                themDiemSuCo();
                themDiemSuCoNoCapture()
        }//                mCurrentPoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
    }

    @SuppressLint("ResourceAsColor")
    private fun handlingColorBackgroundLayerSelected(id: Int) {
        when (id) {
            R.id.llayout_layer_open_street_map -> {
                img_layer_open_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap)
                txt_layer_open_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                img_layer_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                img_layer_image!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_image!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
            R.id.llayout_layer_street_map -> {
                img_layer_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap)
                txt_layer_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                img_layer_open_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_open_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                img_layer_image!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_image!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
            R.id.llayout_layer_image -> {
                img_layer_image!!.setBackgroundResource(R.drawable.layout_shape_basemap)
                txt_layer_image!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                img_layer_open_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_open_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
                img_layer_street_map!!.setBackgroundResource(R.drawable.layout_shape_basemap_none)
                txt_layer_street_map!!.setTextColor(ContextCompat.getColor(this, R.color.colorTextColor_1))
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handlingListTaskActivityResult() {
        mIsAddFeature = false
        if (mApplication!!.getDiemSuCo!!.trangThai == Constant.TrangThaiSuCo.HOAN_THANH && !mIsShowComplete) {
            showHideComplete()
        }
        //query sự cố theo idsuco, lấy objectid
        val selectedIDSuCo = mApplication!!.getDiemSuCo!!.idSuCo
        mapViewHandler!!.query(String.format("%s = '%s'", Constant.FieldSuCo.ID_SUCO, selectedIDSuCo))
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onResume() {
        if (mApplication!!.isFromNotification) {
            refresh()

        }
        super.onResume()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun refresh() {
        if (CheckConnectInternet.isOnline(this)) {
            mIsShowComplete = false
            if (mPopUp != null && mPopUp!!.callout != null && mPopUp!!.callout!!.isShowing)
                mPopUp!!.callout!!.dismiss()
            QueryServiceFeatureTableGetListAsync(this@MainActivity,
                    object : QueryServiceFeatureTableGetListAsync.AsyncResponse {
                        override fun processFinish(output: List<Feature>?) {
                            mSuCoList = output!!
                            mIDSuCoList = getListIDSuCoFromSuCoThongTins(output)
                            mFeatureLayer!!.loadAsync()
                            mFeatureLayer!!.definitionExpression = getDefinitionWithoutComplete(mIDSuCoList!!)
                            if (mApplication!!.isFromNotification) {
                                mApplication!!.isFromNotification = false
                                try {
                                    mapViewHandler!!.query(String.format("%s = '%s'", Constant.FieldSuCo.ID_SUCO,
                                            mApplication!!.getDiemSuCo!!.idSuCo))
                                } catch (e: Exception) {
                                    Toast.makeText(this@MainActivity, "Vui lòng xem danh sách công việc để biết thêm thông tin!", Toast.LENGTH_LONG).show()
                                }

                            }
                        }

                    }).execute()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun findRoute() {
        //        Point center = mApplication.getGeometry().getExtent().getCenter();
        //        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        //        Point point = project.getExtent().getCenter();
        val uri = String.format("google.navigation:q=%s", Uri.encode(mApplication!!.arcGISFeature!!.attributes[Constant.FieldSuCo.DIA_CHI].toString()))
        //        Uri gmmIntentUri = Uri.parse(String.format("geo:%s,%s",(point.getY()+"").replaceAll(",","."),(point.getX()+"").replaceAll(",",".")));
        val gmmIntentUri = Uri.parse(uri)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        //        setupSymbols();
        //        FindRouteAsync async = new FindRouteAsync(MainActivity.this, MainActivity.this, () -> {
        //
        //        });
        //        async.execute(mSourcePoint, mDestinationPoint);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            when (requestCode) {
                Constant.RequestCode.REQUEST_CODE_SEARCH ->
                    //                    final int objectid = data.getIntExtra(getString(R.string.ket_qua_objectid), 1);
                    if (resultCode == Activity.RESULT_OK) {
                        val selectedIDSuCo = mApplication!!.getDiemSuCo!!.idSuCo
                        mapViewHandler!!.query(String.format("%s = '%s'", Constant.FieldSuCo.ID_SUCO, selectedIDSuCo))
                        //                        mapViewHandler.queryByObjectID(objectid);
                    }
                Constant.RequestCode.REQUEST_CODE_LOGIN -> {
                    if (Activity.RESULT_OK != resultCode) {
                        finish()
                        return
                    } else {
                        mGeocoder = Geocoder(this)
                        // create an empty map instance
                        val preparingAsycn = PreparingByAPIAsycn(this, object : PreparingByAPIAsycn.AsyncResponse {
                            override fun processFinish(success: Boolean?) {
                                prepare()
                            }
                        })
                        if (CheckConnectInternet.isOnline(this))
                            preparingAsycn.execute()
                    }
                    mIsAddFeature = false

                    if (mApplication!!.getDiemSuCo!!.point != null) {
                        mapViewHandler!!.addFeature(mApplication!!.getDiemSuCo!!.point!!)
                        deleteSearching()
                        //                        handlingLocation();
                    }
                }
                Constant.RequestCode.REQUEST_CODE_ADD_FEATURE -> {
                    mIsAddFeature = false
                    if (mApplication!!.getDiemSuCo!!.point != null) {
                        mapViewHandler!!.addFeature(mApplication!!.getDiemSuCo!!.point!!)
                        deleteSearching()
                    }
                }
                Constant.RequestCode.REQUEST_CODE_LIST_TASK -> when (resultCode) {
                    Activity.RESULT_OK -> handlingListTaskActivityResult()
                }
                Constant.RequestCode.REQUEST_CODE_CAPTURE -> when (resultCode) {
                    Activity.RESULT_OK -> try {
                        if (mApplication!!.capture != null) {

                             EditAsync(this, mApplication!!.arcGISFeature!!,
                                    true, mApplication!!.capture,ArrayList(), ArrayList(),
                                    object:EditAsync.AsyncResponse{
                                        override fun processFinish(output: ArcGISFeature?) {
                                            //
                                            if (output != null) {
                                                Toast.makeText(mapView.context, "Đã lưu ảnh", Toast.LENGTH_SHORT).show()
                                                if ((output.getAttributes().get(Constant.FieldSuCoThongTin.TRANG_THAI).toString()).toShort()
                                                        == Constant.TrangThaiSuCo.HOAN_THANH)
                                                    APICompleteAsync (mApplication!!, mApplication!!.arcGISFeature!!.getAttributes().get(Constant.FieldSuCoThongTin.ID_SUCO).toString())
                                                .execute();
                                                if (!output.canEditAttachments())
                                                    Toast.makeText(mapView.context, this@MainActivity.getString(R.string.message_cannot_edit_attachment), Toast.LENGTH_SHORT).show()
                                            } else
                                                Toast.makeText(mapView.context, this@MainActivity.getString(R.string.message_update_failed), Toast.LENGTH_SHORT).show()
                                        }

                                    }).execute(mPopUp!!.featureViewMoreInfoAdapter)
                        }
                    } catch (ignored: Exception) {
                    }
                    Activity.RESULT_CANCELED -> MySnackBar.make(mapView!!, "Hủy chụp ảnh", false)
                    else -> MySnackBar.make(mapView!!, "Lỗi khi chụp ảnh", false)
                }
            }
        } catch (ignored: Exception) {
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position) as TraCuuAdapter.Item
        val objectID = item.objectID
        if (objectID != -1 && mapViewHandler != null) {
            mapViewHandler!!.query(String.format("%s = '%s'", Constant.FieldSuCo.ID_SUCO, item.id))
            //            mapViewHandler.queryByObjectID(objectID);
            mSearchAdapter!!.clear()
            mSearchAdapter!!.notifyDataSetChanged()
        } else {

            setViewPointCenterLongLat(Point(item.longtitude, item.latitude), item.diaChi)
            Log.d("Tọa độ tìm kiếm", String.format("[% ,.9f;% ,.9f]", item.longtitude, item.latitude))
        }
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
}

