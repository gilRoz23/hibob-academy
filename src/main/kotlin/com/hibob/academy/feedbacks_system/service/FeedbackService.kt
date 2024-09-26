package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import org.springframework.stereotype.Component

@Component
class FeedbackService(private val feedbackDao: FeedbackDao) {
    private val inappropriateWords = listOf(
        "hate", "stupid", "idiot",
        "moron", "crap", "suck", "dumb", "loser",
        "vile", "disgusting", "scum", "hate speech", "abusive",
        "fool", "jerk", "douche", "shut up", "twit",
        "bastard", "faggot", "cunt", "prick", "wanker",
        "slut", "whore", "retard", "piss", "bullshit",
        "nigga", "trash", "scumbag", "foolish", "pendejo",
        "klutz", "freak", "loser", "chump", "simpleton",
        "asshole", "snot", "dumbass", "dickhead", "knob",
        "putz", "waste", "moronic", "lame", "cringe", "fuck"
    )

    fun insertFeedback(
        companyId: Long,
        content: String,
        isAnonymous: Boolean,
        feedbackProviderId: Long?,
        department: Department
    ) {
        validateLength(content)
        validateContent(content)
        feedbackDao.insertFeedback(companyId, content, isAnonymous, feedbackProviderId, department)
    }

    private fun validateLength(content: String) {
        if (content.length < 50) {
            throw IllegalArgumentException("feedback is too short.")
        }
    }

    private fun validateContent(content: String) {
        if (inappropriateWords.any { content.lowercase().contains(it.lowercase()) }) {
            throw IllegalArgumentException("feedback contains inappropriate language.")
        }
    }

    fun getAllCompanyFeedbacks(companyId: Long): List<FeedbackData> {
        return feedbackDao.getAllCompanyFeedbacks(companyId)
    }

    fun filterFeedbacks(companyId: Long, userFeedbackFilter: UserFeedbackFilter): List<FeedbackData> {
        val filter = FeedbackFilter(
            companyId,
            userFeedbackFilter.isAnonymous,
            userFeedbackFilter.status,
            userFeedbackFilter.feedbackProviderId,
            userFeedbackFilter.department,
            userFeedbackFilter.timeOfSubmitting
        )
        return feedbackDao.filterFeedbacks(filter)
    }

    fun switchFeedbackStatus(feedbackId: Long) {
        val updatedRows = feedbackDao.switchFeedbackStatus(feedbackId)
        if (updatedRows == 0)
            throw IllegalArgumentException("Feedback does not exist.")
    }

    fun getFeedbackStatus(feedbackId: Long): Boolean? {
        return feedbackDao.getFeedbackStatus(feedbackId)?:throw IllegalArgumentException("Feedback does not exist.")
    }
}