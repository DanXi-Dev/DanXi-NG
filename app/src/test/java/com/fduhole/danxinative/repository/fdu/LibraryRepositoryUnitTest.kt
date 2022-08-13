package com.fduhole.danxinative.repository.fdu

import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class LibraryRepositoryUnitTest {
    private val repo = LibraryRepository()

    @Test fun getHost() {
        assertEquals("http://10.55.101.62/book/show", repo.getHost())
    }

    @Test fun getLibraryAttendanceList() = runTest {
        val attendance = repo.getAttendanceList()
        assertEquals(6, attendance.size)
    }
}