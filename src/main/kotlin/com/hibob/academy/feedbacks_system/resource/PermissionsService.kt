import jakarta.ws.rs.container.ContainerRequestContext
import org.springframework.stereotype.Component

@Component
class PermissionService {
    enum class Permission {
        HR,
        MANAGER,
        ADMIN,
        HR_MANAGER,
        HR_ADMIN,
        MANAGER_ADMIN,
        HR_MANAGER_ADMIN
    }

    fun returnPermissions(requestContext: ContainerRequestContext): List<Permission> {
        val role = extractPropertyAsString(requestContext, "role")
        return mapRoleToPermissions(role ?: "")
    }

    fun mapRoleToPermissions(role: String): List<Permission> {
        return when (role.lowercase()) {
            "hr" -> listOf(Permission.HR, Permission.HR_ADMIN, Permission.HR_MANAGER, Permission.HR_MANAGER_ADMIN)
            "manager" -> listOf(Permission.MANAGER, Permission.MANAGER_ADMIN, Permission.HR_MANAGER, Permission.HR_MANAGER_ADMIN)
            "admin" -> listOf(Permission.ADMIN, Permission.HR_ADMIN, Permission.MANAGER_ADMIN, Permission.HR_MANAGER_ADMIN)
            else -> emptyList() // Return an empty list for unrecognized roles
        }
    }

    fun extractPropertyAsLong(requestContext: ContainerRequestContext, propertyName: String): Long? {
        return requestContext.getProperty(propertyName)?.toString()?.toLongOrNull()
    }

    fun extractPropertyAsString(requestContext: ContainerRequestContext, propertyName: String): String? {
        return requestContext.getProperty(propertyName)?.toString()
    }
}