package com.hibob.academy.service
import com.hibob.academy.resource.JWTDetails
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("api/gilad/session")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SessionResource(private val sessionService: SessionService) {

    @POST
    @Path("/login")
    fun createJwtToken(jwtDet : JWTDetails): Response {
        return Response.ok(sessionService.createJwtToken(jwtDet)).build()
    }
}