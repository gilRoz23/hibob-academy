package com.hibob.academy.feedbacks_system

import com.hibob.academy.dao.*
import org.springframework.stereotype.Component
import com.hibob.academy.dao.PetType

@Component
class CompanyService(private val companyDao: CompanyDao) {

    fun getCompanyIdByName(companyName: String): CompanyData? {
        return companyDao.getCompanyIdByName(companyName)
    }
}