package com.cat.androidcat.data.model

import com.google.gson.annotations.SerializedName

data class MaterialDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("category") val category: CategoryDto? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class CreateMaterialRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("categoryId") val categoryId: String
)
