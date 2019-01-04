package vn.ditagis.com.tanhoa.qlsc.entities

import com.esri.arcgisruntime.geometry.Point

import java.util.Date

class DiemSuCo {
    var idSuCo: String? = null
    var vitri: String? = null
    var ngayPhanAnh: Date? = null
    var nguoiPhanAnh: String? = null
    var sdtPhanAnh: String? = null
    var emailPhanAnh: String? = null
    var trangThai: Short = 0
    var hinhThucPhatHien: Short = 0
    var quan: String? = null
    var phuong: String? = null
    var ghiChu: String? = null
    var nguoiCapNhat: String? = null
    var ngayCapNhat: Date? = null
    var nguyenNhan: String? = null
    var point: Point? = null
    var image: ByteArray? = null
    var ketCauDuong: Short = 0
    var phuiDaoDai: Double? = null
    var phuiDaoRong: Double? = null
    var phuiDaoSau: Double? = null

    fun clear() {
        idSuCo = null
        vitri = null
        ngayPhanAnh = null
        nguoiPhanAnh = null
        sdtPhanAnh = null
        emailPhanAnh = null
        trangThai = Constant.TRANG_THAI_SU_CO.CHUA_XU_LY
        hinhThucPhatHien = Constant.HinhThucPhatHien.BE_NOI
        quan = null
        phuong = null
        ghiChu = null
        nguoiCapNhat = null
        ngayCapNhat = null
        nguyenNhan = null
        point = null
        image = null
        ketCauDuong = Constant.PhuiDao.LE_BTXM
        phuiDaoDai = null
        phuiDaoRong = null
        phuiDaoSau = null
    }
}
