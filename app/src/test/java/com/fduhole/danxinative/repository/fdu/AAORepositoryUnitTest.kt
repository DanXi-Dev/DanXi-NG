package com.fduhole.danxinative.repository.fdu

import com.fduhole.danxinative.model.AAONotice
import com.fduhole.danxinative.repository.fdu.AAORepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.junit.Test
import org.junit.Assert.*

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

    @Test
    fun getNoticeList() = runTest {
        val notices = repo.getNoticeList(2)
        assertEquals(14, notices?.size)
    }
}