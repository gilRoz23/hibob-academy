package com.hibob.academy.feedbacks_system.service

import org.springframework.stereotype.Component
import com.hibob.academy.feedbacks_system.Department
import com.hibob.academy.feedbacks_system.FeedbackDao
import com.hibob.academy.feedbacks_system.FeedbackData
import com.hibob.academy.feedbacks_system.FeedbackFilter
import java.time.LocalDateTime

@Component
class FeedbackService(private val feedbackDao: FeedbackDao) {
    private val inappropriateWords = listOf("hate", "stupid", "idiot",
        "moron", "crap", "suck", "dumb", "loser",
        "vile", "disgusting", "scum", "hate speech", "abusive",
        "fool", "jerk", "douche", "shut up", "twit",
        "bastard", "faggot", "cunt", "prick", "wanker",
        "slut", "whore", "retard", "piss", "bullshit",
        "nigga", "trash", "scumbag", "foolish", "pendejo",
        "klutz", "freak", "loser", "chump", "simpleton",
        "asshole", "snot", "dumbass", "dickhead", "knob",
        "putz", "waste", "moronic", "lame", "cringe", "fuck")

    fun insertFeedback(companyId: Long, content: String, isAnonymous: Boolean, feedbackProviderId: Long?, department: Department) {
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
        if(inappropriateWords.any { content.lowercase().contains(it.lowercase()) }) {
            throw IllegalArgumentException("feedback contains inappropriate language.")
        }
    }

    fun getAllCompanyFeedbacks(companyId: Long): List<FeedbackData> {
        return feedbackDao.getAllCompanyFeedbacks(companyId)
    }

    fun filterFeedbacks(companyId: Long, isAnonymous: Boolean?, status: Boolean?, feedbackProviderId: Long?, department: Department?, timeOffSubmitting: LocalDateTime?): List<FeedbackData> {
        val filter = FeedbackFilter(companyId, isAnonymous, status, feedbackProviderId, department, timeOffSubmitting)
        return feedbackDao.filterFeedbacks(filter)
    }
}