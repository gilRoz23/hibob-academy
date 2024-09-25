package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.service.SessionEmployeeService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("api/v1/employee-feedback/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SessionEmployeeResource(private val sessionEmployeeService: SessionEmployeeService) {
    companion object {
        const val COOKIE_NAME = "cookieVal"
    }

    @POST
    @Path("/login")
    fun createJwtToken(jwtDet: JWTDetails): Response {
        return try {
            val token = sessionEmployeeService.createJwtToken(jwtDet)
            Response.ok()
                .cookie(NewCookie.Builder(COOKIE_NAME).value(token).build())
                .build()
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("${e.message}")
                .build()
        }
    }
}