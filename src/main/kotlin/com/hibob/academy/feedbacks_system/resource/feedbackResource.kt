package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.Department
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
    fun submitFeedback(@Context requestContext: ContainerRequestContext, feedbackRequest: FeedbackRequest): Response {
        val feedbackProvider: Long? = if (feedbackRequest.isAnonymous) {
            null
        } else {
            extractPropertyAsLong(requestContext, "employeeId")
        }

        val companyId = extractPropertyAsLong(requestContext, "companyId")
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity("couldn't convert companyId").build()

        feedbackService.insertFeedback(
            companyId,
            feedbackRequest.content,
            feedbackRequest.isAnonymous,
            feedbackProvider,
            feedbackRequest.department
        )

        return Response.status(Response.Status.CREATED).entity("Feedback submitted successfully").build()
    }

    @GET
    @Path("/get-all-feedbacks")
    fun getAllFeedbacks(@Context requestContext: ContainerRequestContext): Response {
        val role = extractPropertyAsString(requestContext, "role")
        if (role == "hr" || role == "manager" || role == "admin") {
            val companyId = extractPropertyAsLong(requestContext, "companyId")
            if (companyId != null) {
                val feedbacksList = feedbackService.getAllCompanyFeedbacks(companyId)
                return Response.status(Response.Status.OK).entity(feedbacksList).build()
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Company ID is missing").build()
            }
        }
        else {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
        }
    }

    // Private function to extract properties as Long
    private fun extractPropertyAsLong(requestContext: ContainerRequestContext, propertyName: String): Long? {
        return requestContext.getProperty(propertyName)?.toString()?.toLongOrNull()
    }

    private fun extractPropertyAsString(requestContext: ContainerRequestContext, propertyName: String): String? {
        return requestContext.getProperty(propertyName)?.toString()
    }
}

data class FeedbackRequest(
    val content: String,
    val isAnonymous: Boolean,
    val department: Department
)
