package com.hibob.academy.feedbacks_system

import java.time.LocalDateTime

enum class Department {
    HR,
    IT,
    SALES,
    MARKETING,
    FINANCE,
    OPERATIONS,
    RESEARCH_AND_DEVELOPMENT,
    CUSTOMER_SERVICE,
    LEGAL,
    OTHER
}


data class FeedbackData(
    val id: Long,
    val companyId: Long,
    val content: String,
    val isAnonymous: Boolean,
    val status: Boolean,
    val feedbackProviderId: Long?,
    val department: Department,
    val timeOfSubmitting: LocalDateTime
)

data class CompanyData(
    val id: Long,
    val name: String
)