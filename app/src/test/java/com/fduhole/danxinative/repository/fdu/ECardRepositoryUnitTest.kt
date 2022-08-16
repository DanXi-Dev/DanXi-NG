package com.fduhole.danxinative.repository.fdu

import android.content.Context
import android.content.SharedPreferences
import com.fduhole.danxinative.model.PersonInfo
import com.fduhole.danxinative.state.GlobalState
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get


class ECardRepositoryUnitTest : KoinTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNoticeList() = runTest {
        startKoin {
            modules(
                module {
                    single { SPMockBuilder().createContext() }
                    single<SharedPreferences> { get<Context>().getSharedPreferences("app_pref", Context.MODE_PRIVATE) }
                    single { GlobalState(get()) }
                    single { ECardRepository() }
                }
            )
        }
        get<GlobalState>().person = PersonInfo("", "", "")
        val repo = get<ECardRepository>()
        assert(repo.getCardPersonInfo().name == "")
        assert(repo.getCardRecords(0).isNotEmpty())
    }
}