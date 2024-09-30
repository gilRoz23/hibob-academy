package com.hibob.academy.feedbacks_system.resource

import com.hibob.academy.feedbacks_system.ResponseRequest
import com.hibob.academy.feedbacks_system.Role
import com.hibob.academy.feedbacks_system.service.PermissionService
import com.hibob.academy.feedbacks_system.service.ResponseService
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
class ResponseResource(
    private val responseService: ResponseService,
    private val permissionService: PermissionService
) {

    @POST
    @Path("/respond")
    fun respondFeedback(@Context requestContext: ContainerRequestContext, responseRequest: ResponseRequest): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR)) { companyId ->
            runCatching {
                responseService.insertResponse(companyId, permissionService.extractPropertyAsLong(requestContext, "employeeId"), responseRequest)
                Response.status(Response.Status.CREATED).entity("Responded successfully").build()
            }.getOrElse { e ->
                permissionService.handleException(e)
            }
        }
    }

    @GET
    @Path("/get-all-responses")
    fun getAllCompanyResponses(@Context requestContext: ContainerRequestContext): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR)) { companyId ->
            Response.ok(responseService.getAllCompanyResponses(companyId)).build()
        }
    }

    @GET
    @Path("/get-all-feedback-responses/feedback-id/{feedbackId}")
    fun getAllResponseByFeedbackId(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        return permissionService.handleWithPermission(requestContext, listOf(Role.HR)) { companyId ->
            Response.ok(responseService.getResponseByFeedbackId(feedbackId)).build()
        }
    }
}