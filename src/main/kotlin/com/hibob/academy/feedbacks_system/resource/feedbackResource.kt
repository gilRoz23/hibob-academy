package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.service.FeedbackService
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("/api/v1/employee-feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @POST
    @Path("/submit-feedback")
    fun submitFeedback(@Context requestContext: ContainerRequestContext): Response {
        val firstname = requestContext.getProperty("firstname") as String
        val lastname = requestContext.getProperty("lastname") as String
        val companyId = requestContext.getProperty("companyId") as String
        val employeeId = requestContext.getProperty("employeeId") as String
        val role = requestContext.getProperty("role") as String
        feedbackService.insertFeedback(companyId, content, isAnonymous, feedbackProvider, department)

        return Response.ok("Feedback submitted successfully").build()
    }
}
