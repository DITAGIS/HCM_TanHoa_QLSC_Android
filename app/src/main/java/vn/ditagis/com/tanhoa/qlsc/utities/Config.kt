package vn.ditagis.com.tanhoa.qlsc.utities

import android.content.Context

/**
 * Created by NGUYEN HONG on 3/20/2018.
 */

class Config private constructor(){
    var url: String? = null
    var queryField: Array<String>? = null
    var outField: Array<String>? = null
    var updateField: Array<String>? = null
    var alias: String? = null
    var name: String? = null
    var minScale: Int = 0
    private val mContext: Context? = null


//    constructor(url: String, outField: Array<String>, alias: String) {
//        this.url = url
//        this.outField = outField
//        this.alias = alias
//    }
//
//    constructor(url: String, queryField: Array<String>, outField: Array<String>, alias: String) {
//        this.url = url
//        this.queryField = queryField
//        this.outField = outField
//        this.alias = alias
//    }
//
//
//    constructor(url: String, queryField: Array<String>, outField: Array<String>, alias: String, minScale: Int, updateField: Array<String>) {
//        this.url = url
//        this.queryField = queryField
//        this.outField = outField
//        this.updateField = updateField
//        this.alias = alias
//        this.minScale = minScale
//    }
//
//    constructor(url: String, queryField: Array<String>, outField: Array<String>, name: String, alias: String, minScale: Int, updateField: Array<String>) {
//        this.url = url
//        this.queryField = queryField
//        this.outField = outField
//        this.updateField = updateField
//        this.alias = alias
//        this.minScale = minScale
//        this.name = name
//    }

    companion object {
//        private var instance: Config? = null
//
//        fun getInstance(): Config {
//            if (instance == null)
//                instance = Config()
//            return instance
//        }
        val instance = Config()
    }


}
