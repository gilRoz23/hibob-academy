package com.hibob.academy.resource

import com.hibob.academy.dao.PetType
import com.hibob.academy.service.PetService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller

@Controller
@Path("/api/gilad/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PetResource(private val petService: PetService) {

    @GET
    @Path("/get-by-type/companyID/{companyId}/type/{petType}")
    fun getPetsByType(@PathParam("companyId") companyId: Long, @PathParam("petType") petType: PetType): Response {
        return try {
            val petsList = petService.getPetsByType(companyId, petType)
            Response.ok(petsList).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @GET
    @Path("/get-owner-by-petID/{petId}")
    fun getOwnerByPetId(@PathParam("petId") petId: Int): Response {
        return try {
            val owner = petService.getOwnerByPetId(petId)
            Response.ok(owner).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @POST
    @Path("/companyID/{companyId}/name/{name}/type/{type}")
    fun addPet(
        @PathParam("companyId") companyId: Long,
        @PathParam("name") name: String,
        @PathParam("type") type: PetType
    ): Response {
        return try {
            petService.addPet(companyId, name, type)
            Response.status(Response.Status.CREATED).entity("Pet added successfully").build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @PUT
    @Path("/adopt-pet/petID/{petId}/ownerID/{ownerId}")
    fun adoptPet(@PathParam("petId") petId: Int, @PathParam("ownerId") ownerId: Long): Response {
        return try {
            petService.adoptPet(petId, ownerId)
            Response.status(Response.Status.CREATED).entity("Pet adopted successfully").build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @GET
    @Path("ownerID/{ownerId}")
    fun getPetsByOwnerId(@PathParam("ownerId") ownerId: Long): Response {
        try {
            val petsList = petService.getPetsByOwnerId(ownerId)
            return Response.ok(petsList).build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @GET
    @Path("count-by-type/{companyId}")
    fun countPetsByType(@PathParam("companyId") companyId: Long): Response {
        try {
            val mapsList = petService.countPetsByType(companyId)
            return Response.ok(mapsList).build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }
}
