package com.hibob.academy.feedbacks_system.service

import org.junit.jupiter.api.Assertions.*
import com.hibob.academy.feedbacks_system.ResponseDao
import com.hibob.academy.feedbacks_system.ResponseData
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.doReturn
import java.time.LocalDateTime
import kotlin.random.Random
import org.mockito.kotlin.whenever

class ResponseServiceTest {

    private var responseDao: ResponseDao = mock(ResponseDao::class.java)
    private var responseService: ResponseService = ResponseService(responseDao)

    @Test
    fun `should insert response successfully`() {
        val companyId = 1L
        val feedbackId = Random.nextLong()
        val content = "This is a response to feedback."
        val responserId: Long? = null
        val timeOfResponding = LocalDateTime.now()

        doReturn(3L).whenever(responseDao).insertResponse(companyId, feedbackId, content, responserId, timeOfResponding)

        val responseId = responseService.insertResponse(companyId, feedbackId, content, responserId, timeOfResponding)

        assertEquals(3L, responseId)
        verify(responseDao).insertResponse(companyId, feedbackId, content, responserId, timeOfResponding)
    }

    @Test
    fun `get all company responses successfully`() {
        val companyId = 1L
        val responseId1 = Random.nextLong()
        val responseId2 = Random.nextLong()
        val response1 = ResponseData(
            id = responseId1,
            companyId = 1L,
            feedbackId = 1L,
            content = "First response",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response2 = ResponseData(
            id = responseId2,
            companyId = 1L,
            feedbackId = 1L,
            content = "Second response",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response3 = ResponseData(
            id = responseId2+1,
            companyId = 2L,
            feedbackId = 1L,
            content = "Response to feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val responseList = listOf(response1, response2, response3)

        doReturn(listOf(response1, response2)).whenever(responseDao).getAllCompanyResponses(1L)

        val responses = responseService.getAllCompanyResponses(companyId)

        assertEquals(2, responses.size)
        assertTrue(responses.any { it.content == "First response" })
        assertTrue(responses.any { it.content == "Second response" })

        verify(responseDao).getAllCompanyResponses(companyId)
    }

    @Test
    fun `get all company responses returns empty list when no responses exist`() {
        val companyId = 1L
        doReturn(emptyList<ResponseData>()).whenever(responseDao).getAllCompanyResponses(companyId)

        val responses = responseService.getAllCompanyResponses(companyId)

        assertEquals(0, responses.size)

        verify(responseDao).getAllCompanyResponses(companyId)
    }

    @Test
    fun `get responses by feedback ID successfully`() {
        val feedbackId = Random.nextLong()
        val responseId1 = Random.nextLong()
        val responseId2 = Random.nextLong()
        val response1 = ResponseData(
            id = responseId1,
            companyId = 1L,
            feedbackId = feedbackId,
            content = "Response to feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response2 = ResponseData(
            id = responseId2,
            companyId = 1L,
            feedbackId = feedbackId,
            content = "Another response to feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val response3 = ResponseData(
            id = responseId2+1,
            companyId = 1L,
            feedbackId = feedbackId+1,
            content = "Response to feedback",
            responserId = null,
            timeOfResponding = LocalDateTime.now()
        )
        val responseList = listOf(response1, response2, response3)

        doReturn(listOf(response1, response2)).whenever(responseDao).getResponseByFeedbackId(feedbackId)

        val responses = responseService.getResponseByFeedbackId(feedbackId)

        assertEquals(2, responses.size)
        assertTrue(responses.any { it.content == "Response to feedback" })
        assertTrue(responses.any { it.content == "Another response to feedback" })

        verify(responseDao).getResponseByFeedbackId(feedbackId)
    }

    @Test
    fun `get responses by feedback ID returns empty list when no responses exist`() {
        val feedbackId = Random.nextLong()
        doReturn(emptyList<ResponseData>()).whenever(responseDao).getResponseByFeedbackId(feedbackId)

        val responses = responseService.getResponseByFeedbackId(feedbackId)

        assertEquals(0, responses.size)

        verify(responseDao).getResponseByFeedbackId(feedbackId)
    }
}