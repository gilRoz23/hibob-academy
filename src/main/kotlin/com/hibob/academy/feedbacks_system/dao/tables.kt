package com.hibob.academy.feedbacks_system.dao

import com.hibob.academy.utils.JooqTable
import com.sun.java.swing.ui.CommonUI.createTextField

class FeedbackTable(tableName : String = "feedback") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val companyId = createBigIntField("company_id")
    val content = createTextField("content")
    val isAnonymous = createBooleanField("is_anonymous")
    val status = createBooleanField("status")
    val feedbackProviderId = createBigIntField("feedback_provider_id")
    val department = createVarcharField("department")
    val timeOfSubmitting = createLocalDateTimeField("time_of_submitting")


    companion object{
        val instance = FeedbackTable()
    }
}