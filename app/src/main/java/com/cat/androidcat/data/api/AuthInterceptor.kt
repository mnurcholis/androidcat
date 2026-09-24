package com.cat.androidcat.data.api

import com.cat.androidcat.data.repository.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("X-App-Platform", "android")

        val token = sessionManager.getToken()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
