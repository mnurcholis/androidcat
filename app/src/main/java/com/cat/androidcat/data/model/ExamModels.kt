package com.cat.androidcat.data.model

import com.google.gson.annotations.SerializedName

data class StartExamRequest(
    @SerializedName("type") val type: String = "EXAM", // "EXAM" or "STUDY"
    @SerializedName("category") val category: String? = null,
    @SerializedName("questionCount") val questionCount: Int = 20,
    @SerializedName("duration") val duration: Int? = null
)

data class StartExamResponse(
    @SerializedName("examId") val examId: String,
    @SerializedName("type") val type: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("questions") val questions: List<QuestionDto>,
    @SerializedName("totalQuestions") val totalQuestions: Int
)

data class SubmitAnswerRequest(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("userChoice") val userChoice: String
)

data class StudyAnswerResponse(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("userChoice") val userChoice: String,
    @SerializedName("isCorrect") val isCorrect: Boolean,
    @SerializedName("correctAnswer") val correctAnswer: String,
    @SerializedName("explanation") val explanation: String?
)

data class AnswerDetailDto(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("questionText") val questionText: String,
    @SerializedName("userChoice") val userChoice: String,
    @SerializedName("correctAnswer") val correctAnswer: String,
    @SerializedName("isCorrect") val isCorrect: Boolean,
    @SerializedName("explanation") val explanation: String?
)

data class ExamResultDto(
    @SerializedName("examId") val examId: String,
    @SerializedName("totalQuestions") val totalQuestions: Int,
    @SerializedName("correctAnswers") val correctAnswers: Int,
    @SerializedName("wrongAnswers") val wrongAnswers: Int,
    @SerializedName("unanswered") val unanswered: Int = 0,
    @SerializedName("score") val score: Double,
    @SerializedName("grade") val grade: String,
    @SerializedName("details") val details: List<AnswerDetailDto>
)

data class ExamHistoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("score") val score: Double?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String?,
    @SerializedName("createdAt") val createdAt: String
)
