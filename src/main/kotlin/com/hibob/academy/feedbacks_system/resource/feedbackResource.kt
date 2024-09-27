package com.hibob.academy.feedbacks_system.resource

import PermissionService
import com.hibob.academy.feedbacks_system.FeedbackRequest
import com.hibob.academy.feedbacks_system.Role
import com.hibob.academy.feedbacks_system.service.FeedbackService
import com.hibob.academy.feedbacks_system.UserFeedbackFilter
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
            ?: return Response.status(Response.Status.BAD_REQUEST).build()
        try {
            feedbackService.insertFeedback(
                companyId,
                feedbackRequest.content,
                feedbackRequest.isAnonymous,
                feedbackProvider,
                feedbackRequest.department
            )

            return Response.status(Response.Status.CREATED).entity("Feedback submitted successfully").build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @GET
    @Path("/get-all-feedbacks")
    fun getAllFeedbacks(@Context requestContext: ContainerRequestContext): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")

        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR, Role.ADMIN))
        }?.let {
            return Response.ok(feedbackService.getAllCompanyFeedbacks(it)).build()
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
    }

    @GET
    @Path("/filter-feedbacks")
    fun filterFeedbacks(@Context requestContext: ContainerRequestContext, userFeedbackFilter: UserFeedbackFilter): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")

        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR, Role.ADMIN))
        }?.let {
            return Response.ok(feedbackService.filterFeedbacks(it, userFeedbackFilter)).build()
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
    }

    @PATCH
    @Path("/switch-review-status/feedback-id/{feedbackId}")
    fun switchFeedbackStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")

        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR))
        }?.let {
            try {
                feedbackService.switchFeedbackStatus(feedbackId)
                return Response.ok("Switched review status successfully").build()
            }
            catch (e: IllegalArgumentException) {
                return Response.status(Response.Status.FORBIDDEN).entity(e.message).build()
            }
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
    }

    @GET
    @Path("/check-status/feedback-id/{feedbackId}")
    fun getFeedbackStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        val permissionService = PermissionService()
        val employeeId = permissionService.extractPropertyAsLong(requestContext, "employeeId")

        employeeId?.let {
            try {
                return Response.ok(feedbackService.getFeedbackStatus(feedbackId, it)).build()
            }
            catch (e: IllegalArgumentException) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
            }
            catch (e: AccessDeniedException) {
                return Response.status(Response.Status.FORBIDDEN).entity(e.message).build()
            }
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build()
    }
}