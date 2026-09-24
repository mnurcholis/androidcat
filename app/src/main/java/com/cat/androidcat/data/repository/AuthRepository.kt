package com.cat.androidcat.data.repository

import com.cat.androidcat.data.api.ApiService
import com.cat.androidcat.data.api.RetrofitClient
import com.cat.androidcat.data.model.AuthResponse
import com.cat.androidcat.data.model.LoginRequest
import com.cat.androidcat.data.model.RegisterRequest
import com.cat.androidcat.data.model.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val api: ApiService = RetrofitClient.apiService,
    private val sessionManager: SessionManager
) {
    private fun extractErrorMessage(e: Exception): String {
        if (e is retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                try {
                    val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    if (json.has("message")) {
                        val msgElem = json.get("message")
                        if (msgElem.isJsonArray) {
                            return msgElem.asJsonArray.joinToString(", ") { it.asString }
                        } else if (msgElem.isJsonPrimitive) {
                            val msg = msgElem.asString
                            return when {
                                msg.contains("Invalid credentials", ignoreCase = true) -> "Email atau password salah."
                                msg.contains("Email already registered", ignoreCase = true) -> "Email ini sudah terdaftar."
                                msg.contains("ThrottlerException", ignoreCase = true) -> "Terlalu banyak percobaan. Silakan tunggu 1 menit."
                                else -> msg
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            return when (e.code()) {
                400 -> "Permintaan tidak valid."
                401 -> "Email atau password yang Anda masukkan salah."
                403 -> "Akses ditolak."
                429 -> "Terlalu banyak percobaan. Harap tunggu sebentar."
                else -> "Terjadi kendala server (${e.code()})."
            }
        }
        return e.localizedMessage ?: "Gagal terhubung ke server. Periksa koneksi internet Anda."
    }

    suspend fun login(email: String, pass: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(
                    LoginRequest(
                        email = email,
                        password = pass,
                        turnstileToken = "android_native_app_token"
                    )
                )
                sessionManager.saveAuthSession(response.accessToken, response.user)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    suspend fun register(name: String, email: String, pass: String): Result<AuthResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.register(
                    RegisterRequest(
                        name = name,
                        email = email,
                        password = pass,
                        turnstileToken = "android_native_app_token"
                    )
                )
                sessionManager.saveAuthSession(response.accessToken, response.user)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    suspend fun fetchProfile(): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            val user = api.getProfile()
            val token = sessionManager.getToken() ?: ""
            sessionManager.saveAuthSession(token, user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun getCurrentUser(): UserDto? = sessionManager.getUser()

    val isLoggedIn: Boolean get() = sessionManager.isLoggedIn

    val isAdmin: Boolean get() = sessionManager.isAdmin
}
