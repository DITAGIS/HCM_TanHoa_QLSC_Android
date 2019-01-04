package vn.ditagis.com.tanhoa.qlsc.entities

import android.app.Application
import android.location.Location
import android.os.Build
import android.support.annotation.RequiresApi
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.geometry.Geometry
import java.net.URISyntaxException
import io.socket.client.IO
import io.socket.client.Socket

@RequiresApi(api = Build.VERSION_CODES.O)
class DApplication : Application() {
    var lstFeatureLayerDTG: List<DLayerInfo>? = null

    var capture: ByteArray? = null

    var userDangNhap: User? = null

    var getDFeatureLayer: DFeatureLayer = DFeatureLayer()

    var channelID: Int = 0

    var socket: Socket? = null
        private set

    var loaiVatTu: Short = 0

    var getDiemSuCo: DiemSuCo? = null

    var isFromNotification: Boolean = false

    var geometry: Geometry? = null

    var arcGISFeature: ArcGISFeature? = null

    private var mLocation: Location? = null
    var isCheckedVersion: Boolean = false

    init {
        capture = null
    }

    init {
        try {
            socket = IO.socket(Constant.Socket.CHAT_SERVER_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }

    init {
        getDiemSuCo = DiemSuCo()
    }

    fun getmLocation(): Location? {
        return mLocation
    }

    fun setmLocation(mLocation: Location?) {
        this.mLocation = mLocation
    }
}
