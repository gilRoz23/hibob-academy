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

    fun getEmployeeId(firstname: String, lastname: String, companyId: Long) : EmployeeData? {
        return sql.select(employeeTable.firstName, employeeTable.lastName, employeeTable.companyId)
            .from(employeeTable)
            .where(employeeTable.firstName.eq(firstname), employeeTable.lastName.eq(lastname), employeeTable.companyId.eq(companyId))
            .fetchOne(employeeMapper)
    }

    fun deleteEmployeeById(id: Long): Int {
        return sql.deleteFrom(employeeTable)
            .where(employeeTable.id.eq(id))
            .execute()
    }

    fun insertEmployee(firstName: String, lastName: String, role: String, companyId: Long): Long {
        return sql.insertInto(employeeTable)
            .columns(employeeTable.firstName, employeeTable.lastName, employeeTable.role, employeeTable.companyId)
            .values(firstName, lastName, role, companyId)
            .returning(employeeTable.id)
            .fetchOne()!![employeeTable.id]
    }
}