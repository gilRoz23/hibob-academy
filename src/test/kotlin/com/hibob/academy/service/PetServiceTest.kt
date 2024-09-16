package com.hibob.academy.service

import com.hibob.academy.dao.PetDao
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hibob.academy.dao.PetType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import com.hibob.academy.dao.PetData
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class PetServiceTest {

    private lateinit var petDao: PetDao
    private lateinit var petService: PetService

    @BeforeEach
    fun setUp() {
        petDao = mock(PetDao::class.java)
        petService = PetService(petDao)
    }

    @Test
    fun `should throw exception for invalid company ID when getting pets by type`() {
        val invalidCompanyId = -1L
        val type = PetType.DOG

        val exception = assertThrows<IllegalArgumentException> {
            petService.getPetsByType(invalidCompanyId, type)
        }

        assertEquals("Company ID must be greater than 0", exception.message)
    }

    @Test
    fun `should get pets by type successfully`() {
        val companyId = 1L
        val type = PetType.DOG
        val pets = listOf(
            PetData(1, companyId, "Murphy", type.type, null), PetData(2, companyId, "Garfield", type.type, null)
        )
        whenever(petDao.petsByType(companyId, type)).thenReturn(pets)
        assertDoesNotThrow {
            petService.getPetsByType(companyId, type)
        }
        verify(petDao).petsByType(companyId, type)
    }

//    @Test
//    fun `should add pet successfully`() {
//        val companyId = 1L
//        val name = "Buddy"
//        val type = PetType.DOG
//        doNothing().`when`(petDao).insertPet(companyId, name, type.type)
//
//        petService.addPet(companyId, name, type)
//
//        verify(petDao).insertPet(companyId, name, type.type)
//    }
//
//    @Test
//    fun `should throw exception for blank pet name`() {
//        val companyId = 1L
//        val type = PetType.DOG
//
//        assertThrows<IllegalArgumentException> {
//            petService.addPet(companyId, "", type)
//        }
//    }
//
//    @Test
//    fun `should adopt pet successfully`() {
//        val petId = 1
//        val ownerId = 2L
//        doNothing().`when`(petDao).adoptPet(petId, ownerId)
//
//        petService.adoptPet(petId, ownerId)
//
//        verify(petDao).adoptPet(petId, ownerId)
//    }
//
//    @Test
//    fun `should throw exception for invalid pet ID during adoption`() {
//        val invalidPetId = -1
//        val ownerId = 2L
//
//        assertThrows<IllegalArgumentException> {
//            petService.adoptPet(invalidPetId, ownerId)
//        }
//    }
//
//    @Test
//    fun `should get owner by pet ID successfully`() {
//        val petId = 1
//        val owner = OwnerData(1, "John Doe", 1L, "E123")
//        whenever(petDao.getOwnerByPetId(petId)).thenReturn(owner)
//
//        val result = petService.getOwnerByPetId(petId)
//
//        assertEquals(owner, result)
//        verify(petDao).getOwnerByPetId(petId)
//    }
//
//    @Test
//    fun `should throw exception for invalid pet ID when getting owner`() {
//        val invalidPetId = -1
//
//        assertThrows<IllegalArgumentException> {
//            petService.getOwnerByPetId(invalidPetId)
//        }
//    }
}