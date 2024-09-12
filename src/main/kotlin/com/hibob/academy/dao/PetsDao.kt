package com.hibob.academy.dao

import com.hibob.academy.utils.JooqTable
import com.hibob.academy.utils.asIs
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import com.hibob.academy.utils.from
import org.jooq.Record
import org.jooq.RecordMapper
import java.time.LocalDate

enum class PetType(val type: String) {
    DOG("dog"),
    CAT("cat"),
    BIRD("bird"),
    FISH("fish");
}

@Component
class PetDao(private val sql: DSLContext) {

    private val table = PetTable.instance

    private val petMapper = RecordMapper<Record, PetData> {
            record ->
        PetData(record[table.id],
            record[table.companyId],
            record[table.name],
            record[table.type],
            record[table.ownerId])
    }

    fun petsByType(companyId: Long, type: PetType) : List<PetData> {
        return sql.select(table.id, table.companyId, table.name, table.type, table.ownerId)
            .from(table)
            .where(table.companyId.eq(companyId))
            .and(table.type.eq(type.type))
            .fetch(petMapper)
    }

    fun insertPet(pet: PetData) {
        sql.insertInto(table)
            .set(table.id, pet.id)
            .set(table.companyId, pet.companyId)
            .set(table.name, pet.name)
            .set(table.type, pet.type)
            .set(table.ownerId, pet.ownerId)
            .execute()
    }

    fun updatePet(petId: Int, ownerId: Long) {

    }
}
