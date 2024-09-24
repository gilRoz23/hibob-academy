package com.hibob.academy.feedbacks_system

import org.springframework.stereotype.Component

@Component
class EmployeeService(private val employeeDao: EmployeeDao) {

    fun getEmployee(firstname: String, lastname: String, companyId: Long): EmployeeData {
        val employeeData = employeeDao.getEmployee(firstname, lastname, companyId)
            ?: throw IllegalArgumentException("Employee not found for firstname: $firstname, lastname: $lastname, companyId: $companyId")
        return employeeData
    }
}