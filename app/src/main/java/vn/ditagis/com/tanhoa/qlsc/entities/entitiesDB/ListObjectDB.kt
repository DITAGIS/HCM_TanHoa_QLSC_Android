package vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB

import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo
import vn.ditagis.com.tanhoa.qlsc.entities.ThietBi
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu


class ListObjectDB private constructor() {
    var vatTus: List<VatTu>? = null
    var thietBis: List<ThietBi>? = null
    var dmas: List<String>? = null
    private var lstHoSoVatTuSuCoInsert: MutableList<HoSoVatTuSuCo>? = null
    private var hoSoVatTuSuCos: MutableList<HoSoVatTuSuCo>? = null
    private var lstHoSoThietBiSuCoInsert: MutableList<HoSoThietBiSuCo>? = null
    private var hoSoThietBiSuCos: MutableList<HoSoThietBiSuCo>? = null

    fun getLstHoSoThietBiSuCoInsert(): List<HoSoThietBiSuCo>? {
        return lstHoSoThietBiSuCoInsert
    }

    fun setLstHoSoThietBiSuCoInsert(lstHoSoThietBiSuCoInsert: MutableList<HoSoThietBiSuCo>) {
        this.lstHoSoThietBiSuCoInsert = lstHoSoThietBiSuCoInsert
    }


    fun setHoSoThietBiSuCos(hoSoThietBiSuCos: MutableList<HoSoThietBiSuCo>) {
        this.hoSoThietBiSuCos = hoSoThietBiSuCos
    }

    fun clearListHoSoVatTuSuCoChange() {
        lstHoSoVatTuSuCoInsert!!.clear()
    }

    fun clearListHoSoThietBiSuCoChange() {
        lstHoSoThietBiSuCoInsert!!.clear()
    }

    fun getLstHoSoVatTuSuCoInsert(): List<HoSoVatTuSuCo>? {
        return lstHoSoVatTuSuCoInsert
    }

    fun setLstHoSoVatTuSuCoInsert(lstHoSoVatTuSuCoInsert: MutableList<HoSoVatTuSuCo>) {
        this.lstHoSoVatTuSuCoInsert = lstHoSoVatTuSuCoInsert
    }


    fun clearHoSoVatTuSuCos() {
        if (hoSoVatTuSuCos != null)
            hoSoVatTuSuCos!!.clear()
    }

    fun clearHoSoThietBiSuCos() {
        if (hoSoThietBiSuCos != null)
            hoSoThietBiSuCos!!.clear()
    }

    fun setHoSoVatTuSuCos(hoSoVatTuSuCos: MutableList<HoSoVatTuSuCo>) {
        this.hoSoVatTuSuCos = hoSoVatTuSuCos
    }
    companion object {
        val instance = ListObjectDB()
    }
}
