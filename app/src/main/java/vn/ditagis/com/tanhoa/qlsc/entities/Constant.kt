package vn.ditagis.com.tanhoa.qlsc.entities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.support.annotation.RequiresApi

import java.text.SimpleDateFormat

@RequiresApi(api = Build.VERSION_CODES.O)
class Constant {


    private var scale: Int = 0

    private var maxScale: Int = 0

    @SuppressLint("SimpleDateFormat")
    object DateFormat {
        //        const val DATE_FORMAT_YEAR_FIRST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        const val DATE_FORMAT_STRING = "dd/MM/yyyy"
        val DATE_FORMAT = SimpleDateFormat(DATE_FORMAT_STRING)
        val DATE_FORMAT_VIEW = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")

    }

    object IDLayer {
        const val ID_SU_CO_THONG_TIN_TABLE = "sucothongtinTBL"
        const val ID_HO_SO_VAT_TU_SU_CO_TABLE = "hosovattusucoTBL"
        const val ID_SU_CO_THIET_BI_TABLE = "thietbiTBL"
        const val ID_HO_SO_THIET_BI_SU_CO_TABLE = "hosothietbisucoTBL"
        const val ID_VAT_TU_TABLE = "vattuTBL"
        const val ID_BASEMAP = "BASEMAP"
        const val DMA = "dmaLYR"
    }

    object FieldDMA {
        const val MA_DMA = "MADMA"
    }

    object FieldSys {
        const val ADD_FIELD = "AddFields"
        const val OUT_FIELD = "OutFields"
        const val DEFINITION = "Definition"
        const val UPDATE_FIELD = "UpdateFields"
        const val LAYER_ID = "LayerID"
        const val LAYER_TITLE = "LayerTitle"
        const val LAYER_URL = "Url"
        const val IS_CREATE = "IsCreate"
        const val IS_DELETE = "IsDelete"
        const val IS_EDIT = "IsEdit"
        const val IS_VIEW = "IsView"
    }

    object Role {
        const val GROUPROLE_TC = "tc"
        const val GROUPROLE_GS = "gs"
        const val ROLE_PGN = "pgn"
    }


    object RequestCode {
        const val REQUEST_CODE_LOGIN = 0
        const val REQUEST_CODE_CAPTURE = 1
        const val REQUEST_CODE_SHOW_CAPTURE = 2
        const val REQUEST_CODE_PERMISSION = 3
        const val REQUEST_CODE_SEARCH = 4
        const val REQUEST_CODE_ADD_FEATURE = 7
        const val REQUEST_CODE_LIST_TASK = 9

    }

    object CodeVatTu {
        const val CAPMOI: Short = 0
        const val THUHOI: Short = 1

    }

    object LoaiSuCo {
        const val LOAISUCO_ONGNGANH: Short = 1
        const val LOAISUCO_ONGCHINH: Short = 2

    }

    object Another {

        const val HINH_THUC_PHAT_HIEN_BE_NGAM = "Bể ngầm"
        const val DOI_TUONG_PHAT_HIEN_CBCNV: Short = 1
        const val URL_BASEMAP = "/3"
    }

    object RequestPermission {
        val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    object URLSymbol {

        const val URL_SYMBOL_CHUA_SUA_CHUA = "$SERVER/images/map/0.png"
        const val URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM = "$SERVER/images/map/bengam.png"
        const val URL_SYMBOL_DANG_SUA_CHUA = "$SERVER/images/map/1.png"
        const val URL_SYMBOL_HOAN_THANH = "$SERVER/images/map/2.png"
    }


    object Socket {
        //        public static final String CHAT_SERVER_URL = SERVER + "/socket/";
        const val CHAT_SERVER_URL = "http://sawagis.vn:3000"
        const val EVENT_LOCATION = "vitrinhanvien"
        const val EVENT_STAFF_NAME = "tennhanvien"
        const val EVENT_GIAO_VIEC = "giaoviecsuco"
        const val APP_ID = "qlsc"
    }

    object UrlApi {
        const val CHECK_VERSION = "http://tanhoa.sawagis.vn/apiv1" + "/versioning/QLSC?version=%s"
        const val LOGIN = "$SERVER_API/Login"
        const val PROFILE = "$SERVER_API/Account/Profile"
        const val GENERATE_ID_SUCO = "$SERVER_API/QuanLySuCo/GenerateIDSuCo"
        const val LAYER_INFO = "$SERVER_API/Account/layerinfo"
        const val CHANGE_PASSWORD = "$SERVER_API/Account/changepass"
        const val COMPLETE = "$SERVER_API/quanlysuco/xacnhanhoanthanhnhanvien?id=%s"
        const val IS_ACCESS = "$SERVER_API/Account/IsAccess/m_qlsc"
        const val GENERATE_ID_SUCOTHONGTIN = "$SERVER_API/QuanLySuCo/GenerateIDSuCoThongTin/"


    }

    init {
        scale = 100
    }

    init {
        maxScale = 4
    }

    object HTTPRequest {
        const val GET_METHOD = "GET"
        const val POST_METHOD = "POST"
        const val AUTHORIZATION = "Authorization"
    }

    object FieldSuCo {
        const val ID_SUCO = "IDSuCo"
        const val LOAI_SU_CO = "LoaiSuCo"
        const val TRANG_THAI = "TrangThai"
        const val GHI_CHU = "GhiChu"
        const val NGUOI_PHAN_ANH = "NguoiPhanAnh"
        const val EMAIL_NGUOI_PHAN_ANH = "EmailNguoiPhanAnh"
        const val TGKHAC_PHUC = "TGKhacPhuc"
        const val TGPHAN_ANH = "TGPhanAnh"
        const val DIA_CHI = "DiaChi"
        const val QUAN = "Quan"
        const val PHUONG = "Phuong"
        const val HINH_THUC_PHAT_HIEN = "HinhThucPhatHien"
        const val SDT = "SDTPhanAnh"
        const val NGUYEN_NHAN = "NGUYENNHAN"
        const val VAT_LIEU = "VatLieu"
        const val DUONG_KINH_ONG = "DuongKinhOng"
        const val DOI_TUONG_PHAT_HIEN = "DoiTuongPhatHien"
        const val TRANG_THAI_THI_CONG = "TrangThaiThiCong"
        const val TRANG_THAI_GIAM_SAT = "TrangThaiGiamSat"
        const val HINH_THUC_PHAT_HIEN_THI_CONG = "HinhThucPhatHienThiCong"
        const val HINH_THUC_PHAT_HIEN_GIAM_SAT = "HinhThucPhatHienGiamSat"
        const val KET_CAU_DUONG = "KetCauDuong"

    }

    object FieldSuCoThongTin {
        const val OBJECT_ID = "OBJECTID"
        const val ID_SUCO = "SuCo"
        const val ID_SUCOTT = "IDSuCoTT"
        const val LOAI_SU_CO = "LoaiSuCo"
        const val TRANG_THAI = "TrangThai"
        const val GHI_CHU = "GhiChu"
        const val NHAN_VIEN = "NhanVien"
        const val TG_CAP_NHAT = "TGCapNhat"
        const val TG_GIAO_VIEC = "TGGiaoViec"
        const val DIA_CHI = "DiaChi"
        const val HINH_THUC_PHAT_HIEN = "HinhThucPhatHien"
        const val NGUYEN_NHAN = "NguyenNhan"
        const val VAT_LIEU = "VatLieu"
        const val DUONG_KINH_ONG = "DuongKinhOng"
        const val DON_VI = "DonVi"
        const val TGTC_DU_KIEN_TU = "TGTCDuKienTu"
        const val TGTC_DU_KIEN_DEN = "TGTCDuKienDen"
        const val TG_HOAN_THANH = "TGHoanThanh"

    }

    object FieldVatTu {
        const val ID_SU_CO = "IDSuCo"
        const val MA_VAT_TU = "MaVatTu"
        const val SO_LUONG = "SoLuong"
        const val TEN_VAT_TU = "TenVatTu"
        const val DON_VI_TINH = "DonViTinh"
        const val LOAI_VAT_TU = "LoaiVatTu"

    }

    object FieldThietBi {
        const val ID_SU_CO = "IDSuCo"
        const val MA_THIET_BI = "MaThietBi"
        const val THOI_GIAN_VAN_HANH = "ThoiGianVanHanh"
        const val TEN_THIET_BI = "TenThietBi"
    }

    object NoOutFieldSuCo {
        const val DON_VI = FieldSuCoThongTin.DON_VI
    }


    object FieldHanhChinh {
        const val ID_HANH_CHINH = "IDHanhChinh"
        const val MA_HUYEN = "MaHuyen"

    }


    object HoSoSuCoMethod {
        const val FIND = 0
        const val INSERT = 2
    }

    object TrangThaiSuCo {
        const val CHUA_XU_LY: Short = 0
        const val DANG_XU_LY: Short = 1
        const val HOAN_THANH: Short = 2

    }

    object HinhThucPhatHien {
        const val BE_NGAM: Short = 1
        const val BE_NOI: Short = 2
    }

    object PhuiDao {
        const val LE_BTXM: Short = 1
    }

    object FieldAccount {
        const val ROLE = "Role"
        const val GROUP_ROLE = "GroupRole"
        const val DISPLAY_NAME = "DisplayName"
    }

    object FileType {
        const val PNG = "image/png"
        const val PDF = "application/pdf"
        const val DOC = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    }

    companion object {

        //    private static final String SERVER = "http://tanhoa.sawagis.vn";
        private const val SERVER = "http://113.161.88.180:798"
        private const val SERVER_API = "$SERVER/apiv1/api"
    }
}
