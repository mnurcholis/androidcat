package com.cat.androidcat.data.api

import com.cat.androidcat.data.model.*
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @GET("auth/profile")
    suspend fun getProfile(): UserDto

    // Categories
    @GET("questions/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("questions/categories/count")
    suspend fun getCategoryCounts(): List<CategoryCountItem>

    // Questions
    @GET("questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String? = null
    ): PaginatedQuestionsResponse

    @POST("questions/generate-ai")
    suspend fun generateAiQuestions(@Body body: GenerateAiQuestionsRequest): List<QuestionDto>

    @POST("questions/batch-create")
    suspend fun batchCreateQuestions(@Body body: BatchCreateQuestionsRequest): BatchCreateResponse

    @POST("questions/batch-delete")
    suspend fun batchDeleteQuestions(@Body body: BatchDeleteRequest): BatchDeleteResponse

    @DELETE("questions/{id}")
    suspend fun deleteQuestion(@Path("id") id: String): Map<String, Any>

    // Exams
    @POST("exams/start")
    suspend fun startExam(@Body body: StartExamRequest): StartExamResponse

    @POST("exams/{examId}/exam/answer")
    suspend fun submitExamAnswer(
        @Path("examId") examId: String,
        @Body body: SubmitAnswerRequest
    ): Map<String, Any>

    @POST("exams/{examId}/study/answer")
    suspend fun submitStudyAnswer(
        @Path("examId") examId: String,
        @Body body: SubmitAnswerRequest
    ): StudyAnswerResponse

    @POST("exams/{examId}/finish")
    suspend fun finishExam(@Path("examId") examId: String): ExamResultDto

    @GET("exams")
    suspend fun getUserExams(): List<ExamHistoryItem>

    // Materials
    @GET("materials")
    suspend fun getMaterials(@Query("categoryId") categoryId: String? = null): List<MaterialDto>

    @GET("materials/{id}")
    suspend fun getMaterialById(@Path("id") id: String): MaterialDto

    @POST("materials")
    suspend fun createMaterial(@Body body: CreateMaterialRequest): MaterialDto

    @DELETE("materials/{id}")
    suspend fun deleteMaterial(@Path("id") id: String): Map<String, Any>

    // Users (Admin)
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
