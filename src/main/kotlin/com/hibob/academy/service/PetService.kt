package com.hibob.academy.service

import com.hibob.academy.dao.*
import org.springframework.stereotype.Component
import com.hibob.academy.dao.PetType

@Component
class PetService(private val petDao: PetDao) {

    fun getPetsByType(companyId: Long, type: PetType): List<PetData> {
        validateCompanyId(companyId)
        validatePetType(type)

        val pets = petDao.petsByType(companyId, type)
        return pets
    }

    fun addPet(companyId: Long, name: String, type: PetType) {
        validateName(name)
        validateCompanyId(companyId)
        validatePetType(type)
        petDao.insertPet(companyId, name, type.type)
    }

    fun adoptPet(petId: Int, ownerId: Long) {
        validatePetId(petId)
        validateOwnerId(ownerId)
        petDao.adoptPet(petId, ownerId)
    }

    fun getOwnerByPetId(petId: Int): OwnerData? {
        validatePetId(petId)
        return petDao.getOwnerByPetId(petId)
    }

    fun getPetsByOwnerId(ownerId: Long): List<PetData> {
        validateOwnerId(ownerId)
        val pets = petDao.getPetsByOwnerId(ownerId)
        return pets
    }

    fun countPetsByType(companyId: Long): List<Map<String, Int>> {
        validateCompanyId(companyId)
        val mapsList = petDao.countPetsByType(companyId)
        return mapsList
    }

    private fun validatePetType(type: PetType) {
        if (!PetType.entries.map { it.type }.contains(type.type)) {
            throw IllegalArgumentException("Invalid pet type: ${type.type}")
        }
    }

    private fun validateCompanyId(companyId: Long) {
        if (companyId <= 0) {
            throw IllegalArgumentException("Company ID must be greater than 0")
        }
    }

    private fun validateName(name: String) {
        if (name.isBlank()) {
            throw IllegalArgumentException("Name cannot be blank")
        }
    }

    private fun validatePetId(petId: Int) {
        if (petId <= 0) {
            throw IllegalArgumentException("Pet ID must be greater than 0")
        }
    }

    private fun validateOwnerId(ownerId: Long) {
        if (ownerId <= 0) {
            throw IllegalArgumentException("Owner ID must be greater than 0")
        }
    }

}