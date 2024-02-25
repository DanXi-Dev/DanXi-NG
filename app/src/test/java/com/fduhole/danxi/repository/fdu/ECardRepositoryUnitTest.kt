package com.fduhole.danxi.repository.fdu


//class ECardRepositoryUnitTest : KoinTest {
//
//    @Test
//    fun getNoticeList() = runTest {
//        startKoin {
//            modules(
//                module {
//                    single { SPMockBuilder().createContext() }
//                    single<SharedPreferences> { get<Context>().getSharedPreferences("app_pref", Context.MODE_PRIVATE) }
//                    single { GlobalState(get()) }
//                    single { ECardRepository() }
//                }
//            )
//        }
//
//        // Insert your login credentials for testing here.
//        get<GlobalState>().person = PersonInfo("", "", "")
//
//        val repo = get<ECardRepository>()
//        assert(repo.getCardPersonInfo().name.isNotEmpty())
//        assert(repo.getCardRecords(0).isNotEmpty())
//        assert(repo.getQRCode().isNotEmpty())
//    }
//}