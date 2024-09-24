package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.service.FeedbackService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("/api/v1/employee-feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @GET
    @Path("/submit-feedback")
    fun submitFeedback(): Response {


//        return try {
//            val petsList = petService.getPetsByType(companyId, petType)
//            Response.ok(petsList).build()
//        } catch (e: IllegalArgumentException) {
//            Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
//        } catch (e: NoContentException) {
//            Response.noContent().build()
//        }
    }
}
