package qlsctanhoa.hcm.ditagis.com.qlsc.utities;

import java.util.ArrayList;

/**
 * Created by NGUYEN HONG on 3/20/2018.
 */

public class Config {
    private String url;
    private String[] queryField;
    private String[] outField;
    private String[] updateField;
    private String title;
    private int minScale;

    public String[] getUpdateField() {
        return updateField;
    }

    public void setUpdateField(String[] updateField) {
        this.updateField = updateField;
    }

    public Config(String url, String[] outField, String title) {
        this.url = url;
        this.outField = outField;
        this.title = title;
    }

    public Config(String url, String[] queryField, String[] outField, String title) {
        this.url = url;
        this.queryField = queryField;
        this.outField = outField;
        this.title = title;
    }


    public Config(String url, String[] queryField, String[] outField, String title, int minScale, String[] updateField) {
        this.url = url;
        this.queryField = queryField;
        this.outField = outField;
        this.updateField = updateField;
        this.title = title;
        this.minScale = minScale;
    }

    public int getMinScale() {
        return minScale;
    }

    public void setMinScale(int minScale) {
        this.minScale = minScale;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getQueryField() {
        return queryField;
    }

    public void setQueryField(String[] queryField) {
        this.queryField = queryField;
    }

    public String[] getOutField() {
        return outField;
    }

    public void setOutField(String[] outField) {
        this.outField = outField;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class FeatureConfig {


        public static ArrayList<Config> getConfigs() {
            ArrayList<Config> configs = new ArrayList<>();
            configs.add(new Config(Urls.url_dma, QueryFields.queryFields_dma, OutFields.outFields_dma, Title.title_dma, MinScale.minScale_dma, new String[]{}));
            configs.add(new Config(Urls.url_van, QueryFields.queryFields_van, OutFields.outFields_van, Title.title_van, MinScale.minScale_van, new String[]{}));
            configs.add(new Config(Urls.url_diemsuco, QueryFields.queryFields_diemsuco, OutFields.outFields_diemsuco, Title.title_diemsuco, MinScale.minScale_diemsuco, UpdateFields.updateFields_suco));
            configs.add(new Config(Urls.url_donghotong, QueryFields.queryFields_donghotong, OutFields.outFields_donghotong, Title.title_donghotong, MinScale.minScale_donghotong, new String[]{}));
            configs.add(new Config(Urls.url_ongnganh, QueryFields.queryFields_ongnganh, OutFields.outFields_ongnganh, Title.title_ongnganh, MinScale.minScale_ongnganh, new String[]{}));
            configs.add(new Config(Urls.url_ongphanphoi, QueryFields.queryFields_ongphanphoi, OutFields.outFields_ongphanphoi, Title.title_ongphanphoi, MinScale.minScale_ongphanphoi, new String[]{}));
            configs.add(new Config(Urls.url_ongtruyendan, QueryFields.queryFields_ongtruyendan, OutFields.outFields_ongtruyendan, Title.title_ongtruyendan, MinScale.minScale_ongtruyendan, new String[]{}));
            configs.add(new Config(Urls.url_moinoi, QueryFields.queryFields_moinoi, OutFields.outFields_moinoi, Title.title_moinoi, MinScale.minScale_moinoi, new String[]{}));
            configs.add(new Config(Urls.url_truhong, QueryFields.queryFields_truhong, OutFields.outFields_truhong, Title.title_truhong, MinScale.minScale_truhong, new String[]{}));
            configs.add(new Config(Urls.url_diemapluc, QueryFields.queryFields_diemapluc, OutFields.outFields_diemapluc, Title.title_diemapluc, MinScale.minScale_diemapluc, new String[]{}));
            configs.add(new Config(Urls.url_diemcuoiong, QueryFields.queryFields_diemcuoiong, OutFields.outFields_diemcuoiong, Title.title_diemcuoiong, MinScale.minScale_diemcuoiong, new String[]{}));
            configs.add(new Config(Urls.url_donghokhachhang, QueryFields.queryFields_donghokhachhang, OutFields.outFields_donghokhachhang, Title.title_donghokhachhang, MinScale.minScale_donghokhachhang, new String[]{}));
            return configs;
        }
    }

    public static class UpdateFields {
        public static String[] updateFields_suco = {
                "NgayKhacPhuc",
                "NgaySuaChua",
                "NguyenNhan",
                "NguoiSuaChua",
                "GhiChuVatTu",
                "ViTri",
                "GhiChu",
                "NgayCapNhat",
                "PhanLoaiSuCo",
                "DonViSuaChua",
                "DuongKinhOng",
                "HinhThucPhatHien",
                "VatLieu",
                "SoNha",
                "TrangThai",
                "NGUOICAPNHAT",
                "KetQuaKiemTra",
                "ApLuc",
                "THOIGIANTHUCHIEN",
                "CONGNHANTHUCHIEN",
                "VatLieuOng",
                "LoaiSuCo"
        };


    }

    public static class QueryFields {
        public static String[] queryFields_dma = {"OBJECTID", "MADMA", "TENDMA"};
        public static String[] queryFields_diemcuoiong = {"OBJECTID", "MaDMA", "TenQuan"};
        public static String[] queryFields_diemapluc = {"OBJECTID", "IDDiemApLuc", "MaDMA"};
        public static String[] queryFields_van = {"OBJECTID", "DuongChinh", "TenHanhChinh", "TenQuan", "VITRIVAN"};
        public static String[] queryFields_truhong = {"OBJECTID", "IDMATRUHONG", "MADMA", "TenHanhChinh", "TenQuan"};
        public static String[] queryFields_moinoi = {"OBJECTID", "TenHanhChinh", "TenQuan"};
        public static String[] queryFields_diemsuco = {"OBJECTID", "IDSuCo", "ViTri", "NgayCapNhat"};
        public static String[] queryFields_ongtruyendan = {"OBJECTID", "MADMA", "VatLieu"};
        public static String[] queryFields_ongphanphoi = {"OBJECTID", "MADMA", "TenConDuong", "VatLieu"};
        public static String[] queryFields_ongnganh = {"OBJECTID", "MADMA", "TenChuSoHuu", "SoNhaMoi", "TenConDuong"};
        public static String[] queryFields_donghotong = {"OBJECTID", "MADMA", "MADUONG", "TenHanhChinh", "TenQuan"};
        public static String[] queryFields_donghokhachhang = {"OBJECTID", "DBDONGHONUOC", "TENTHUEBAO", "TenDuong", "CODONGHO", "NGAYLAPDAT"};

    }

    public static class OutFields {
        public static String[] outFields_dma = {"OBJECTID", "MADMA", "TENDMA"};
        public static String[] outFields_diemcuoiong = {"OBJECTID", "MaDMA", "TenQuan"};
        public static String[] outFields_diemapluc = {"OBJECTID", "IDDiemApLuc", "MaDMA"};
        public static String[] outFields_van = {"OBJECTID", "DuongChinh", "TenHanhChinh", "TenQuan", "VITRIVAN"};
        public static String[] outFields_truhong = {"OBJECTID", "IDMATRUHONG", "MADMA", "TenHanhChinh", "TenQuan"};
        public static String[] outFields_moinoi = {"OBJECTID", "TenHanhChinh", "TenQuan"};
        public static String[] outFields_diemsuco = {"OBJECTID", "IDSuCo", "ViTri", "NgayCapNhat"};
        public static String[] outFields_ongtruyendan = {"OBJECTID", "MADMA", "VatLieu"};
        public static String[] outFields_ongphanphoi = {"OBJECTID", "MADMA", "TenConDuong", "VatLieu"};
        public static String[] outFields_ongnganh = {"OBJECTID", "MADMA", "TenChuSoHuu", "SoNhaMoi", "TenConDuong"};
        public static String[] outFields_donghotong = {"OBJECTID", "MADMA", "MADUONG", "TenHanhChinh", "TenQuan"};
        public static String[] outFields_donghokhachhang = {"OBJECTID", "DBDONGHONUOC", "TENTHUEBAO", "TenDuong"};

    }

    public static class Urls {
        //         "https://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/0"
        private static String url_donghokhachhang = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/0";
        private static String url_donghotong = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/1";
        private static String url_ongnganh = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/2";
        private static String url_ongphanphoi = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/3";
        private static String url_ongtruyendan = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/4";
        private static String url_moinoi = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/5";
        private static String url_diemsuco = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/6";
        private static String url_truhong = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/7";
        private static String url_van = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/8";
        private static String url_diemapluc = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/9";
        private static String url_diemcuoiong = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/10";
        private static String url_dma = "http://113.161.88.180:800/arcgis/rest/services/TanHoa/TANHOAGIS/MapServer/11";
    }

    public static class Title {
        public static String title_donghokhachhang = "Đồng hồ khách hàng";
        public static String title_donghotong = "Đồng hồ tổng";
        public static String title_ongnganh = "Ống ngánh";
        public static String title_ongphanphoi = "Ống phân phối";
        public static String title_ongtruyendan = "Ống truyền dẫn";
        public static String title_moinoi = "Mối nối";
        public static String title_diemsuco = "Điểm sự cố";
        public static String title_truhong = "Trụ Họng";
        public static String title_van = "Van";
        public static String title_diemapluc = "Điểm Áp Lực";
        public static String title_diemcuoiong = "Điểm Cuối Ống";
        public static String title_dma = "DMA";
    }

    public static class MinScale {
        private static int minScale_donghokhachhang = 2000;
        private static int minScale_donghotong = 1000000;
        private static int minScale_ongnganh = 2000;
        private static int minScale_ongphanphoi = 10000;
        private static int minScale_ongtruyendan = 1000000;
        private static int minScale_moinoi = 2000;
        private static int minScale_diemsuco = 10000;
        private static int minScale_truhong = 2000;
        private static int minScale_van = 2000;
        private static int minScale_diemapluc = 2000;
        private static int minScale_diemcuoiong = 2000;
        private static int minScale_dma = 20000;
    }

}
