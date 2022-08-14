package com.fduhole.danxinative.state


import android.content.Context
import android.content.SharedPreferences
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.repository.fdu.*
import com.fduhole.danxinative.repository.opentreehole.FDUHoleRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> { get<Context>().getSharedPreferences("app_pref", Context.MODE_PRIVATE) }
    single { GlobalState(get()) }
    single { ZLAppRepository() }
    single { EhallRepository() }
    single { FDUHoleRepository() }
    single { AAORepository() }
    single { LibraryRepository() }
    single { DatacenterRepository() }
}

class GlobalState constructor(private val sp: SharedPreferences) {

    companion object {
        const val KEY_PERSON_INFO = "person_info"
        const val KEY_FDUHOLE_TOKEN = "fduhole_token"
    }

    var person: PersonInfo?
        get() {
            if (sp.contains(KEY_PERSON_INFO)) {
                try {
                    return Json.decodeFromString(sp.getString(KEY_PERSON_INFO, "")!!)
                } catch (_: Exception) {
                }
            }
            return null
        }
        set(value) = sp.edit().putString(KEY_PERSON_INFO, Json.encodeToString(value)).apply()

    var fduholeToken: OTJWTToken?
        get() {
            if (sp.contains(KEY_FDUHOLE_TOKEN)) {
                try {
                    return Json.decodeFromString(sp.getString(KEY_FDUHOLE_TOKEN, "")!!)
                } catch (_: Exception) {
                }

            }
            return null
        }
        set(value) = sp.edit().putString(KEY_FDUHOLE_TOKEN, Json.encodeToString(value)).apply()
}