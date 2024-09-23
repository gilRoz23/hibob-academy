package com.hibob.academy.feedbacks_system

import com.hibob.academy.dao.*
import org.springframework.stereotype.Component
import com.hibob.academy.dao.PetType

@Component
class EmployeeService(private val employeeDao: EmployeeDao) {

    fun getEmployeeId(firstname: String, lastname: String, companyId: Long) : EmployeeData? {

        return employeeDao.getEmployeeId(firstname, lastname, companyId)
    }
}