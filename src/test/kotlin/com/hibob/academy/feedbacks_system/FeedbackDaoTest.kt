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
            content = "Could this BE any more anonymous?",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Look at me, I'm not anonymous!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val anotherAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "I’ll never tell who I am.",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + anotherAnonymousFeedbackId

        val filterAnonymous = FeedbackFilter(companyId = companyId, isAnonymous = true)
        val filteredAnonymous = feedbackDao.filterFeedbacks(filterAnonymous)

        assertEquals(1, filteredAnonymous.size)
        assertTrue(filteredAnonymous.all { it.isAnonymous })
    }

    @Test
    fun `filter feedbacks by isAnonymous false`() {
        val feedbackProviderId = Random.nextLong()
        val anonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This is a secret! Shhh!",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val nonAnonymousFeedbackIdCompany1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I want everyone to know my opinion!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val anotherNonAnonymousFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "This feedback is not a secret!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + anonymousFeedbackIdCompany1 + nonAnonymousFeedbackIdCompany1 + anotherNonAnonymousFeedbackId

        val filterNonAnonymous = FeedbackFilter(companyId = companyId, isAnonymous = false)
        val filteredNonAnonymous = feedbackDao.filterFeedbacks(filterNonAnonymous)

        assertEquals(1, filteredNonAnonymous.size)
        assertTrue(filteredNonAnonymous.all { !it.isAnonymous })
    }

    @Test
    fun `filter feedbacks by feedbackProviderId`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "How you doin'? This feedback is from me.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This one’s not from me; just a regular feedback.",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR
        )

        val anotherCompanySameId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Another great feedback from yours truly!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + anotherFeedbackId + anotherCompanySameId

        val filter = FeedbackFilter(companyId = companyId, feedbackProviderId = feedbackProviderId)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.feedbackProviderId == feedbackProviderId })
    }

    @Test
    fun `filter feedbacks by department`() {
        val feedbackProviderId = Random.nextLong()
        val feedbackIdHR = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "HR is the best department. Period.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val anotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "IT feedback is also important!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        val yetAnotherFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "More HR feedback, because why not?",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )
        insertedFeedbackIds = insertedFeedbackIds + feedbackIdHR + anotherFeedbackId + yetAnotherFeedbackId

        val filterHR = FeedbackFilter(companyId = companyId, department = Department.HR)
        val filteredHRFeedbacks = feedbackDao.filterFeedbacks(filterHR)

        assertEquals(1, filteredHRFeedbacks.size)
        assertTrue(filteredHRFeedbacks.all { it.department == Department.HR })
    }

    @Test
    fun `filter feedbacks by date`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Smelly Cat feedback for today!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "More Smelly Cat feedback for today!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = companyId + 1,
            content = "Smelly Cat feedback from three days ago.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES,
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + feedbackId2 + feedbackId3

        val filterByDate = FeedbackFilter(companyId = companyId, timeOfSubmitting = LocalDateTime.now())
        val filteredDateFeedbacks = feedbackDao.filterFeedbacks(filterByDate)

        assertEquals(2, filteredDateFeedbacks.size)
    }

    @Test
    fun `filter feedbacks by isAnonymous true and feedbackProviderId`() {
        val feedbackProviderId = Random.nextLong()
        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I'm not telling who I am, but I love feedback!",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This one's from me, and I'm proud!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Anonymous feedback from another provider!",
            isAnonymous = true,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT
        )

        val filter = FeedbackFilter(companyId = companyId, isAnonymous = true, feedbackProviderId = feedbackProviderId)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.isAnonymous && it.feedbackProviderId == feedbackProviderId })
    }

    @Test
    fun `filter feedbacks by department and isAnonymous false`() {
        val feedbackProviderId = Random.nextLong()
        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "HR rules!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "IT is awesome!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "HR is my favorite!",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR
        )

        val filter = FeedbackFilter(companyId = companyId, isAnonymous = false, department = Department.HR)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(2, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { !it.isAnonymous && it.department == Department.HR })
    }

    @Test
    fun `filter feedbacks by feedbackProviderId and department`() {
        val feedbackProviderId = Random.nextLong()
        val hrFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I love HR!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val itFeedbackId = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "IT is crucial!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.IT
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Different provider, different department.",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.SALES
        )

        val filter = FeedbackFilter(companyId = companyId, feedbackProviderId = feedbackProviderId, department = Department.HR)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.feedbackProviderId == feedbackProviderId && it.department == Department.HR })
    }

    @Test
    fun `filter feedbacks by isAnonymous true, feedbackProviderId and department`() {
        val feedbackProviderId = Random.nextLong()
        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Anonymous HR feedback? Yes, please!",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Not anonymous, still HR.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Anonymous IT feedback.",
            isAnonymous = true,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT
        )

        val filter = FeedbackFilter(companyId = companyId, isAnonymous = true, feedbackProviderId = feedbackProviderId, department = Department.HR)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(1, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.isAnonymous && it.feedbackProviderId == feedbackProviderId && it.department == Department.HR })
    }

    @Test
    fun `filter feedbacks by date, feedbackProviderId and isAnonymous false`() {
        val feedbackProviderId = Random.nextLong()

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I submitted this feedback yesterday.",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I submitted this feedback today!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR,
        )

        feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This is not from me!",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.HR,
        )

        val filter = FeedbackFilter(companyId = companyId, feedbackProviderId = feedbackProviderId, isAnonymous = false, timeOfSubmitting = LocalDateTime.now())
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filter)

        assertEquals(2, filteredFeedbacks.size)
        assertTrue(filteredFeedbacks.all { it.feedbackProviderId == feedbackProviderId && !it.isAnonymous })
    }

    @Test
    fun `filter feedbacks without any filters`() {
        val feedbackProviderId = Random.nextLong()

        val feedbackId1 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "This coffee is like the best part of my day!",
            isAnonymous = false,
            feedbackProviderId = feedbackProviderId,
            department = Department.HR
        )

        val feedbackId2 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "Could I *be* any more helpful?",
            isAnonymous = true,
            feedbackProviderId = feedbackProviderId,
            department = Department.SALES
        )

        val feedbackId3 = feedbackDao.insertFeedback(
            companyId = companyId,
            content = "I just want to be there for my friends, and maybe help them out at work too!",
            isAnonymous = false,
            feedbackProviderId = Random.nextLong(),
            department = Department.IT
        )

        insertedFeedbackIds = insertedFeedbackIds + feedbackId1 + feedbackId2 + feedbackId3

        val filterAll = FeedbackFilter(companyId = companyId)
        val filteredFeedbacks = feedbackDao.filterFeedbacks(filterAll)

        assertEquals(3, filteredFeedbacks.size, "Just like Ross's marriages, there should be no filters on this feedback!")

        val expectedContents = setOf(
            "This coffee is like the best part of my day!",
            "Could I *be* any more helpful?",
            "I just want to be there for my friends, and maybe help them out at work too!"
        )
        val actualContents = filteredFeedbacks.map { it.content }.toSet()

        assertTrue(actualContents.containsAll(expectedContents))
    }
}
