package com.hibob.academy.feedbacks_system.dao
import java.time.LocalDate

data class FeedbackData(
    val id: Long,
    val company_id: Long,
    val fee
)

data class OwnerData(
    val id: Long,
    val name: String,
    val companyId: Long,
    val employeeId: String
)

data class VaccineData(
    val id: Long,
    val name: String
)

data class VaccineToPet
    (
            val id: Long,
            val petId: Int,
            val vaccintionDate: LocalDate
            )