package com.cat.androidcat.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("website_hp") val websiteHp: String? = null,
    @SerializedName("turnstileToken") val turnstileToken: String? = null
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("website_hp") val websiteHp: String? = null,
    @SerializedName("turnstileToken") val turnstileToken: String? = null
)

data class AuthResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("accessToken") val accessToken: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String?,
    @SerializedName("role") val role: String
) {
    val isAdmin: Boolean get() = role.equals("ADMIN", ignoreCase = true)
}
