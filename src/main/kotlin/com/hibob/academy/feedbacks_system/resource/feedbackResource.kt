package com.hibob.academy.feedbacks_system.resource

import PermissionService
import com.hibob.academy.feedbacks_system.FeedbackRequest
import com.hibob.academy.feedbacks_system.Role
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
class FeedbackResource(
    private val feedbackService: FeedbackService
) {

    @POST
    @Path("/submit-feedback")
    fun submitFeedback(@Context requestContext: ContainerRequestContext, feedbackRequest: FeedbackRequest): Response {
        val permissionService = PermissionService()
        val feedbackProvider: Long? = if (feedbackRequest.isAnonymous) {
            null
        } else {
            permissionService.extractPropertyAsLong(requestContext, "employeeId")
        }

        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")
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
        val permissionService = PermissionService()

        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""

        if (permissionService.validatePermission(role, listOf(Role.HR, Role.ADMIN))) {
            val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")

            companyId?.let {
                return Response.ok(feedbackService.getAllCompanyFeedbacks(it)).build()
            } ?: return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
        }
    }

    @GET
    @Path("/filter-feedbacks")
    fun filterFeedbacks(@Context requestContext: ContainerRequestContext, feedbackData: FeedbackData): Response {
        val permissionService = PermissionService()
        val permissions = permissionService.returnPermissions(requestContext)

        return if (permissions.contains(PermissionService.Permission.FEEDBACK_VIEWING)) {
            val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")

            companyId?.let {
                Response.ok(feedbackService.filterFeedbacks(it, feedbackData.isAnonymous, feedbackData.status, feedbackData.feedbackProviderId, feedbackData.department, feedbackData.timeOfSubmitting)).build()
            } ?: Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
        } else {
            Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
        }
    }
}