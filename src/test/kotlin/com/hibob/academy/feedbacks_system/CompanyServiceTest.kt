package com.hibob.academy.feedbacks_system

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class CompanyServiceTest {
    private var companyDao: CompanyDao = mock(CompanyDao::class.java)
    private var companyService: CompanyService = CompanyService(companyDao)

    @Test
    fun `should return company data when company exists`() {
        val companyName = "Bob"
        val companyData = CompanyData(9L, companyName)

        whenever(companyDao.getCompanyByName(companyName)).thenReturn(companyData)

        val result = companyService.getCompanyByName(companyName)

        assertEquals(companyData, result)
        verify(companyDao).getCompanyByName(companyName)
    }

    @Test
    fun `should throw exception when company does not exist`() {
        val companyName = "Telad"

        whenever(companyDao.getCompanyByName(companyName)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            companyService.getCompanyByName(companyName)
        }

        verify(companyDao).getCompanyByName(companyName)
    }
}
