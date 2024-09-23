package com.hibob.academy.feedbacks_system.dao

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
    LEGAL
}


data class FeedbackData(
    val id: Long,
    val company_id: Long,
    val content: String,
    val isAnonymous: Boolean,
    val status: Boolean,
    val feedbackProviderId: Long?,
    val department: Department?,
    val timeOfSubmitting: LocalDateTime
)