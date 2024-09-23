package com.hibob.academy.feedbacks_system


import com.hibob.academy.feedbacks_system.service.FeedbackService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever


class FeedbackServiceTest {

    private lateinit var feedbackDao: FeedbackDao
    private lateinit var feedbackService: FeedbackService

    @BeforeEach
    fun setUp() {
        feedbackDao = mock(FeedbackDao::class.java)
        feedbackService = FeedbackService(feedbackDao)
    }

    @Test
    fun `should throw exception for feedback that is too short`() {
        val shortFeedback = "Too short feedback"
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        val exception = assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, shortFeedback, isAnonymous, feedbackProviderId, department)
        }

        assertEquals("feedback is too short.", exception.message)
    }

    @Test
    fun `should throw exception for feedback containing inappropriate language`() {
        val inappropriateFeedback = "I truly hate this product because it fails to deliver on its promises and has many disappointing flaws."
        val companyId = 1L
        val isAnonymous = false
        val feedbackProviderId: Long? = null
        val department = Department.HR

        val exception = assertThrows<IllegalArgumentException> {
            feedbackService.insertFeedback(companyId, inappropriateFeedback, isAnonymous, feedbackProviderId, department)
        }

        assertEquals("feedback contains inappropriate language.", exception.message)
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