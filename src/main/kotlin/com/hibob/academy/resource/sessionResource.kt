package com.hibob.academy.service
import com.hibob.academy.resource.JWTDetails
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("api/gilad/session")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SessionResource(private val sessionService: SessionService) {

    companion object {
        const val COOKIE_NAME = "cookieVal"
    }

    @POST
    @Path("/login")
    fun createJwtToken(jwtDet: JWTDetails): Response {
        val token = sessionService.createJwtToken(jwtDet)
        return Response.ok().cookie(NewCookie.Builder(COOKIE_NAME).value(token).build()).build()
    }

    @GET
    @Path("/try")
    fun getTest(): Response {
        return Response.ok().build()
    }
}