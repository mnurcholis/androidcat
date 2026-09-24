package com.cat.androidcat.data.model

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("code") val code: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("passingGrade") val passingGrade: Int? = null,
    @SerializedName("questionCount") val questionCount: Int? = null,
    @SerializedName("subcategories") val subcategories: List<CategoryDto>? = null,
    @SerializedName("children") val children: List<CategoryDto>? = null,
    @SerializedName("parentId") val parentId: String? = null
) {
    val allChildren: List<CategoryDto> get() = children ?: subcategories ?: emptyList()
}

data class CategoryCountItem(
    @SerializedName("category") val category: String,
    @SerializedName("count") val count: Int
)
