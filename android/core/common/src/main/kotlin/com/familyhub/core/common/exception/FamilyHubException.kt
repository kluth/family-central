package com.familyhub.core.common.exception

/**
 * Base exception for all FamilyHub application errors
 */
sealed class FamilyHubException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    // Authentication exceptions
    class AuthenticationException(message: String? = null, cause: Throwable? = null) : FamilyHubException(message, cause)
    class InvalidCredentialsException(message: String = "Invalid email or password") : FamilyHubException(message)
    class UserNotFoundException(message: String = "User not found") : FamilyHubException(message)
    class EmailAlreadyExistsException(message: String = "Email already registered") : FamilyHubException(message)
    class WeakPasswordException(message: String = "Password is too weak") : FamilyHubException(message)

    // Authorization exceptions
    class UnauthorizedException(message: String = "Unauthorized access") : FamilyHubException(message)
    class InsufficientPermissionsException(message: String = "Insufficient permissions") : FamilyHubException(message)

    // Family exceptions
    class FamilyNotFoundException(message: String = "Family not found") : FamilyHubException(message)
    class InvalidInviteCodeException(message: String = "Invalid or expired invite code") : FamilyHubException(message)
    class AlreadyFamilyMemberException(message: String = "Already a member of this family") : FamilyHubException(message)

    // Network exceptions
    class NetworkException(message: String = "Network error occurred", cause: Throwable? = null) : FamilyHubException(message, cause)
    class TimeoutException(message: String = "Request timed out") : FamilyHubException(message)

    // Data exceptions
    class DataNotFoundException(message: String = "Data not found") : FamilyHubException(message)
    class DataCorruptedException(message: String = "Data is corrupted") : FamilyHubException(message)

    // Validation exceptions
    class ValidationException(message: String) : FamilyHubException(message)
    class InvalidInputException(message: String) : FamilyHubException(message)

    // General exceptions
    class UnknownException(message: String = "An unknown error occurred", cause: Throwable? = null) : FamilyHubException(message, cause)
}
