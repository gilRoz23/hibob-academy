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

    @Test
    fun `filter feedbacks by isAnonymous true`() {
        val feedbackProviderId = Random.nextLong()
        val anonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Could this feedback BE any more anonymous?",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I’m not saying it’s the best coffee, but it’s definitely better than Central Perk’s.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val anotherAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "This feedback is as forgettable as Ross’s second marriage.",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + anotherAnonymousFeedbackId

        val anotherNonAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 2,
            content = "Could I *be* any more unhelpful?",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.FINANCE
        )
        insertedFeedbackIds = insertedFeedbackIds + anonymousFeedbackIdCompany1 + nonAnonymousFeedbackIdCompany1 + anotherNonAnonymousFeedbackId

        val filterAnonymous = FeedbackDao.FeedbackFilter(companyId = companyId, isAnonymous = true)
        val filteredAnonymous = feedbackDao.filterFeedbacks(filterAnonymous)

        assertEquals(1, filteredAnonymous.size)
        assertTrue(filteredAnonymous.all { it.isAnonymous })
    }

    @Test
    fun `filter feedbacks by isAnonymous false`() {
        val feedbackProviderId = Random.nextLong()
        val anonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Could this feedback *be* any more anonymous?",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I told you about this in confidence, but now I'm just sharing it with the whole company.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val anotherNonAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "This feedback belongs in the archives of Ross's failed relationships.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + anonymousFeedbackIdCompany1 + nonAnonymousFeedbackIdCompany1 + anotherNonAnonymousFeedbackId

        val filterNonAnonymous = FeedbackDao.FeedbackFilter(companyId = companyId, isAnonymous = false)
        val filteredNonAnonymous = feedbackDao.filterFeedbacks(filterNonAnonymous)

        assertEquals(1, filteredNonAnonymous.size)
        assertTrue(filteredNonAnonymous.all { !it.isAnonymous })
    }

    @Test
    fun `filter feedbacks by feedbackProviderId`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This coffee is as good as a date with Joey!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This feedback should not be returned, just like Ross’s last girlfriend.",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR
        )

        val anotherCompanySameId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "This feedback belongs in the archives of Ross's failed relationships.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + anotherFeedbackId + anotherCompanySameId

        val filter = FeedbackDao.FeedbackFilter(companyId = companyId, feedbackProviderId = feedbackProviderId)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertEquals(feedbackProviderId, filteredFeedbacks.first().feedbackProviderId)
    }

    @Test
    fun `filter feedbacks by department`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackIdHR = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Feedback for HR: I need a raise! Just kidding, please don't fire me!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Feedback for a different department: Is it just me or does Ross really need to let go?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val yetAnotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Feedback for sales: Could I sell any more... coffee?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackIdHR + anotherFeedbackId + yetAnotherFeedbackId

        val filterHR = FeedbackDao.FeedbackFilter(companyId = companyId, department = Department.HR)
        val filteredHRFeedbacks = feedbackDao.filterFeedbacks(filterHR)

        assertEquals(1, filteredHRFeedbacks.size)
        assertEquals(Department.HR, filteredHRFeedbacks.first().department)
    }

    @Test
    fun `filter feedbacks by department sales`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackIdHR = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Feedback for HR: I need a raise! Just kidding, please don't fire me!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Feedback for a different department: Is it just me or does Ross really need to let go?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val yetAnotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Feedback for sales: Could I sell any more... coffee?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackIdHR + anotherFeedbackId + yetAnotherFeedbackId

        val filterSales = FeedbackDao.FeedbackFilter(companyId = companyId + 1, department = Department.SALES)
        val filteredSalesFeedbacks = feedbackDao.filterFeedbacks(filterSales)

        assertEquals(1, filteredSalesFeedbacks.size)
        assertEquals(Department.SALES, filteredSalesFeedbacks.first().department)
    }


    @Test
    fun `filter feedbacks by date`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Let's rock this project!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "What a tough day!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Can't wait for the weekend!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + feedbackId2 + feedbackId3

        val filterByDate = FeedbackDao.FeedbackFilter(companyId = companyId, timeOfSubmitting = LocalDateTime.now())
        val filteredDateFeedbacks = feedbackDao.filterFeedbacks(filterByDate)

        assertEquals(2, filteredDateFeedbacks.size)
    }

}
