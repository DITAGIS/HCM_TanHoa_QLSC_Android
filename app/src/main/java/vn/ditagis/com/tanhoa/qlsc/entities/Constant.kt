package vn.ditagis.com.tanhoa.qlsc.entities

import android.Manifest
import android.os.Build
import android.support.annotation.RequiresApi

import java.text.SimpleDateFormat

@RequiresApi(api = Build.VERSION_CODES.O)
class Constant {

    var ADMIN_AREA_TPHCM: String

    var SCALE_IMAGE_WITH_LABLES: Int = 0

    var MAX_SCALE_IMAGE_WITH_LABLES: Int = 0

    object DateFormat {
        val DATE_FORMAT_STRING = "dd/MM/yyyy"
        val DATE_FORMAT_YEAR_FIRST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val DATE_FORMAT = SimpleDateFormat(DATE_FORMAT_STRING)
        val DATE_FORMAT_VIEW = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")

    }

    object IDLayer {
        val ID_SU_CO_THONG_TIN_TABLE = "sucothongtinTBL"
        val ID_HO_SO_VAT_TU_SU_CO_TABLE = "hosovattusucoTBL"
        val ID_SU_CO_THIET_BI_TABLE = "thietbiTBL"
        val ID_HO_SO_THIET_BI_SU_CO_TABLE = "hosothietbisucoTBL"
        val ID_VAT_TU_TABLE = "vattuTBL"
        val ID_BASEMAP = "BASEMAP"
        val DMA = "dmaLYR"
    }
    object FieldDMA {
        val MA_DMA = "MADMA"
    }
    object FieldSys {
        val ADD_FIELD = "AddFields"
        val OUT_FIELD = "OutFields"
        val DEFINITION = "Definition"
        val UPDATE_FIELD = "UpdateFields"
        val LAYER_ID = "LayerID"
        val LAYER_TITLE = "LayerTitle"
        val LAYER_URL = "Url"
        val IS_CREATE = "IsCreate"
        val IS_DELETE = "IsDelete"
        val IS_EDIT = "IsEdit"
        val IS_VIEW = "IsView"
    }
    object Role {
        val GROUPROLE_TC = "tc"
        val GROUPROLE_GS = "gs"
        val ROLE_PGN = "pgn"
    }


    object RequestCode {
        val REQUEST_CODE_LOGIN = 0
        val REQUEST_CODE_CAPTURE = 1
        val REQUEST_CODE_SHOW_CAPTURE = 2
        val REQUEST_CODE_PERMISSION = 3
        val REQUEST_CODE_SEARCH = 4
        val REQUEST_CODE_BASEMAP = 5
        val REQUEST_CODE_LAYER = 6
        val REQUEST_CODE_ADD_FEATURE = 7
        val REQUEST_CODE_ADD_FEATURE_ATTACHMENT = 8
        val REQUEST_CODE_LIST_TASK = 9
        val REQUEST_CODE_NOTIFICATION = 100

    }

    object CodeVatTu {
        val CAPMOI: Short = 0
        val THUHOI: Short = 1

    }

    object LoaiSuCo {
        val LOAISUCO_ONGNGANH: Short = 1
        val LOAISUCO_ONGCHINH: Short = 2

    }

    object Another {

        val HINH_THUC_PHAT_HIEN_BE_NGAM = "Bể ngầm"
        val DOI_TUONG_PHAT_HIEN_CBCNV: Short = 1
        val URL_BASEMAP = "/3"
    }

    object RequestPermission {
        val REQUEST_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    object URLSymbol {

        val URL_SYMBOL_CHUA_SUA_CHUA = "$SERVER/images/map/0.png"
        val URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM = "$SERVER/images/map/bengam.png"
        val URL_SYMBOL_DANG_SUA_CHUA = "$SERVER/images/map/1.png"
        val URL_SYMBOL_HOAN_THANH = "$SERVER/images/map/2.png"
    }


    object Socket {
        //        public static final String CHAT_SERVER_URL = SERVER + "/socket/";
        val CHAT_SERVER_URL = "http://sawagis.vn:3000"
        val EVENT_LOCATION = "vitrinhanvien"
        val EVENT_STAFF_NAME = "tennhanvien"
        val EVENT_GIAO_VIEC = "giaoviecsuco"
        val APP_ID = "qlsc"
        val REQUEST_LOGIN = 0
    }

    object URL_API {
        val CHECK_VERSION = "http://tanhoa.sawagis.vn/apiv1" + "/versioning/QLSC?version=%s"
        val LOGIN = "$SERVER_API/Login"
        val PROFILE = "$SERVER_API/Account/Profile"
        val GENERATE_ID_SUCO = "$SERVER_API/QuanLySuCo/GenerateIDSuCo"
        val LAYER_INFO = "$SERVER_API/Account/layerinfo"
        val CHANGE_PASSWORD = "$SERVER_API/Account/changepass"
        val COMPLETE = "$SERVER_API/quanlysuco/xacnhanhoanthanhnhanvien?id=%s"
        val IS_ACCESS = "$SERVER_API/Account/IsAccess/m_qlsc"
        val GENERATE_ID_SUCOTHONGTIN = "$SERVER_API/QuanLySuCo/GenerateIDSuCoThongTin/"


    }

    init {
        ADMIN_AREA_TPHCM = "Hồ Chí Minh"
    }

    init {
        SCALE_IMAGE_WITH_LABLES = 100
    }

    init {
        MAX_SCALE_IMAGE_WITH_LABLES = 4
    }

    object HTTPRequest {
        val GET_METHOD = "GET"
        val POST_METHOD = "POST"
        val AUTHORIZATION = "Authorization"
    }

    object FIELD_SUCO {
        val ID_SUCO = "IDSuCo"
        val LOAI_SU_CO = "LoaiSuCo"
        val TRANG_THAI = "TrangThai"
        val GHI_CHU = "GhiChu"
        val NGUOI_PHAN_ANH = "NguoiPhanAnh"
        val EMAIL_NGUOI_PHAN_ANH = "EmailNguoiPhanAnh"
        val TGKHAC_PHUC = "TGKhacPhuc"
        val TGPHAN_ANH = "TGPhanAnh"
        val DIA_CHI = "DiaChi"
        val QUAN = "Quan"
        val PHUONG = "Phuong"
        val HINH_THUC_PHAT_HIEN = "HinhThucPhatHien"
        val SDT = "SDTPhanAnh"
        val NGUYEN_NHAN = "NGUYENNHAN"
        val VAT_LIEU = "VatLieu"
        val DUONG_KINH_ONG = "DuongKinhOng"
        val DOI_TUONG_PHAT_HIEN = "DoiTuongPhatHien"
        val TRANG_THAI_THI_CONG = "TrangThaiThiCong"
        val TRANG_THAI_GIAM_SAT = "TrangThaiGiamSat"
        val HINH_THUC_PHAT_HIEN_THI_CONG = "HinhThucPhatHienThiCong"
        val HINH_THUC_PHAT_HIEN_GIAM_SAT = "HinhThucPhatHienGiamSat"
        val KET_CAU_DUONG = "KetCauDuong"
        val PHUI_DAO_1_DAI = "PhuiDao1Dai"
        val PHUI_DAO_1_RONG = "PhuiDao1Rong"
        val PHUI_DAO_1_SAU = "PhuiDao1Sau"

    }

    object FIELD_SUCOTHONGTIN {
        val OBJECT_ID = "OBJECTID"
        val ID_SUCO = "SuCo"
        val ID_SUCOTT = "IDSuCoTT"
        val LOAI_SU_CO = "LoaiSuCo"
        val TRANG_THAI = "TrangThai"
        val GHI_CHU = "GhiChu"
        val NHAN_VIEN = "NhanVien"
        val TG_CAP_NHAT = "TGCapNhat"
        val TG_GIAO_VIEC = "TGGiaoViec"
        val DIA_CHI = "DiaChi"
        val HINH_THUC_PHAT_HIEN = "HinhThucPhatHien"
        val NGUYEN_NHAN = "NguyenNhan"
        val VAT_LIEU = "VatLieu"
        val DUONG_KINH_ONG = "DuongKinhOng"
        val DON_VI = "DonVi"
        val TGTC_DU_KIEN_TU = "TGTCDuKienTu"
        val TGTC_DU_KIEN_DEN = "TGTCDuKienDen"

    }

    object FIELD_VATTU {
        val ID_SU_CO = "IDSuCo"
        val MA_VAT_TU = "MaVatTu"
        val SO_LUONG = "SoLuong"
        val TEN_VAT_TU = "TenVatTu"
        val DON_VI_TINH = "DonViTinh"
        val LOAI_VAT_TU = "LoaiVatTu"

    }

    object FIELD_THIETBI {
        val ID_SU_CO = "IDSuCo"
        val MA_THIET_BI = "MaThietBi"
        val THOI_GIAN_VAN_HANH = "ThoiGianVanHanh"
        val TEN_THIET_BI = "TenThietBi"
    }

    object NO_OUTFIELD_SUCO {
        val DON_VI = FIELD_SUCOTHONGTIN.DON_VI
    }


    object FIELD_HANHCHINH {
        val ID_HANH_CHINH = "IDHanhChinh"
        val MA_HUYEN = "MaHuyen"

    }


    object HOSOSUCO_METHOD {
        val FIND = 0
        val INSERT = 2
    }

    object TRANG_THAI_SU_CO {
        val CHUA_XU_LY: Short = 0
        val DANG_XU_LY: Short = 1
        val HOAN_THANH: Short = 2

    }

    object HinhThucPhatHien {
        val BE_NGAM: Short = 1
        val BE_NOI: Short = 2
    }

    object PhuiDao {
        val LE_BTXM: Short = 1
    }

    object FIELD_ACCOUNT {
        val ROLE = "Role"
        val GROUP_ROLE = "GroupRole"
        val DISPLAY_NAME = "DisplayName"
    }

    object FILE_TYPE {
        val PNG = "image/png"
        val PDF = "application/pdf"
        val DOC = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    }

    companion object {

        //    private static final String SERVER = "http://tanhoa.sawagis.vn";
        private val SERVER = "http://113.161.88.180:798"
        private val SERVER_API = "$SERVER/apiv1/api"
    }
}
