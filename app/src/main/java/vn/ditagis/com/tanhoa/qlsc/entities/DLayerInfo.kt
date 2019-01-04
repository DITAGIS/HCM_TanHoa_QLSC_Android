package vn.ditagis.com.tanhoa.qlsc.entities


/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

class DLayerInfo {


    lateinit var id: String
    lateinit var titleLayer: String
    lateinit var url: String
    var isCreate: Boolean = false
    var isDelete: Boolean = false
    var isEdit: Boolean = false
    var isView: Boolean = false
    var definition: String? = null
    lateinit var outFields: String
    lateinit var noOutFields: String
    lateinit var addFields: String
    lateinit var updateFields: String

    constructor() {}

    constructor(id: String, titleLayer: String, url: String, isCreate: Boolean, isDelete: Boolean, isEdit: Boolean, isView: Boolean, definition: String?, outFields: String, noOutFields: String, addFields: String, updateFields: String) {
        this.id = id
        this.titleLayer = titleLayer
        this.url = url
        this.isCreate = isCreate
        this.isDelete = isDelete
        this.isEdit = isEdit
        this.isView = isView
        this.definition = definition
        this.outFields = outFields
        this.noOutFields = noOutFields
        this.addFields = addFields
        this.updateFields = updateFields
    }
}
