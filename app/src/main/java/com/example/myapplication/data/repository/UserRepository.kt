package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.Address
import com.example.myapplication.data.model.DrivingLicense
import com.example.myapplication.data.model.User
import com.example.myapplication.utils.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository interface for user-related operations.
 */
interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String, phone: String): Result<User>
    suspend fun getUserById(id: Long): Result<User>
    suspend fun updateUser(id: Long, user: User): Result<User>
    suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String>
    suspend fun checkEmailExists(email: String): Result<Boolean>
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun verifyPasswordReset(email: String, code: String, newPassword: String): Result<String>
    suspend fun updateAddress(id: Long, address: Address): Result<Address>
    suspend fun getAddress(id: Long): Result<Address>
    suspend fun updateDrivingLicense(id: Long, license: DrivingLicense): Result<DrivingLicense>
    suspend fun getDrivingLicense(id: Long): Result<DrivingLicense>
    suspend fun uploadDrivingLicenseImage(id: Long, image: MultipartBody.Part): Result<String>
    suspend fun getFavorites(id: Long): Result<List<Long>>
    suspend fun addToFavorites(id: Long, carId: Long): Result<List<Long>>
    suspend fun removeFromFavorites(id: Long, carId: Long): Result<List<Long>>
}

/**
 * Implementation of the UserRepository interface.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) : UserRepository {

    override suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val request = ApiService.LoginRequest(email, password)
            val response = apiService.login(email, request.toString())
            
            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true
            
            // Get the user profile
            val user = getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String, phone: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val request = ApiService.RegisterRequest(name, email, password, phone)
            val response = apiService.register(name, email, password, request.toString())
            
            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true
            
            // Get the user profile
            val user = getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: Long): Result<User> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val user = apiService.getUserById(id, "Bearer $token")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(id: Long, user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val request = ApiService.UpdateProfileRequest(
                name = user.name.toString(),
                email = user.email.toString(),
                phone = user.phone.toString()
            )
            
            val updatedUser = apiService.updateUserProfile(id, request, "Bearer $token")
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            
            val response = apiService.verifyEmail(userId, verificationCode, "Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkEmailExists(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.checkEmailExists(email)
            Result.success(response["exists"] ?: false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = ApiService.PasswordResetRequest(email)
            apiService.requestPasswordReset(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyPasswordReset(email: String, code: String, newPassword: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = ApiService.PasswordResetVerifyRequest(email, code, newPassword)
            val response = apiService.verifyPasswordReset(request)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAddress(id: Long, address: Address): Result<Address> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val updatedAddress = apiService.updateAddress(id, address, "Bearer $token")
            Result.success(updatedAddress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAddress(id: Long): Result<Address> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val address = apiService.getAddress(id, "Bearer $token")
            Result.success(address)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDrivingLicense(id: Long, license: DrivingLicense): Result<DrivingLicense> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val updatedLicense = apiService.updateDrivingLicense(id, license, "Bearer $token")
            Result.success(updatedLicense)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDrivingLicense(id: Long): Result<DrivingLicense> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val license = apiService.getDrivingLicense(id, "Bearer $token")
            Result.success(license)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadDrivingLicenseImage(id: Long, image: MultipartBody.Part): Result<String> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val response = apiService.uploadDrivingLicenseImage(id, image, "Bearer $token")
            Result.success(response.url ?: throw IllegalStateException("No URL returned"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFavorites(id: Long): Result<List<Long>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val favorites = apiService.getFavorites(id, "Bearer $token")
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addToFavorites(id: Long, carId: Long): Result<List<Long>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val favorites = apiService.addToFavorites(id, carId, "Bearer $token")
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromFavorites(id: Long, carId: Long): Result<List<Long>> = withContext(Dispatchers.IO) {
        try {
            val token = preferenceManager.authToken ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )
            val favorites = apiService.removeFromFavorites(id, carId, "Bearer $token")
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Repository that handles authentication operations.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) {
    /**
     * Login with email and password.
     * @param email User's email.
     * @param password User's password.
     * @return Result containing the authenticated user or an error message.
     */
    suspend fun login(email: String, password: String): Result<User> {
        return userRepository.login(email, password)
    }

    /**
     * Register a new user.
     * @param name User's name.
     * @param email User's email.
     * @param password User's password.
     * @param phone User's phone number.
     * @return Result containing the registered user or an error message.
     */
    suspend fun register(name: String, email: String, password: String, phone: String): Result<User> {
        return userRepository.register(name, email, password, phone)
    }

    /**
     * Login with Facebook access token.
     * @param token Facebook access token.
     * @return Result containing the authenticated user or an error message.
     */
    suspend fun loginWithFacebook(token: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.handleOAuth2Redirect(token, 0)

            // Save the auth token
            preferenceManager.authToken = response.token
            preferenceManager.userId = response.id.toString()
            preferenceManager.isLoggedIn = true

            // Get the user profile
            val user = userRepository.getUserById(response.id).getOrThrow()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verify user's email.
     * @param userId User's ID.
     * @param verificationCode Verification code sent to the user's email.
     * @return Result containing a success message or an error message.
     */
    suspend fun verifyEmail(userId: Long, verificationCode: String): Result<String> {
        return userRepository.verifyEmail(userId, verificationCode)
    }

    /**
     * Check if an email is already registered.
     * @param email Email to check.
     * @return Result containing a boolean indicating if the email exists or an error message.
     */
    suspend fun checkEmailExists(email: String): Result<Boolean> {
        return userRepository.checkEmailExists(email)
    }

    /**
     * Check if the user is already logged in.
     * @return True if the user is logged in, false otherwise.
     */
    fun isLoggedIn(): Boolean {
        return preferenceManager.isLoggedIn
    }

    /**
     * Get the current user's ID.
     * @return The user's ID or null if not logged in.
     */
    open fun getCurrentUserId(): Long? {
        return preferenceManager.userId?.toLongOrNull()
    }

    /**
     * Log out the current user.
     */
    open fun logout() {
        preferenceManager.authToken = null
        preferenceManager.userId = null
        preferenceManager.isLoggedIn = false
    }
}