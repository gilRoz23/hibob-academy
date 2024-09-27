package com.hibob.academy.feedbacks_system.resource

import PermissionService
import com.hibob.academy.feedbacks_system.ResponseRequest
import com.hibob.academy.feedbacks_system.Role
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
    private val responseService: ResponseService
) {

    @POST
    @Path("/respond")
    fun respondFeedback(@Context requestContext: ContainerRequestContext, responseRequest: ResponseRequest): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")
        val employeeId = permissionService.extractPropertyAsLong(requestContext, "employeeId")
        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR))
        }?.let {
            try {
                responseService.insertResponse(it, employeeId, responseRequest)
                return Response.status(Response.Status.CREATED).entity("Responded successfully").build()
            }
            catch (e: IllegalArgumentException){
                return Response.status(Response.Status.FORBIDDEN).entity(e.message).build()
            }
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Request denied").build()
    }

    @GET
    @Path("/get-all-responses")
    fun getAllCompanyResponses(@Context requestContext: ContainerRequestContext): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")
        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR))
        }?.let {
            return Response.ok(responseService.getAllCompanyResponses(companyId)).build()
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Request denied").build()
    }

    @GET
    @Path("/get-all-feedback-responses/feedback-id/{feedbackId}")
    fun getAllResponseByFeedbackId(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long): Response {
        val permissionService = PermissionService()
        val role = permissionService.extractPropertyAsString(requestContext, "role") ?: ""
        val companyId = permissionService.extractPropertyAsLong(requestContext, "companyId")
        companyId?.takeIf {
            permissionService.validatePermission(role, listOf(Role.HR))
        }?.let {
            return Response.ok(responseService.getResponseByFeedbackId(feedbackId)).build()
        } ?: return Response.status(Response.Status.FORBIDDEN).entity("Request denied").build()
    }
}