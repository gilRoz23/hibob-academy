package com.hibob.academy.feedbacks_system

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.random.Random

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    private val companyId = Random.nextLong()
    private var insertedFeedbackIds = listOf<Long>()

    @AfterEach
    fun cleanup() {
        insertedFeedbackIds.forEach { feedbackId ->
            feedbackDao.deleteFeedback(feedbackId)
        }
        insertedFeedbackIds = listOf()
    }

    @Test
    fun `insert feedback successfully`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Great work on the last project!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

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
    }

    @Test
    fun `insert anonymous feedback`() {
        val feedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Someone doesn't flush the toilet consistently!",
            isAnonymous = true,
            feedbackProviderId = null,
            department = Department.HR
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId

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
    }

    @Test
    fun `get feedback by employeeId successfully`() {
        val employeeId = Random.nextLong()
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I'm Mr. Meeseeks!",
            isAnonymous = false,
            feedbackProviderId = employeeId,
            department = Department.IT
        )
        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "OOOWeeee!",
            isAnonymous = false,
            feedbackProviderId = employeeId,
            department = Department.IT
        )
        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Rick Sanchez, I'm in",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT
        )

        insertedFeedbackIds = insertedFeedbackIds + listOf(feedbackId1, feedbackId2, feedbackId3)

        val feedbacks = feedbackDao.getFeedbackByEmployeeId(employeeId)

        assertEquals(2, feedbacks.size)
        assertTrue(feedbacks.all { it.feedbackProviderId == employeeId })

        val feedbackContents = feedbacks.map { it.content }
        assertTrue(feedbackContents.contains("I'm Mr. Meeseeks!"))
        assertTrue(feedbackContents.contains("OOOWeeee!"))
    }

    @Test
    fun `get feedback by employeeId when no feedback exists`() {
        val feedbackProviderId = Random.nextLong()

        val feedbacks = feedbackDao.getFeedbackByEmployeeId(feedbackProviderId)

        assertTrue(feedbacks.isEmpty())
    }
}
