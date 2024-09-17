package com.hibob.academy.resource

import com.hibob.academy.service.OwnerService
import com.hibob.kotlinEx.Owner
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller


@Controller
@Path("/api/gilad/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OwnerResource(private val ownerService: OwnerService) {

    @GET
    @Path("getOwnersByCompany/{companyId}")
    fun getOwnersByCompany(@PathParam("companyId") companyId: Long): Response {
        try {
            val ownersList = ownerService.getOwnersByCompany(companyId)
            return if (ownersList.isEmpty())
                Response.noContent().build()
            else
                Response.ok(ownersList).build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @POST
    @Path("addOwner/{name}/{companyId}/{employeeId}")
    fun addOwner(@PathParam("name") name: String, @PathParam("companyId") companyId: Long, @PathParam("employeeId") employeeId: String): Response {
        try {
            ownerService.addOwner(name, companyId, employeeId)
            return Response.status(Response.Status.CREATED).entity("Owner added successfully").build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

}