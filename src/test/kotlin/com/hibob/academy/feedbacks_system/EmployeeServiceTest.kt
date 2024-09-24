package com.hibob.academy.feedbacks_system

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class EmployeeServiceTest {
    private var employeeDao: EmployeeDao = mock(EmployeeDao::class.java)
    private var employeeService: EmployeeService = EmployeeService(employeeDao)

    @Test
    fun `should return employee data when employee exists`() {
        val firstname = "Gilad"
        val lastname = "Rozner"
        val companyId = 1L
        val employeeData = EmployeeData(1L, firstname, lastname, "employee", 1L)

        whenever(employeeDao.getEmployee(firstname, lastname, companyId)).thenReturn(employeeData)

        val result = employeeService.getEmployee(firstname, lastname, companyId)

        assertEquals(employeeData, result)
        verify(employeeDao).getEmployee(firstname, lastname, companyId)
    }

    @Test
    fun `should throw exception when employee does not exist`() {
        val firstname = "Scoobi"
        val lastname = "Doo"
        val companyId = 1L

        whenever(employeeDao.getEmployee(firstname, lastname, companyId)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            employeeService.getEmployee(firstname, lastname, companyId)
        }

        verify(employeeDao).getEmployee(firstname, lastname, companyId)
    }
}
