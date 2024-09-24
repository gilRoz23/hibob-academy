package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.Department
import com.hibob.academy.feedbacks_system.FeedbackDao
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

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
}