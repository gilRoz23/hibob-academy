package com.hibob.academy.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL

enum class PetType(val type: String) {
    DOG("dog"),
    CAT("cat"),
    BIRD("bird"),
    FISH("fish");
}

@Component
class PetDao(private val sql: DSLContext) {

    private val petTable = PetTable.instance
    private val ownerTable = OwnerTable.instance

    private val petMapper = RecordMapper<Record, PetData> {
            record ->
        PetData(record[petTable.id],
            record[petTable.companyId],
            record[petTable.name],
            record[petTable.type],
            record[petTable.ownerId])
    }

    private val typesMapper = RecordMapper<Record, Map<String, Int>> { record ->
        val petType = record[petTable.type]
        val petCount = record.get("count", Int::class.java)
        mapOf(petType to petCount)
    }


    fun petsByType(companyId: Long, type: PetType) : List<PetData> {
        return sql.select(petTable.id, petTable.companyId, petTable.name, petTable.type, petTable.ownerId)
            .from(petTable)
            .where(petTable.companyId.eq(companyId))
            .and(petTable.type.eq(type.type))
            .fetch(petMapper)
    }

    fun insertPet(companyId: Long, name: String, type: String) {
        sql.insertInto(petTable)
            .set(petTable.companyId, companyId)
            .set(petTable.name, name)
            .set(petTable.type, type)
            .execute()
    }

    fun adoptPet(petId: Int, ownerId: Long) {
        sql.update(petTable)
            .set(petTable.ownerId, ownerId)
            .where(petTable.id.eq(petId).and(petTable.ownerId.isNull)) // Only update if there's no owner
            .execute()
    }

    fun getOwnerByPetId(petId: Int): OwnerData? {
        val ownerDao = OwnerDao(sql)
        return sql.select(ownerTable.id, ownerTable.name, ownerTable.companyId, ownerTable.employeeId)
            .from(petTable)
            .join(ownerTable)
            .on(petTable.ownerId.eq(ownerTable.id))
            .where(petTable.id.eq(petId))
            .fetchOne(ownerDao.ownerMapper)
    }

//    ***
//    FROM HERE AND DOWN ADDING TO SQL2. DO NOT TOUCH UPWARD

    fun getPetsByOwnerId(ownerId: Long): List<PetData> {
        return sql.select(petTable.id, petTable.companyId, petTable.name, petTable.type, petTable.ownerId)
            .from(petTable)
            .where(petTable.ownerId.eq(ownerId))
            .fetch(petMapper)
    }

    fun countPetsByType(companyId: Long): List<Map<String, Int>> {
        val countField = DSL.count()
        return sql.select(petTable.type, countField.`as`("count"))
            .from(petTable)
            .where(petTable.companyId.eq(companyId))
            .groupBy(petTable.type)
            .orderBy(countField)
            .fetch(typesMapper)
    }

    fun deletePetById(petId: Int) {
        sql.deleteFrom(petTable)
            .where(petTable.id.eq(petId))
            .execute()
    }

    fun getAllRecords(): List<PetData> {
        return sql.select(petTable.id, petTable.companyId, petTable.name, petTable.type, petTable.ownerId)
            .from(petTable)
            .fetch(petMapper)
    }
}