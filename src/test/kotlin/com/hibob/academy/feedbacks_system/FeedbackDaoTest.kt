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
    fun `get all feedback by companyId successfully`() {
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I'm Mr. Meeseeks!",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT
        )
        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "OOOWeeee!",
            isAnonymous = true,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR
        )

        val differentCompanyId = Random.nextLong()
        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = differentCompanyId,
            content = "Rick Sanchez, I'm in",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.SALES
        )

        insertedFeedbackIds = insertedFeedbackIds + listOf(feedbackId1, feedbackId2, feedbackId3)

        val feedbacks = feedbackDao.getAllCompanyFeedbacks(companyId)

        // Assertions
        assertEquals(2, feedbacks.size)
        assertTrue(feedbacks.all { it.companyId == companyId })

        val feedbackContents = feedbacks.map { it.content }
        assertTrue(feedbackContents.contains("I'm Mr. Meeseeks!"))
        assertTrue(feedbackContents.contains("OOOWeeee!"))
    }
    @Test
    fun `get all feedback by companyId when no feedback exists`() {
        val feedbacks = feedbackDao.getAllCompanyFeedbacks(Random.nextLong())

        assertTrue(feedbacks.isEmpty())
    }

}
