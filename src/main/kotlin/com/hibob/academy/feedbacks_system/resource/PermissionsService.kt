import com.hibob.academy.feedbacks_system.Department
import jakarta.ws.rs.container.ContainerRequestContext
import org.springframework.stereotype.Component

@Component
class PermissionService {
    fun extractPropertyAsLong(requestContext: ContainerRequestContext, propertyName: String): Long? {
        return requestContext.getProperty(propertyName)?.toString()?.toLongOrNull()
    }

    fun extractPropertyAsString(requestContext: ContainerRequestContext, propertyName: String): String? {
        return requestContext.getProperty(propertyName)?.toString()
    }

    fun validatePermission(role: String, permissions: List<Role>): Boolean {

        return permissions.contains(Role.valueOf(role.uppercase()))
    }


    enum class Role{
        MANAGER,
        ADMIN,
        HR,
        EMPLOYEE
    }
}