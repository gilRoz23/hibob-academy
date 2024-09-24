package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.CompanyDao
import com.hibob.academy.feedbacks_system.EmployeeDao
import com.hibob.academy.feedbacks_system.resource.JWTDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Component
class SessionService(private val companyDao: CompanyDao, private val employeeDao: EmployeeDao) {
    // Generate a secure key for HS256
    companion object {
        val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    }


    fun createJwtToken(jwtDet: JWTDetails): String {
        val now = Date()
        val companyId = companyDao.getCompanyByName(jwtDet.companyName)?.id
        val employeeData = employeeDao.getEmployee(jwtDet.firstname, jwtDet.lastname, companyId)
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("firstname", jwtDet.firstname)
            .claim("lastname", jwtDet.lastname)
            .claim("companyId", "${jwtDet.companyId}")
            .claim("role", "${jwtDet.role}")
            .setIssuedAt(now)
            .setExpiration(Date(now.time + TimeUnit.HOURS.toMillis(24)))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }
}