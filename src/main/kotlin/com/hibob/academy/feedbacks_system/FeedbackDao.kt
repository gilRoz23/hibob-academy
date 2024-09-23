package com.hibob.academy.feedbacks_system

import org.jooq.DSLContext
import org.springframework.stereotype.Component


@Component
class FeedbackDao(private val sql: DSLContext) {

    private val feedbackTable = FeedbackTable.instance

//    private val feedbackMapper = RecordMapper<Record, FeedbackData> {
//            record ->
//        FeedbackData(record[],
//            record[])
//    }

//    fun insertPet(companyId: Long, name: String, type: String) {
//        sql.insertInto(petTable)
//            .set(petTable.companyId, companyId)
//            .set(petTable.name, name)
//            .set(petTable.type, type)
//            .execute()
//    }

    //MAKE SURE COMPANYID COMES FROM THE COOKIE
    fun insertFeedback(companyId: Long, isAnonymous: Boolean, content: String, feedbackProviderId: Long, department: Department) {
        sql.insertInto(feedbackTable)
            .set(feedbackTable.companyId, companyId)
            .set(feedbackTable.content, content)
    }
}