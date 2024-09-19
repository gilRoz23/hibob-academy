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
class OwnerDaoTest @Autowired constructor(private val sql: DSLContext)  {

    private val dao = OwnerDao(sql)
    val companyId = Random.nextLong()

    @BeforeEach
    @AfterEach
    fun cleanup() {
        val allOwners = dao.getAllRecords()
        allOwners.forEach { owner -> dao.deleteOwnerById(owner.id) }
    }

    @Test
    fun `inserting owner test`() {
        dao.insertOwner("Gilad", companyId, "1")
        val ownersList = dao.getAllOwners(companyId)
        assertEquals(1, ownersList.size)
        assertEquals("Gilad", ownersList.get(0).name)
        assertEquals(companyId, ownersList.get(0).companyId)
        assertEquals("1", ownersList.get(0).employeeId)
    }

    @Test
    fun `insert an owner with the same companyId and employeeId`() {
        dao.insertOwner("Gilad", companyId, "1")
        dao.insertOwner("Bob", companyId, "1")
        val ownersList = dao.getAllOwners(companyId)
        assertEquals(1, ownersList.size)
        assertEquals("Gilad", ownersList.get(0).name)
        assertEquals("1", ownersList.get(0).employeeId)
    }
}