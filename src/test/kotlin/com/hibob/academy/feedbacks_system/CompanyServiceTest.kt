package com.hibob.academy.feedbacks_system

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class CompanyServiceTest {
    private lateinit var companyDao: CompanyDao
    private lateinit var companyService: CompanyService

    @BeforeEach
    fun setUp() {
        companyDao = mock(CompanyDao::class.java)
        companyService = CompanyService(companyDao)
    }

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

        val exception = assertThrows<NoSuchElementException> {
            companyService.getCompanyByName(companyName)
        }

        assertEquals("No company found with name: $companyName", exception.message)
        verify(companyDao).getCompanyByName(companyName)
    }
}
