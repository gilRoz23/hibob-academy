package com.hibob.academy.feedbacks_system.resource

enum class Role {
    HR,
    ADMIN,
    EMPLOYEE
}

data class JWTDetails(val firstname:String, val lastname: String, val companyId: Long, val role: Role)