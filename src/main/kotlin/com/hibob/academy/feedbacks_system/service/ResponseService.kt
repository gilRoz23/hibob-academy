package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import org.springframework.stereotype.Component

@Component
class ResponseService(private val responseDao: ResponseDao, private val feedbackDao: FeedbackDao) {

        fun insertResponse(companyId: Long, responserId: Long?,  responseRequest: ResponseRequest){
            val feedback = feedbackDao.getFeedbackById(responseRequest.feedbackId)
            if(feedback == null || feedback.isAnonymous){
                throw IllegalArgumentException("Request denied")
            }
            responseDao.insertResponse(companyId, responseRequest.feedbackId, responseRequest.content, responserId)
        }

        fun getAllCompanyResponses(companyId: Long): List<ResponseData> {
            return responseDao.getAllCompanyResponses(companyId)
        }

        fun getResponseByFeedbackId(feedbackId: Long): List<ResponseData> {
            return responseDao.getResponseByFeedbackId(feedbackId)
        }
    }