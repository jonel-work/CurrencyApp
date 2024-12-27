package com.j.antiojo.currencyapp.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class CurrencyRealm : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var code: String = ""
    var value: Double = 0.0
}