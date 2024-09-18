package com.hibob.academy.service

import com.hibob.academy.dao.Example
import com.hibob.academy.dao.ExampleDao
import com.hibob.academy.dao.OwnerDao
import com.hibob.academy.dao.OwnerData
import com.hibob.kotlinEx.Owner
import jakarta.ws.rs.core.NoContentException
import org.springframework.stereotype.Component

@Component
class OwnerService(private val ownerDao: OwnerDao) {

    fun getOwnersByCompany(companyId: Long): List<OwnerData> {
        if (companyId <= 0) {
            throw IllegalArgumentException("Company ID must be greater than 0")
        }

        val ownersList = ownerDao.getAllOwners(companyId)
        if (ownersList.isEmpty()) {
            throw NoContentException("No owners found for company ID: $companyId")
        }

        return ownersList
    }

    fun addOwner(name: String, companyId: Long, employeeId: String) {
        if (name.isBlank() || employeeId.isBlank()) {
            throw IllegalArgumentException("Name and Employee ID cannot be blank")
        }
        if (companyId <= 0) {
            throw IllegalArgumentException("Invalid Company ID")
        }

        // Call the DAO to insert the owner
        ownerDao.insertOwner(name, companyId, employeeId)
    }
}