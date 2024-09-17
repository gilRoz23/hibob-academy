package com.hibob.academy.resource
import com.hibob.academy.dao.OwnerData
import com.hibob.academy.dao.PetType
import com.hibob.academy.service.PetService
import com.hibob.kotlinEx.Owner
import com.hibob.kotlinEx.Pet
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import java.sql.Date


@Controller
@Path("/api/gilad/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PetResource(private val petService: PetService) {

    @GET
    @Path("getPetsByType/{companyId}/{petType}")
    fun getPetsByType(@PathParam("companyId") companyId: Long, @PathParam("petType") petType: PetType): Response {
        try {
            val petsList = petService.getPetsByType(companyId, petType)
            return if (petsList.isEmpty())
                Response.noContent().build()
            else
                Response.ok(petsList).build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @GET
    @Path("getOwnerByPetId/{petId}")
    fun getOwnerByPetId(@PathParam("petId") petId: Int): Response {
        try {
            val owner: OwnerData? = petService.getOwnerByPetId(petId)
            return if (owner != null) {
                Response.ok(owner).build()
            } else {
                Response.status(Response.Status.NOT_FOUND)
                    .entity("Owner not found for pet ID: $petId").build()
            }
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @POST
    @Path("addPet/{companyId}/{name}/{type}")
    fun addPet(@PathParam("companyId") companyId: Long, @PathParam("name") name: String, @PathParam("type") type: PetType): Response {
        try {
            petService.addPet(companyId, name, type)
            return Response.status(Response.Status.CREATED).entity("Pet added successfully").build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }

    @PUT
    @Path("adoptPet/{petId}/{ownerId}")
    fun adoptPet(@PathParam("petId") petId: Int, @PathParam("ownerId") ownerId: Long): Response {
        try {
            petService.adoptPet(petId, ownerId)
            return Response.status(Response.Status.CREATED).entity("Pet adopted successfully").build()
        }
        catch (e: IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.message).build()
        }
    }
}
