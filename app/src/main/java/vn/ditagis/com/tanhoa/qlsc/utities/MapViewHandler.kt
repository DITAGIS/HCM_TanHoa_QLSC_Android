package vn.ditagis.com.tanhoa.qlsc.utities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.MotionEvent
import android.widget.Toast

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.Callout
import com.esri.arcgisruntime.mapping.view.MapView

import java.util.GregorianCalendar
import java.util.concurrent.ExecutionException

import vn.ditagis.com.tanhoa.qlsc.R
import vn.ditagis.com.tanhoa.qlsc.adapter.TraCuuAdapter
import vn.ditagis.com.tanhoa.qlsc.async.QueryServiceFeatureTableAsync
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapAddFeatureAsync
import vn.ditagis.com.tanhoa.qlsc.async.SingleTapMapViewAsync
import vn.ditagis.com.tanhoa.qlsc.entities.Constant
import vn.ditagis.com.tanhoa.qlsc.entities.DApplication

/**
 * Created by ThanLe on 2/2/2018.
 */

@SuppressLint("Registered")
class MapViewHandler(private val mDCallout: Callout?, private val mMapView: MapView,
                     private val mPopUp: Popup, private val mActivity: Activity) : Activity() {
    private var suCoTanHoaLayerThiCong: FeatureLayer? = null
    private var mClickPoint: android.graphics.Point? = null
    private var isClickBtnAdd = false
    private var mServiceFeatureTable: ServiceFeatureTable? = null
    private val mApplication: DApplication = mActivity.application as DApplication

    init {
        if (mApplication.getDFeatureLayer.layer != null) {
            this.mServiceFeatureTable = mApplication.getDFeatureLayer.layer!!.featureTable as ServiceFeatureTable
            this.suCoTanHoaLayerThiCong = mApplication.getDFeatureLayer.layer
        }
        //        this.isThiCong = KhachHangDangNhap.getInstance().getKhachHang().getGroupRole().equals(mActivity.getString(R.string.group_role_thicong));
    }


//    fun setClickBtnAdd(clickBtnAdd: Boolean) {
//        isClickBtnAdd = clickBtnAdd
//    }

    fun addFeature(pointFindLocation: Point) {
        mClickPoint = mMapView.locationToScreen(pointFindLocation)

        val singleTapAdddFeatureAsync = SingleTapAddFeatureAsync(mActivity,
                mServiceFeatureTable!!, object : SingleTapAddFeatureAsync.AsyncResponse {
            override fun processFinish(output: Feature?) {
                if (output != null) {
                    if (mDCallout != null && mDCallout.isShowing)
                        mDCallout.dismiss()
                    Toast.makeText(mMapView.context, String.format("Bạn vừa thêm sự cố có id là: %s", output.attributes.get(Constant.FieldSuCo.ID_SUCO)),
                            Toast.LENGTH_LONG).show()
                    //                mPopUp.showPopup((ArcGISFeature) output, true);
                } else {
                    Toast.makeText(mMapView.context, "Không báo được sự cố. Vui lòng thử lại sau",
                            Toast.LENGTH_LONG).show()
                }
            }

        })
        singleTapAdddFeatureAsync.execute()
    }
//
//    fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): DoubleArray {
//        val center = mMapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry.extent.center
//        val project = GeometryEngine.project(center, SpatialReferences.getWgs84())
//        val location = doubleArrayOf(project.extent.center.x, project.extent.center.y)
//        mClickPoint = android.graphics.Point(e2.x.toInt(), e2.y.toInt())
//        //        Geometry geometry = GeometryEngine.project(project, SpatialReferences.getWebMercator());
//        return location
//    }

    fun onSingleTapMapView(e: MotionEvent) {
        val clickPoint = mMapView.screenToLocation(android.graphics.Point(Math.round(e.x), Math.round(e.y)))
        mClickPoint = android.graphics.Point(e.x.toInt(), e.y.toInt())
        if (isClickBtnAdd) {
            mMapView.setViewpointCenterAsync(clickPoint)
        } else {

            val singleTapMapViewAsync = SingleTapMapViewAsync(mActivity, mPopUp, mClickPoint!!, mMapView)
            singleTapMapViewAsync.execute(clickPoint)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun query(query: String) {

        val queryParameters = QueryParameters()
        queryParameters.whereClause = query
        val feature: ListenableFuture<FeatureQueryResult>
        feature = mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        feature.addDoneListener {
            try {
                val result = feature.get()
                if (result.iterator().hasNext()) {
                    val item = result.iterator().next()
                    if (item != null) {
                        if (item.geometry != null) {
                            val extent = item.geometry.extent
                            mApplication.geometry = item.geometry
                            mMapView.setViewpointGeometryAsync(extent)
                        }
                        suCoTanHoaLayerThiCong!!.selectFeature(item)
                        if (mApplication.getDFeatureLayer.layer != null) {
                            val queryClause = String.format("%s = '%s' and %s = '%s'",
                                    Constant.FieldSuCoThongTin.ID_SUCO, item.attributes[Constant.FieldSuCo.ID_SUCO].toString(),
                                    Constant.FieldSuCoThongTin.NHAN_VIEN, mApplication.userDangNhap!!.userName)
                            val queryParameters1 = QueryParameters()
                            queryParameters1.whereClause = queryClause
                            QueryServiceFeatureTableAsync(mActivity,
                                    mApplication.getDFeatureLayer.serviceFeatureTableSuCoThongTin!!,
                                    object : QueryServiceFeatureTableAsync.AsyncResponse {
                                        override fun processFinish(output: Feature?) {
                                            if (output != null) {
                                                mApplication.arcGISFeature = output as ArcGISFeature
                                                mPopUp.showPopup()
                                            }
                                        }

                                    }).execute(queryParameters1)

                        }
                    }
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
    }


    fun querySearch(searchStr: String, adapter: TraCuuAdapter) {
        adapter.clear()
        adapter.notifyDataSetChanged()
        mDCallout!!.dismiss()

        suCoTanHoaLayerThiCong!!.clearSelection()
        val queryParameters = QueryParameters()
        val builder = StringBuilder()
        builder.append("DiaChi  like N'%").append(searchStr).append("%'")
                .append(" or IDSuCo like '%").append(searchStr).append("%'")
        queryParameters.whereClause = builder.toString()
        val featureQueryResult = mServiceFeatureTable!!.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        featureQueryResult.addDoneListener {
            try {
                val result = featureQueryResult.get()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next() as Feature
                    val attributes = item.attributes
                    var formatDate = ""
                    val split = attributes[Constant.FieldSuCo.ID_SUCO].toString().split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    try {
                        formatDate = Constant.DateFormat.DATE_FORMAT.format(GregorianCalendar(Integer.parseInt(split[3]), Integer.parseInt(split[2]), Integer.parseInt(split[1])).time)
                    } catch (ignored: Exception) {

                    }

                    var viTri = ""
                    try {
                        viTri = attributes[Constant.FieldSuCo.DIA_CHI].toString()
                    } catch (ignored: Exception) {

                    }

                    val objectID = Integer.parseInt(attributes[mActivity.getString(R.string.Field_OBJECTID)].toString())
                    var isFound = false
                    for (itemTraCuu in adapter.dItems) {
                        if (itemTraCuu.objectID == objectID) {
                            isFound = true
                            break
                        }
                    }
                    if (!isFound)
                        adapter.add(TraCuuAdapter.Item(objectID,
                                attributes[Constant.FieldSuCo.ID_SUCO].toString(),
                                Integer.parseInt(attributes[Constant.FieldSuCo.TRANG_THAI].toString()), formatDate, viTri))
                    adapter.notifyDataSetChanged()

                    //                        queryByObjectID(Integer.parseInt(attributes.get(Constant.OBJECTID).toString()));
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }

    }

}

