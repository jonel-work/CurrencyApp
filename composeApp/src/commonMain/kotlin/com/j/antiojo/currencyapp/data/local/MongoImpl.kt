package com.j.antiojo.currencyapp.data.local

import com.j.antiojo.currencyapp.data.mapper.toCurrencyDTO
import com.j.antiojo.currencyapp.data.mapper.toRealm
import com.j.antiojo.currencyapp.domain.MongoRepository
import com.j.antiojo.currencyapp.domain.model.Currency
import com.j.antiojo.currencyapp.domain.model.CurrencyRealm
import com.j.antiojo.currencyapp.domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoImpl : MongoRepository {

    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(CurrencyRealm::class)
            )
                .compactOnLaunch()
                .build()

            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: Currency) {
        realm?.write { copyToRealm(currency.toRealm()) }
    }

    override fun readCurrencyDataFromRealm(): Flow<RequestState<List<Currency>>> {
        return realm?.query<CurrencyRealm>()
            ?.asFlow()
            ?.map { result ->
                println("HomeViewModel result.list == ${result.list}")
                val currency = result.list.map { it.toCurrencyDTO() }
                println("HomeViewModel currency.list == $currency")
                RequestState.Success(data = currency)
            } ?: flow { RequestState.Error(message = "Realm not configured.") }
    }

    override suspend fun cleanUp() {
      realm?.write {
          val currentCollection = this.query<CurrencyRealm>()
          delete(currentCollection)
      }
    }
}