package com.hibob.academy.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component
import org.jooq.Record
import org.jooq.RecordMapper

@Component
class VaccineToPetDao(private val sql: DSLContext) {

    private val table = VaccineToPetTable.instance
}
