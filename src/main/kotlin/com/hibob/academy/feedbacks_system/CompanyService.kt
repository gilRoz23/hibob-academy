package com.hibob.academy.feedbacks_system

import org.springframework.stereotype.Component

@Component
class CompanyService(private val companyDao: CompanyDao) {

    fun getCompanyByName(companyName: String): CompanyData {
        val companyData = companyDao.getCompanyByName(companyName)
            ?: throw NoSuchElementException("No company found with name: $companyName")
        return companyData
    }
}