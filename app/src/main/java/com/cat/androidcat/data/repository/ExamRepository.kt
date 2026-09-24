package com.cat.androidcat.data.repository

import com.cat.androidcat.data.api.ApiService
import com.cat.androidcat.data.api.RetrofitClient
import com.cat.androidcat.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExamRepository(
    private val api: ApiService = RetrofitClient.apiService
) {
    suspend fun getCategories(): Result<List<CategoryDto>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getCategories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryCounts(): Result<List<CategoryCountItem>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getCategoryCounts())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startExam(
        type: String,
        category: String? = null,
        count: Int = 20,
        durationMinutes: Int? = 60
    ): Result<StartExamResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.startExam(
                StartExamRequest(
                    type = type,
                    category = category,
                    questionCount = count,
                    duration = if (type == "STUDY") null else durationMinutes
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitExamAnswer(
        examId: String,
        questionId: String,
        choice: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.submitExamAnswer(examId, SubmitAnswerRequest(questionId, choice))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitStudyAnswer(
        examId: String,
        questionId: String,
        choice: String
    ): Result<StudyAnswerResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.submitStudyAnswer(examId, SubmitAnswerRequest(questionId, choice))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finishExam(examId: String): Result<ExamResultDto> = withContext(Dispatchers.IO) {
        try {
            val result = api.finishExam(examId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamHistory(): Result<List<ExamHistoryItem>> = withContext(Dispatchers.IO) {
        try {
            Result.success(api.getUserExams())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
