package com.hibob.academy.feedbacks_system

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.random.Random

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    val companyId = Random.nextLong()
    private val insertedFeedbackIds = mutableListOf<Long>()


    private fun deleteInsertedFeedbacks() {
        insertedFeedbackIds.forEach { feedbackId ->
            feedbackDao.deleteFeedback(feedbackId)
        }
        insertedFeedbackIds.clear()
    }

    @Test
    fun `insert feedback successfully`() {
        try {
            val feedbackProviderId = Random.nextLong()
            val feedbackId = feedbackDao.insertFeedback(
                companyId = companyId,
                content = "Great work on the last project!",
                isAnonymous = false,
                feedbackProviderId = feedbackProviderId,
                department = Department.HR
            )
            insertedFeedbackIds.add(feedbackId) // Track inserted feedback ID

            val insertedFeedback = feedbackDao.getFeedbackById(feedbackId)

            assertNotNull(insertedFeedback)
            assertEquals(companyId, insertedFeedback?.companyId)
            assertEquals("Great work on the last project!", insertedFeedback?.content)
            assertEquals(false, insertedFeedback?.isAnonymous)
            assertEquals(feedbackProviderId, insertedFeedback?.feedbackProviderId)
            assertEquals(Department.HR, insertedFeedback?.department)
            assertEquals(false, insertedFeedback?.status) // not reviewed yet
            assertTrue(insertedFeedback?.timeOfSubmitting?.isBefore(LocalDateTime.now()) == true)
            assertEquals(feedbackId, insertedFeedback?.id)
        } finally {
            deleteInsertedFeedbacks() // Ensure records are deleted after the test
        }
    }

    @Test
    fun `insert anonymous feedback`() {
        try {
            val feedbackId = feedbackDao.insertFeedback(
                companyId = companyId,
                content = "Someone doesn't flush the toilet consistently!",
                isAnonymous = true,
                feedbackProviderId = null,
                department = Department.HR
            )
            insertedFeedbackIds.add(feedbackId)

            val insertedFeedback = feedbackDao.getFeedbackById(feedbackId)

            assertNotNull(insertedFeedback)
            assertEquals(companyId, insertedFeedback?.companyId)
            assertEquals("Someone doesn't flush the toilet consistently!", insertedFeedback?.content)
            assertEquals(true, insertedFeedback?.isAnonymous)
            assertEquals(null, insertedFeedback?.feedbackProviderId)
            assertEquals(Department.HR, insertedFeedback?.department)
            assertEquals(false, insertedFeedback?.status) // not reviewed yet
            assertTrue(insertedFeedback?.timeOfSubmitting?.isBefore(LocalDateTime.now()) == true)
            assertEquals(feedbackId, insertedFeedback?.id)
        } finally {
            deleteInsertedFeedbacks()
        }
    }
}
