package com.cat.androidcat.data.repository

import com.cat.androidcat.data.api.ApiService
import com.cat.androidcat.data.api.RetrofitClient
import com.cat.androidcat.data.model.CreateMaterialRequest
import com.cat.androidcat.data.model.MaterialDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MaterialRepository(
    private val api: ApiService = RetrofitClient.apiService
) {
    suspend fun getMaterials(categoryId: String? = null): Result<List<MaterialDto>> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(api.getMaterials(categoryId))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getMaterialById(id: String): Result<MaterialDto> =
        withContext(Dispatchers.IO) {
            try {
                Result.success(api.getMaterialById(id))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun createMaterial(
        title: String,
        content: String,
        categoryId: String
    ): Result<MaterialDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.createMaterial(CreateMaterialRequest(title, content, categoryId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMaterial(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            api.deleteMaterial(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
