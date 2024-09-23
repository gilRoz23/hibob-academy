package com.hibob.academy.feedbacks_system

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.jooq.Record
import org.jooq.RecordMapper

@Component
class PetDao(private val sql: DSLContext) {

    private val employeeTable = EmployeeTable.instance

    private val employeeMapper = RecordMapper<Record, EmployeeData> {
            record ->
        EmployeeData(record[employeeTable.id],
            record[employeeTable.firstName],
            record[employeeTable.lastName],
            record[employeeTable.role],
        record[employeeTable.companyId])
    }

    fun getEmployeeId(firstname: String, lastname: String, companyId: Long) : EmployeeData {
        return sql.select(employeeTable.firstName, employeeTable.lastName, employeeTable.companyId)
            .from(employeeTable)
            .where(employeeTable.firstName.eq(firstname), employeeTable.lastName.eq(lastname), employeeTable.companyId.eq(companyId))
            .fetchOne()!![employeeTable.id]
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
}