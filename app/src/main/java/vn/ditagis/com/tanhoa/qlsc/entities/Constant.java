package vn.ditagis.com.tanhoa.qlsc.entities;

import android.Manifest;

import java.text.SimpleDateFormat;

public class Constant {
    public static final String DATE_FORMAT_STRING = "dd/MM/yyyy";
    public static final SimpleDateFormat DATE_FORMAT_YEAR_FIRST = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
    public static final SimpleDateFormat DATE_FORMAT_VIEW = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    public static final String ID_SU_CO_THONG_TIN_TABLE = "sucothongtinTBL";
    public static final String ID_HO_SO_VAT_TU_SU_CO_TABLE = "hosovattusucoTBL";
    public static final String ID_SU_CO_THIET_BI_TABLE = "thietbiTBL";
    public static final String ID_HO_SO_THIET_BI_SU_CO_TABLE = "hosothietbisucoTBL";
    public static final String ID_VAT_TU_TABLE = "vattuTBL";
    public static final String ID_BASEMAP = "BASEMAP";
    public static final String ROLE_PGN = "pgn";
    public static final String GROUPROLE_TC = "tc";
    public static final String GROUPROLE_GS = "gs";
    public static final String HINH_THUC_PHAT_HIEN_BE_NGAM = "Bể ngầm";
    public static final int NOTIFICATION_ID = 12345;

    public static final int REQUEST_CODE_PERMISSION = 2;
    public static final int REQUEST_CODE_BASEMAP = 5;
    public static final int REQUEST_CODE_LAYER = 6;
    public static final int REQUEST_CODE_ADD_FEATURE = 7;
    public static final int REQUEST_CODE_ADD_FEATURE_ATTACHMENT = 8;
    public static final int REQUEST_CODE_LIST_TASK = 9;
    public static final int REQUEST_CODE_NOTIFICATION = 100;
    public static final short CODE_VATTU_CAPMOI = 0;
    public static final short CODE_VATTU_THUHOI = 1;
    public static final short DOI_TUONG_PHAT_HIEN_CBCNV = 1;
    public static final String URL_BASEMAP = "/3";
    public static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String SERVER = "http://tanhoa.sawagis.vn";
    private String SERVER_API = SERVER + "/apiv1/api";
    public String URL_SYMBOL_CHUA_SUA_CHUA = SERVER + "/images/map/0.png";
    public String URL_SYMBOL_CHUA_SUA_CHUA_BE_NGAM = SERVER + "/images/map/bengam.png";
    public String URL_SYMBOL_DANG_SUA_CHUA = SERVER + "/images/map/1.png";
    public String URL_SYMBOL_HOAN_THANH = SERVER + "/images/map/2.png";

    public static final short LOAISUCO_ONGNGANH = 1;
    public static final short LOAISUCO_ONGCHINH = 2;
    public String API_LOGIN;


    {
        API_LOGIN = SERVER_API + "/Login";
    }

    public String DISPLAY_NAME;


    {
        DISPLAY_NAME = SERVER_API + "/Account/Profile";
    }

    public String GENERATE_ID_SUCO;


    {
        GENERATE_ID_SUCO = SERVER_API + "/QuanLySuCo/GenerateIDSuCo";
    }

    public String getGENERATE_ID_SUCOTHONGTIN(String idSuCo) {
        return SERVER_API + "/QuanLySuCo/GenerateIDSuCoThongTin/" + idSuCo;
    }

    public String LAYER_INFO;


    {
        LAYER_INFO = SERVER_API + "/Account/layerinfo";
    }

    public String API_COMPLETE;


    {
        API_COMPLETE = SERVER_API + "/quanlysuco/xacnhanhoanthanhnhanvien?id=%s";
    }

    public String IS_ACCESS;

    {
        IS_ACCESS = SERVER_API + "/Account/IsAccess/m_qlsc";
    }

    public String ADMIN_AREA_TPHCM;

    {
        ADMIN_AREA_TPHCM = "Hồ Chí Minh";
    }

    public int SCALE_IMAGE_WITH_LABLES;

    {
        SCALE_IMAGE_WITH_LABLES = 100;
    }

    public int MAX_SCALE_IMAGE_WITH_LABLES;

    {
        MAX_SCALE_IMAGE_WITH_LABLES = 4;
    }

    public class FIELD_SUCO {
        public static final String ID_SUCO = "IDSuCo";
        public static final String LOAI_SU_CO = "LoaiSuCo";
        public static final String TRANG_THAI = "TrangThai";
        public static final String GHI_CHU = "GhiChu";
        public static final String NGUOI_PHAN_ANH = "NguoiPhanAnh";
        public static final String EMAIL_NGUOI_PHAN_ANH = "EmailNguoiPhanAnh";
        public static final String TGKHAC_PHUC = "TGKhacPhuc";
        public static final String TGPHAN_ANH = "TGPhanAnh";
        public static final String DIA_CHI = "DiaChi";
        public static final String QUAN = "Quan";
        public static final String PHUONG = "Phuong";
        public static final String HINH_THUC_PHAT_HIEN = "HinhThucPhatHien";
        public static final String SDT = "SDTPhanAnh";
        public static final String NGUYEN_NHAN = "NGUYENNHAN";
        public static final String VAT_LIEU = "VatLieu";
        public static final String DUONG_KINH_ONG = "DuongKinhOng";
        public static final String DOI_TUONG_PHAT_HIEN = "DoiTuongPhatHien";
        public static final String TRANG_THAI_THI_CONG = "TrangThaiThiCong";
        public static final String TRANG_THAI_GIAM_SAT = "TrangThaiGiamSat";
        public static final String HINH_THUC_PHAT_HIEN_THI_CONG = "HinhThucPhatHienThiCong";
        public static final String HINH_THUC_PHAT_HIEN_GIAM_SAT = "HinhThucPhatHienGiamSat";
        public static final String PHUI_DAO_1_DAI = "PhuiDao1Dai";
        public static final String PHUI_DAO_1_RONG = "PhuiDao1Rong";
        public static final String PHUI_DAO_1_SAU = "PhuiDao1Sau";

    }

    public class FIELD_SUCOTHONGTIN {
        public static final String OBJECT_ID = "OBJECTID";
        public static final String ID_SUCO = "SuCo";
        public static final String ID_SUCOTT = "IDSuCoTT";
        public static final String LOAI_SU_CO = "LoaiSuCo";
        public static final String TRANG_THAI = "TrangThai";
        public static final String GHI_CHU = "GhiChu";
        public static final String NHAN_VIEN = "NhanVien";
        public static final String TG_CAP_NHAT = "TGCapNhat";
        public static final String TG_GIAO_VIEC = "TGGiaoViec";
        public static final String DIA_CHI = "DiaChi";
        public static final String HINH_THUC_PHAT_HIEN = "HinhThucPhatHien";
        public static final String NGUYEN_NHAN = "NguyenNhan";
        public static final String VAT_LIEU = "VatLieu";
        public static final String DUONG_KINH_ONG = "DuongKinhOng";
        public static final String DON_VI = "DonVi";
        public static final String TGTC_DU_KIEN_TU = "TGTCDuKienTu";
        public static final String TGTC_DU_KIEN_DEN = "TGTCDuKienDen";

    }

    public class FIELD_VATTU {
        public static final String ID_SU_CO = "IDSuCo";
        public static final String MA_VAT_TU = "MaVatTu";
        public static final String SO_LUONG = "SoLuong";
        public static final String TEN_VAT_TU = "TenVatTu";
        public static final String DON_VI_TINH = "DonViTinh";
        public static final String LOAI_VAT_TU = "LoaiVatTu";

    }
    public class FIELD_THIETBI {
        public static final String ID_SU_CO = "IDSuCo";
        public static final String MA_THIET_BI = "MaThietBi";
        public static final String THOI_GIAN_VAN_HANH = "ThoiGianVanHanh";
        public static final String TEN_THIET_BI = "TenThietBi";
    }

    public class NO_OUTFIELD_SUCO {
        public static final String DON_VI = FIELD_SUCOTHONGTIN.DON_VI;
    }


    public class FIELD_HANHCHINH {
        public static final String ID_HANH_CHINH = "IDHanhChinh";
        public static final String MA_HUYEN = "MaHuyen";

    }


    public class HOSOSUCO_METHOD {
        public static final int FIND = 0;
        public static final int INSERT = 2;
    }

    public class TRANG_THAI_SU_CO {
        public static final short CHUA_XU_LY = 0;
        public static final short DANG_XU_LY = 1;
        public static final short HOAN_THANH = 2;

    }

    public class FIELD_ACCOUNT {
        public static final String ROLE = "Role";
        public static final String GROUP_ROLE = "GroupRole";
        public static final String DISPLAY_NAME = "DisplayName";
    }

    public class FILE_TYPE {
        public static final String PNG = "PNG";
        public static final String PDF = "application/pdf";
        public static final String DOC = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }

    public Constant() {
    }
}
