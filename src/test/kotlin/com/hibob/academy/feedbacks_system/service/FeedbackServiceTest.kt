package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.Department
import com.hibob.academy.feedbacks_system.FeedbackDao
import com.hibob.academy.feedbacks_system.FeedbackData
import org.jooq.impl.DSL.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.random.Random

class FeedbackServiceTest {
    private var feedbackDao: FeedbackDao = mock(FeedbackDao::class.java)
    private var feedbackService: FeedbackService = FeedbackService(feedbackDao)

    @Test
    fun `should throw exception for feedback that is too short`() {
        val shortFeedback = "Too short feedback"
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, shortFeedback, isAnonymous, feedbackProviderId, department)
        }
    }

    @Test
    fun `should throw exception for feedback containing inappropriate language`() {
        val inappropriateFeedback = "I truly hate this product because it fails to deliver on its promises and has many disappointing flaws."
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, inappropriateFeedback, isAnonymous, feedbackProviderId, department)
        }
    }

    @Test
    fun `should insert feedback successfully`() {
        val validFeedback = "This is an exceptional product that I truly enjoy using every day. It meets all my needs and exceeds my expectations!"
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        doReturn(3L).whenever(feedbackDao).insertFeedback(companyId, validFeedback, isAnonymous, feedbackProviderId, department)

        assertDoesNotThrow {
            feedbackService.insertFeedback(companyId, validFeedback, isAnonymous, feedbackProviderId, department)
        }

        verify(feedbackDao).insertFeedback(companyId, validFeedback, isAnonymous, feedbackProviderId, department)
    }

    @Test
    fun `get all company feedbacks successfully`() {
        val companyId = 1L
        val feedbackId1 = Random.nextLong()
        val feedbackId2 = Random.nextLong()
        val feedbackList = listOf(
            FeedbackData(
                id = feedbackId1,
                companyId = companyId,
                content = "I'm Mr. Meeseeks!",
                isAnonymous = false,
                status = true,
                feedbackProviderId = Random.nextLong(),
                department = Department.IT,
                timeOfSubmitting = LocalDateTime.now()
            ),
            FeedbackData(
                id = feedbackId2,
                companyId = companyId,
                content = "OOOWeeee!",
                isAnonymous = true,
                status = false,
                feedbackProviderId = null,
                department = Department.HR,
                timeOfSubmitting = LocalDateTime.now()
            )
        )

        doReturn(feedbackList).whenever(feedbackDao).getAllCompanyFeedbacks(companyId)

        val feedbacks = feedbackService.getAllCompanyFeedbacks(companyId)

        assertEquals(2, feedbacks.size)
        assertTrue(feedbacks.all { it.companyId == companyId })
        assertTrue(feedbacks.any { it.content == "I'm Mr. Meeseeks!" })
        assertTrue(feedbacks.any { it.content == "OOOWeeee!" })

        verify(feedbackDao).getAllCompanyFeedbacks(companyId)
    }

    @Test
    fun `get all company feedbacks returns empty list when no feedback exists`() {
        val companyId = 1L
        doReturn(emptyList<FeedbackData>()).whenever(feedbackDao).getAllCompanyFeedbacks(companyId)

        val feedbacks = feedbackService.getAllCompanyFeedbacks(companyId)

        assertEquals(0, feedbacks.size)

        verify(feedbackDao).getAllCompanyFeedbacks(companyId)
    }

}