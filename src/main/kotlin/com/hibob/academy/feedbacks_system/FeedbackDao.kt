package com.hibob.academy.feedbacks_system

import com.hibob.academy.dao.PetData
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*


@Component
class FeedbackDao(private val sql: DSLContext) {

    private val feedbackTable = FeedbackTable.instance

    private val feedbackMapper = RecordMapper<org.jooq.Record, FeedbackData> {
            record ->
        FeedbackData(record[feedbackTable.id],
            record[feedbackTable.companyId],
            record[feedbackTable.content],
            record[feedbackTable.isAnonymous],
            record[feedbackTable.status],
            record[feedbackTable.feedbackProviderId],
            department = Department.valueOf(record[feedbackTable.department].uppercase(Locale.getDefault())),
            record[feedbackTable.timeOfSubmitting]
            )
    }
    //Converting String to Department enum


    fun insertFeedback(companyId: Long, content: String, isAnonymous: Boolean, feedbackProviderId: Long?, department: Department): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, companyId)
            .set(feedbackTable.content, content)
            .set(feedbackTable.isAnonymous, isAnonymous)
            .set(feedbackTable.status, false)
            .set(feedbackTable.feedbackProviderId, feedbackProviderId)
            .set(feedbackTable.department, department.name)
            .set(feedbackTable.timeOfSubmitting, LocalDateTime.now())
            .returning(feedbackTable.id)
            .fetchOne()!![feedbackTable.id]
    }

    fun deleteFeedback(feedbackId: Long): Int {
        return sql.deleteFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId))
            .execute()
    }

    fun getFeedbackById(feedbackId: Long): FeedbackData? {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackId))
            .fetchOne(feedbackMapper)
    }
}