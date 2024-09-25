import jakarta.ws.rs.container.ContainerRequestContext
import org.springframework.stereotype.Component

@Component
class PermissionService {
    enum class Permission {
        FEEDBACK_VIEWING,
        RESPONSE,
        TOGGLE_IS_REVIEWED
    }

    fun returnPermissions(requestContext: ContainerRequestContext): List<Permission> {
        val role = extractPropertyAsString(requestContext, "role")
        return mapRoleToPermissions(role ?: "")
    }

    fun mapRoleToPermissions(role: String): List<Permission> {
        return when (role.lowercase()) {
            "hr" -> listOf(Permission.FEEDBACK_VIEWING, Permission.RESPONSE, Permission.TOGGLE_IS_REVIEWED)
            "admin" -> listOf(Permission.FEEDBACK_VIEWING)
            else -> emptyList()
        }
    }

    fun extractPropertyAsLong(requestContext: ContainerRequestContext, propertyName: String): Long? {
        return requestContext.getProperty(propertyName)?.toString()?.toLongOrNull()
    }

    fun extractPropertyAsString(requestContext: ContainerRequestContext, propertyName: String): String? {
        return requestContext.getProperty(propertyName)?.toString()
    }
}