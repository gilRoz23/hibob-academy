package com.hibob.academy.feedbacks_system

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import org.jooq.impl.DSL
import java.time.LocalDate


@Component
class FeedbackDao(private val sql: DSLContext) {
    private val feedbackTable = FeedbackTable.instance

    private val feedbackMapper = RecordMapper<org.jooq.Record, FeedbackData> { record ->
        FeedbackData(
            id = record[feedbackTable.id],
            companyId = record[feedbackTable.companyId],
            content = record[feedbackTable.content],
            isAnonymous = record[feedbackTable.isAnonymous],
            status = record[feedbackTable.status],
            feedbackProviderId = record[feedbackTable.feedbackProviderId],
            department = Department.valueOf(record[feedbackTable.department].uppercase()),
            timeOfSubmitting = record[feedbackTable.timeOfSubmitting]
        )
    }


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

    fun getAllCompanyFeedbacks(companyId: Long): List<FeedbackData> {
        return sql.selectFrom(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .fetch(feedbackMapper)
    }


    fun filterFeedbacks(filter: FeedbackFilter): List<FeedbackData> {
        val conditions = listOfNotNull(
            filter.companyId?.let { feedbackTable.companyId.eq(it) },
            filter.isAnonymous?.let { feedbackTable.isAnonymous.eq(it) },
            filter.status?.let { feedbackTable.status.eq(it) },
            filter.feedbackProviderId?.let { feedbackTable.feedbackProviderId.eq(it) },
            filter.department?.let { feedbackTable.department.eq(it.name) },
            filter.timeOfSubmitting?.let { time ->
                val dateToCompare = time.toLocalDate()
                val dateField = DSL.field("DATE({0})", LocalDate::class.java, feedbackTable.timeOfSubmitting)
                dateField.ge(dateToCompare)
            }
        )

        // Building the query
        val query = sql.selectFrom(feedbackTable)

        // Apply conditions only if there are any
        if (conditions.isNotEmpty()) {
            query.where(conditions.reduce(Condition::and))
        }

        // Add the order by clause regardless of filters
        query.orderBy(feedbackTable.timeOfSubmitting.desc())

        // Fetch and return the result
        return query.fetch(feedbackMapper)
    }

}



    data class FeedbackFilter(
        val companyId: Long? = null,
        val isAnonymous: Boolean? = null,
        val status: Boolean? = null,
        val feedbackProviderId: Long? = null,
        val department: Department? = null,
        val timeOfSubmitting: LocalDateTime? = null
    )
}