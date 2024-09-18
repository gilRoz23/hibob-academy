package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Autowired constructor(private val sql: DSLContext)  {

    private val ownerDao = OwnerDao(sql)
    private val petDao = PetDao(sql)
    val companyId = Random.nextLong()

    @BeforeEach
    @AfterEach
    fun cleanup() {
        val allPets = petDao.getAllRecords()
        allPets.forEach { pet -> petDao.deletePetById(pet.id) }

        val allOwners = ownerDao.getAllRecords()
        allOwners.forEach { owner -> ownerDao.deleteOwnerById(owner.id) }
    }

    @Test
    fun `inserting pet test`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        val petsList = petDao.petsByType(companyId, PetType.DOG)
        assertEquals(1, petsList.size)
        assertEquals(companyId, petsList[0].companyId)
        assertEquals("Murphy", petsList[0].name)
        assertEquals("dog", petsList[0].type)
    }

    @Test
    fun `pet by type when in the db not exists pets with this type`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        val petsList = petDao.petsByType(companyId, PetType.CAT)
        assertEquals(emptyList<PetData>(), petsList)
    }

    @Test
    fun `adopt a pet`(){
        petDao.insertPet(companyId, "Murphy", "dog")
        val petsListBeforeUpdate = petDao.petsByType(companyId, PetType.DOG)
        assertEquals(null, petsListBeforeUpdate[0].ownerId)
        petDao.adoptPet(petsListBeforeUpdate[0].id, 1L)
        val petsListAfterUpdate = petDao.petsByType(companyId, PetType.DOG)
        assertEquals(1L, petsListAfterUpdate[0].ownerId)
    }

    @Test
    fun `adopt a pet which is already adopted`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        val pet = petDao.petsByType(companyId, PetType.DOG)[0]
        petDao.adoptPet(pet.id, 1L)
        petDao.adoptPet(pet.id, 2L)
        val petsListAfterAdoption = petDao.petsByType(companyId, PetType.DOG)
        assertEquals(1L, petsListAfterAdoption[0].ownerId)
    }

    @Test
    fun `get owner data by petId`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        ownerDao.insertOwner("Gilad", companyId, "1")
        val pet = petDao.petsByType(companyId, PetType.DOG)[0]
        petDao.adoptPet(pet.id, ownerDao.getAllOwners(companyId)[0].id)
        val owner = petDao.getOwnerByPetId(pet.id)
        assertEquals("Gilad", owner?.name)
        assertEquals(companyId, owner?.companyId)
        assertEquals("1", owner?.employeeId.toString())
    }

    @Test
    fun `get owner data by petId when no owner exists`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        val pet = petDao.petsByType(companyId, PetType.DOG)[0]
        val owner = petDao.getOwnerByPetId(pet.id)
        assertEquals(null, owner)
    }

//    FROM HERE TESTS ADDED TO SQL2. DO NOT TOUCH UPWARD
    @Test
    fun `count pets by type`(){
        petDao.insertPet(companyId, "Murphy", "dog")
        petDao.insertPet(companyId, "Hachiko", "dog")
        petDao.insertPet(companyId, "Kliford", "dog")
        petDao.insertPet(companyId, "Garfield", "cat")
        petDao.insertPet(companyId, "Tom", "cat")
        val typesList = petDao.countPetsByType(companyId)
        assertEquals(listOf(mapOf("cat" to 2), mapOf("dog" to 3)), typesList)
    }

    @Test
    fun `count pets by type with zero pets`(){
        val typesList = petDao.countPetsByType(companyId)
        val blankList: List<Map<String, Int>> = emptyList()
        assertEquals(blankList, typesList)
    }

    @Test
    fun `get pets by owner Id`(){
        petDao.insertPet(companyId, "Murphy", "dog")
        petDao.insertPet(companyId, "Garfield", "cat")
        petDao.insertPet(companyId, "Tom", "cat")
        ownerDao.insertOwner("Gilad", companyId, "1")
        ownerDao.insertOwner("Bob", companyId, "2")
        val murphyId = petDao.petsByType(companyId, PetType.DOG)[0].id
        val garfieldId = petDao.petsByType(companyId, PetType.CAT)[0].id
        val tomId = petDao.petsByType(companyId, PetType.CAT)[1].id
        val giladId = ownerDao.getAllOwners(companyId)[0].id
        val bobId = ownerDao.getAllOwners(companyId)[1].id
        petDao.adoptPet(murphyId,giladId)
        petDao.adoptPet(garfieldId,giladId)
        petDao.adoptPet(tomId,bobId)
        val petsList = petDao.getPetsByOwnerId(giladId)
        assertEquals(listOf(PetData(murphyId, companyId, "Murphy", "dog", giladId), PetData(garfieldId, companyId, "Garfield", "cat", giladId)), petsList)
    }

    // JOOQ-BATCH TESTS
    @Test
    fun `adopt multiple pets`() {
        petDao.insertPet(companyId, "Murphy", "dog")
        petDao.insertPet(companyId, "Hachiko", "dog")
        petDao.insertPet(companyId, "Garfield", "cat")
        ownerDao.insertOwner("Gilad", companyId, "1")

        val murphyId = petDao.petsByType(companyId, PetType.DOG)[0].id
        val hachikoId = petDao.petsByType(companyId, PetType.DOG)[1].id
        val garfieldId = petDao.petsByType(companyId, PetType.CAT)[0].id
        val giladId = ownerDao.getAllOwners(companyId)[0].id

        petDao.adoptMultiplePets(giladId, listOf(murphyId, hachikoId, garfieldId))

        // Verify that the pets have been adopted by Gilad
        val petsList = petDao.getPetsByOwnerId(giladId)
        assertEquals(
            listOf(
                PetData(murphyId, companyId, "Murphy", "dog", giladId),
                PetData(hachikoId, companyId, "Hachiko", "dog", giladId),
                PetData(garfieldId, companyId, "Garfield", "cat", giladId)
            ),
            petsList
        )
    }

    @Test
    fun `add multiple pets`() {
        val petsDataList = listOf(
            PetData(id = 0, companyId = companyId, name = "Murphy", type = "dog", ownerId = null),
            PetData(id = 0, companyId = companyId, name = "Hachiko", type = "dog", ownerId = null),
            PetData(id = 0, companyId = companyId, name = "Garfield", type = "cat", ownerId = null)
        )
        petDao.addMultiplePets(petsDataList)

        val petsList = petDao.petsByType(companyId, PetType.DOG) + petDao.petsByType(companyId, PetType.CAT)

        assertEquals(3, petsList.size)

        val addedPets = petsDataList.associateBy { it.name }
        petsList.forEach { pet ->
            val expectedPet = addedPets[pet.name]
            assertEquals(expectedPet?.companyId, pet.companyId)
            assertEquals(expectedPet?.name, pet.name)
            assertEquals(expectedPet?.type, pet.type)
        }
    }
}