package com.cat.androidcat.data.model

import com.google.gson.annotations.SerializedName

data class QuestionOption(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)

data class QuestionDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("text") val text: String,
    @SerializedName("options") val options: List<QuestionOption>,
    @SerializedName("correctAnswer") val correctAnswer: String? = null,
    @SerializedName("explanation") val explanation: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("categoryRel") val categoryRel: CategoryDto? = null,
    @SerializedName("figuralData") val figuralData: FiguralDataDto? = null
)

data class FiguralShapeDto(
    @SerializedName("shape") val shape: String = "circle",
    @SerializedName("fill") val fill: String = "solid",
    @SerializedName("rotation") val rotation: Float = 0f,
    @SerializedName("size") val size: String = "medium",
    @SerializedName("position") val position: String = "center"
)

data class FiguralCellDto(
    @SerializedName("row") val row: Int = 0,
    @SerializedName("col") val col: Int = 0,
    @SerializedName("index") val index: Int = 0,
    @SerializedName("isMissing") val isMissing: Boolean = false,
    @SerializedName("shapes") val shapes: List<FiguralShapeDto> = emptyList()
)

data class FiguralOptionItemDto(
    @SerializedName("shapes") val shapes: List<FiguralShapeDto> = emptyList()
)

data class FiguralDataDto(
    @SerializedName("type") val type: String = "grid_3x3",
    @SerializedName("questionGrid") val questionGrid: List<FiguralCellDto> = emptyList(),
    @SerializedName("options") val options: Map<String, FiguralOptionItemDto> = emptyMap()
)

data class GenerateAiQuestionsRequest(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("count") val count: Int = 5,
    @SerializedName("difficulty") val difficulty: String = "medium",
    @SerializedName("customInstructions") val customInstructions: String? = null,
    @SerializedName("apiKey") val apiKey: String? = null,
    @SerializedName("model") val model: String? = null
)

data class BatchCreateQuestionsRequest(
    @SerializedName("questions") val questions: List<QuestionDto>
)

data class BatchCreateResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("questions") val questions: List<QuestionDto>? = null
)

data class BatchDeleteRequest(
    @SerializedName("ids") val ids: List<String>
)

data class BatchDeleteResponse(
    @SerializedName("count") val count: Int
)

data class QuestionMeta(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("limit") val limit: Int = 50,
    @SerializedName("totalPages") val totalPages: Int = 1
)

data class PaginatedQuestionsResponse(
    @SerializedName("data") val data: List<QuestionDto> = emptyList(),
    @SerializedName("meta") val meta: QuestionMeta? = null
) {
    val total: Int get() = meta?.total ?: data.size
    val page: Int get() = meta?.page ?: 1
    val limit: Int get() = meta?.limit ?: data.size
}
