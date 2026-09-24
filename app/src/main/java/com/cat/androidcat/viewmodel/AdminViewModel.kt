package com.cat.androidcat.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.androidcat.CatApplication
import com.cat.androidcat.data.model.CategoryDto
import com.cat.androidcat.data.model.QuestionDto
import com.cat.androidcat.data.model.UserDto
import com.cat.androidcat.data.repository.AdminRepository
import com.cat.androidcat.data.repository.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val isSavingAi: Boolean = false,
    val questions: List<QuestionDto> = emptyList(),
    val totalQuestions: Int = 0,
    val categories: List<CategoryDto> = emptyList(),
    val users: List<UserDto> = emptyList(),
    val totalUsers: Int = 0,
    val selectedCategoryFilter: String? = null,
    val searchQuery: String = "",
    val generatedPreview: List<QuestionDto> = emptyList(),
    val customApiKey: String = "",
    val selectedModel: String = "gemini-2.5-flash",
    val error: String? = null,
    val successMessage: String? = null
)

class AdminViewModel(
    private val adminRepo: AdminRepository = AdminRepository(),
    private val examRepo: ExamRepository = ExamRepository()
) : ViewModel() {

    private val prefs = CatApplication.instance.getSharedPreferences("cat_admin_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        AdminUiState(
            customApiKey = prefs.getString("gemini_api_key", "") ?: "",
            selectedModel = prefs.getString("gemini_model", "gemini-2.5-flash") ?: "gemini-2.5-flash"
        )
    )
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun saveApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key.trim()).apply()
        _uiState.value = _uiState.value.copy(customApiKey = key.trim())
    }

    fun setAiModel(model: String) {
        prefs.edit().putString("gemini_model", model).apply()
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    fun loadDashboardData(categoryFilter: String? = _uiState.value.selectedCategoryFilter) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            // 1. Categories
            examRepo.getCategories().onSuccess { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }

            // 2. Questions (with optional category filter)
            adminRepo.getQuestions(page = 1, limit = 100, category = categoryFilter).onSuccess { res ->
                _uiState.value = _uiState.value.copy(
                    questions = res.data,
                    totalQuestions = res.total,
                    selectedCategoryFilter = categoryFilter
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(error = err.message)
            }

            // 3. Users count & list
            adminRepo.getUsers().onSuccess { usersList ->
                _uiState.value = _uiState.value.copy(
                    users = usersList,
                    totalUsers = usersList.size
                )
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun filterByCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryFilter = categoryId)
        loadDashboardData(categoryId)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun generateAiQuestions(
        categoryId: String,
        topic: String?,
        count: Int,
        difficulty: String,
        apiKey: String?,
        customInstructions: String? = null,
        model: String? = null
    ) {
        val effectiveKey = apiKey?.ifBlank { null } ?: _uiState.value.customApiKey.ifBlank { null }
        if (effectiveKey != null) {
            saveApiKey(effectiveKey)
        }
        val effectiveModel = model ?: _uiState.value.selectedModel

        _uiState.value = _uiState.value.copy(
            isGenerating = true,
            error = null,
            successMessage = null
        )

        viewModelScope.launch {
            val result = adminRepo.generateAiQuestions(
                categoryId = categoryId,
                topic = topic,
                count = count,
                difficulty = difficulty,
                apiKey = effectiveKey,
                customInstructions = customInstructions,
                model = effectiveModel
            )

            result.onSuccess { generatedList ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    generatedPreview = generatedList,
                    successMessage = "Berhasil membuat ${generatedList.size} butir soal! Silakan tinjau dan klik 'Simpan ke Bank Soal'."
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = err.message ?: "Gagal membuat soal dengan AI."
                )
            }
        }
    }

    fun saveGeneratedPreviewToBank() {
        val listToSave = _uiState.value.generatedPreview
        if (listToSave.isEmpty()) return

        _uiState.value = _uiState.value.copy(isSavingAi = true, error = null)
        viewModelScope.launch {
            val result = adminRepo.batchCreateQuestions(listToSave)
            result.onSuccess { countSaved ->
                _uiState.value = _uiState.value.copy(
                    isSavingAi = false,
                    generatedPreview = emptyList(),
                    successMessage = "Sukses! $countSaved butir soal baru telah disimpan ke Bank Soal."
                )
                // Reload question list to reflect changes immediately
                loadDashboardData()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSavingAi = false,
                    error = err.message ?: "Gagal menyimpan soal ke Bank Soal."
                )
            }
        }
    }

    fun clearGeneratedPreview() {
        _uiState.value = _uiState.value.copy(generatedPreview = emptyList(), error = null, successMessage = null)
    }

    fun deleteQuestion(id: String) {
        viewModelScope.launch {
            adminRepo.deleteQuestion(id).onSuccess {
                loadDashboardData()
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(error = err.message)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
