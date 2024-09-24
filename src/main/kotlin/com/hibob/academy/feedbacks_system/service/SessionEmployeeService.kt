package com.hibob.academy.feedbacks_system.service

import com.hibob.academy.feedbacks_system.*
import com.hibob.academy.feedbacks_system.CompanyService
import com.hibob.academy.feedbacks_system.EmployeeData
import com.hibob.academy.feedbacks_system.EmployeeService
import com.hibob.academy.feedbacks_system.resource.JWTDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

@Component
class SessionEmployeeService(private val companyDao: CompanyDao, private val employeeDao: EmployeeDao) {
    // Generate a secure key for HS256
    companion object {
        val key: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    }


    fun createJwtToken(jwtDet: JWTDetails): String {
        val now = Date()
        val companyId: Long
        val employeeData: EmployeeData

        try {
            companyId = getCompanyByName(jwtDet.companyName).id
        } catch (e: NoSuchElementException) {
            throw e
        }

        try {
            employeeData = getEmployee(jwtDet.firstname, jwtDet.lastname, companyId)
        } catch (e: IllegalArgumentException) {
            throw e
        }

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("firstname", jwtDet.firstname)
            .claim("lastname", jwtDet.lastname)
            .claim("companyId", companyId)
            .claim("employeeId", employeeData.id)
            .claim("role", employeeData.role)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + TimeUnit.HOURS.toMillis(24)))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }

    fun getEmployee(firstname: String, lastname: String, companyId: Long): EmployeeData {
        val employeeData = employeeDao.getEmployee(firstname, lastname, companyId)
            ?: throw IllegalArgumentException("Employee not found for firstname: $firstname, lastname: $lastname, companyId: $companyId")
        return employeeData
    }

    fun getCompanyByName(companyName: String): CompanyData {
        val companyData = companyDao.getCompanyByName(companyName)
            ?: throw NoSuchElementException("No company found with name: $companyName")
        return companyData
    }
}