package com.fduhole.danxinative.repository.fdu

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AAORepositoryUnitTest {
    private val repo = AAORepository()

    @Test
    fun getUISLoginURL() {
        val loginURL = repo.getUISLoginURL()
        assertEquals("https://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fjwc.fudan.edu.cn%2Feb%2Fb7%2Fc9397a388023%2Fpage.psp", loginURL)
    }

    @Test
    fun getHost() {
        assertEquals("https://jwc.fudan.edu.cn", repo.getHost())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNoticeList() = runTest {
        val notices = repo.getNoticeList(2)
        assertEquals(14, notices.size)
    }
}