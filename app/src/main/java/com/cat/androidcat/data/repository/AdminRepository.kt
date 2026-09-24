package com.cat.androidcat.data.repository

import com.cat.androidcat.data.api.ApiService
import com.cat.androidcat.data.api.RetrofitClient
import com.cat.androidcat.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository(
    private val api: ApiService = RetrofitClient.apiService
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
                            return msgElem.asString
                        }
                    }
                } catch (_: Exception) {}
            }
            return "Error HTTP ${e.code()}"
        }
        return e.localizedMessage ?: "Terjadi kesalahan sistem."
    }

    suspend fun getQuestions(
        page: Int = 1,
        limit: Int = 100,
        category: String? = null
    ): Result<PaginatedQuestionsResponse> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getQuestions(page, limit, category))
        } catch (e: Exception) {
            Result.failure(Exception(extractErrorMessage(e)))
        }
    }

    suspend fun generateAiQuestions(
        categoryId: String,
        topic: String? = null,
        count: Int = 5,
        difficulty: String = "medium",
        apiKey: String? = null,
        customInstructions: String? = null
    ): Result<List<QuestionDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.generateAiQuestions(
                GenerateAiQuestionsRequest(
                    categoryId = categoryId,
                    topic = topic,
                    count = count,
                    difficulty = difficulty,
                    apiKey = apiKey,
                    customInstructions = customInstructions
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(extractErrorMessage(e)))
        }
    }

    suspend fun batchCreateQuestions(questions: List<QuestionDto>): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                val res = api.batchCreateQuestions(BatchCreateQuestionsRequest(questions))
                Result.success(res.count)
            } catch (e: Exception) {
                Result.failure(Exception(extractErrorMessage(e)))
            }
        }

    suspend fun deleteQuestion(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.deleteQuestion(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(extractErrorMessage(e)))
        }
    }

    suspend fun batchDeleteQuestions(ids: List<String>): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = api.batchDeleteQuestions(BatchDeleteRequest(ids))
            Result.success(response.count)
        } catch (e: Exception) {
            Result.failure(Exception(extractErrorMessage(e)))
        }
    }

    suspend fun getUsers(): Result<List<UserDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getUsers())
        } catch (e: Exception) {
            Result.failure(Exception(extractErrorMessage(e)))
        }
    }
}
