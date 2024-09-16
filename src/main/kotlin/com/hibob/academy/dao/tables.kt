package com.hibob.academy.dao
import com.hibob.academy.utils.JooqTable

class PetTable(tableName : String = "pet") : JooqTable(tableName) {
    val id = createIntField("id")
    val companyId = createBigIntField("company_id")
    val name = createVarcharField("name")
    val type = createVarcharField("type")
    val dateOfArrival = createDateField("date_of_arrival")
    val ownerId = createBigIntField("owner_id")


    companion object{
        val instance = PetTable()
    }
}

class OwnerTable(tableName : String = "owner") : JooqTable(tableName) {

    val id = createBigIntField("id")
    val name = createVarcharField("name")
    val companyId = createBigIntField("company_id")
    val employeeId = createVarcharField("employee_id")

    companion object{
        val instance = OwnerTable()
    }
}

class VaccineTable(tableName : String = "vaccine") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")

    companion object{
        val instance = VaccineTable()
    }
}

class VaccineToPetTable(tableName : String = "vaccine_to_pet") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val petId = createIntField("pet_id")
    val vaccinationDate = createDateField("vaccination_date")

    companion object{
        val instance = VaccineToPetTable()
    }
}