package com.cat.androidcat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.androidcat.data.model.MaterialDto
import com.cat.androidcat.data.repository.MaterialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MaterialUiState(
    val isLoading: Boolean = false,
    val materials: List<MaterialDto> = emptyList(),
    val selectedMaterial: MaterialDto? = null,
    val error: String? = null
)

class MaterialViewModel(
    private val repository: MaterialRepository = MaterialRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialUiState())
    val uiState: StateFlow<MaterialUiState> = _uiState.asStateFlow()

    init {
        fetchMaterials()
    }

    fun fetchMaterials(categoryId: String? = null) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.getMaterials(categoryId)
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(isLoading = false, materials = list)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Gagal memuat materi."
                )
            }
        }
    }

    fun fetchMaterialDetail(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.getMaterialById(id)
            result.onSuccess { item ->
                _uiState.value = _uiState.value.copy(isLoading = false, selectedMaterial = item)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Gagal memuat detail materi."
                )
            }
        }
    }
}
