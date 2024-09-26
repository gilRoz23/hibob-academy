package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ResponseService(private val responseDao: ResponseDao) {

        fun insertResponse(
            companyId: Long,
            feedbackId: Long,
            content: String,
            responserId: Long?,
            timeOfResponding: LocalDateTime = LocalDateTime.now()
        ): Long {
            return responseDao.insertResponse(companyId, feedbackId, content, responserId, timeOfResponding)
        }

        fun getAllCompanyResponses(companyId: Long): List<ResponseData> {
            return responseDao.getAllCompanyResponses(companyId)
        }

        fun getResponseByFeedbackId(feedbackId: Long): List<ResponseData> {
            return responseDao.getResponseByFeedbackId(feedbackId)
        }
    }