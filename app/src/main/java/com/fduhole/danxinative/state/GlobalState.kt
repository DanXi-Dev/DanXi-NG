package com.fduhole.danxinative.state


import android.content.Context
import android.content.SharedPreferences
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.repository.fdu.ZLAppRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> { get<Context>().getSharedPreferences("app_pref", Context.MODE_PRIVATE) }
    single { GlobalState(get()) }
    single { ZLAppRepository() }
}

class GlobalState constructor(private val sp: SharedPreferences) {

    companion object {
        const val KEY_PERSON_INFO = "person_info"
    }

    var person: PersonInfo?
        get() = if (sp.contains(KEY_PERSON_INFO))
            Json.decodeFromString(sp.getString(KEY_PERSON_INFO, "")!!) else null
        set(value) = sp.edit().putString(KEY_PERSON_INFO, Json.encodeToString(value)).apply()
}