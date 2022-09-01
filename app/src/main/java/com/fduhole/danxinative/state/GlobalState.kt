package com.fduhole.danxinative.state


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.model.opentreehole.OTJWTToken
import com.fduhole.danxinative.repository.fdu.*
import com.fduhole.danxinative.repository.opentreehole.FDUHoleRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single { MasterKey.Builder(get()).setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC).build() }
    single {
        EncryptedSharedPreferences.create(get<Context>(),
            "app_pref",
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }
    single { GlobalState(get()) }
    single { ZLAppRepository() }
    single { EhallRepository() }
    single { FDUHoleRepository() }
    single { AAORepository() }
    single { LibraryRepository() }
    single { ECardRepository() }
}

class GlobalState constructor(val preferences: SharedPreferences) {

    companion object {
        const val KEY_PERSON_INFO = "person_info"
        const val KEY_FDUHOLE_TOKEN = "fduhole_token"

        /**
         * The keys below are defined in `root_preferences.xml`.
         */
        const val KEY_HIGH_CONTRAST_COLOR = "high_contrast_color"
    }


    inner class Pref {
        var highContrastColor: Boolean
            get() = preferences.getBoolean(KEY_HIGH_CONTRAST_COLOR, false)
            set(value) = preferences.edit { putBoolean(KEY_HIGH_CONTRAST_COLOR, value) }
    }

    var person: PersonInfo?
        get() {
            if (preferences.contains(KEY_PERSON_INFO)) {
                try {
                    return Json.decodeFromString(preferences.getString(KEY_PERSON_INFO, "")!!)
                } catch (_: Exception) {
                }
            }
            return null
        }
        set(value) = preferences.edit().putString(KEY_PERSON_INFO, Json.encodeToString(value)).apply()

    var fduholeToken: OTJWTToken?
        get() {
            if (preferences.contains(KEY_FDUHOLE_TOKEN)) {
                try {
                    return Json.decodeFromString(preferences.getString(KEY_FDUHOLE_TOKEN, "")!!)
                } catch (_: Exception) {
                }

            }
            return null
        }
        set(value) = preferences.edit().putString(KEY_FDUHOLE_TOKEN, Json.encodeToString(value)).apply()
}