package com.hibob.academy.filters

import com.hibob.academy.service.SessionResource
import com.hibob.academy.service.SessionService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
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
    private val sessionResource: SessionResource
) : ContainerRequestFilter {
    @Throws(Nothing::class)
    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == "api/gilad/session/login")
            return

        val cookies = requestContext.cookies
        val cookieVal = cookies[SessionResource.COOKIE_NAME]?.value.toString()

        try {
            verify(cookieVal)
        } catch (e: Exception) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }

    }

    fun verify(cookie: String?) {
        cookie?.let {
            try {
                Jwts.parser().setSigningKey(SessionService.key).parseClaimsJws(it)
            } catch (e: Exception) {
                throw Exception("Invalid token")
            }
        } ?: throw Exception("Missing token")
    }
}