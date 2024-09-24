package com.hibob.academy.filters

import com.hibob.academy.feedbacks_system.resource.SessionEmployeeResource
import com.hibob.academy.feedbacks_system.service.SessionEmployeeService
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider
import jakarta.ws.rs.core.Response
import org.springframework.boot.actuate.health.HttpCodeStatusMapper
import org.springframework.stereotype.Component


@Provider
@Component
class AuthenticationFilter(
    private val status: HttpCodeStatusMapper,
    private val sessionEmployeeResource: SessionEmployeeResource
) : ContainerRequestFilter {
    @Throws(Nothing::class)
    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == "api/v1/employee-feedback/login")
            return

        val cookies = requestContext.cookies
        val cookieVal = cookies[SessionEmployeeResource.COOKIE_NAME]?.value.toString()

        verify(cookieVal, requestContext)
    }

    fun verify(cookie: String?, requestContext: ContainerRequestContext) {
        if (cookie.isNullOrBlank()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
            return
        }

        try {
            val claims = Jwts.parser()
                .setSigningKey(SessionEmployeeService.key)
                .parseClaimsJws(cookie)
                .body

            // Set the claims as properties in the request context
            requestContext.setProperty("firstname", claims["firstname"].toString())
            requestContext.setProperty("lastname", claims["lastname"].toString())
            requestContext.setProperty("companyId", claims["companyId"].toString())
            requestContext.setProperty("employeeId", claims["employeeId"].toString())
            requestContext.setProperty("role", claims["role"].toString())

        } catch (e: Exception) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }
    }
}