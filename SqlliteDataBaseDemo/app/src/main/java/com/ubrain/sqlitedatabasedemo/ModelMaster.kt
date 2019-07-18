package com.ubrain.sqlitedatabasedemo

class ModelMaster {
    data class CallApiRecipeModel(var titleS: String, var textS: String)
    data class DBModel(var userId: Int, var title:String, var subTitle:String)
    data class RcyNameContactModel(var personName: String? = null, var personContact: String? = null)
    data class RecycleModel(var recycleImg: Int = 0, var recycleTitle: String? = null)
}