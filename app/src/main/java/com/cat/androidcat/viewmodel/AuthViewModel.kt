package com.cat.androidcat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.androidcat.CatApplication
import com.cat.androidcat.data.model.UserDto
import com.cat.androidcat.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(
        sessionManager = CatApplication.instance.sessionManager
    )
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(user = repository.getCurrentUser())
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean get() = repository.isLoggedIn
    val isAdmin: Boolean get() = repository.isAdmin

    fun login(email: String, pass: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSuccess = false)
        viewModelScope.launch {
            val result = repository.login(email.trim(), pass)
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = response.user,
                    isSuccess = true,
                    error = null
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Login gagal. Periksa email dan password."
                )
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSuccess = false)
        viewModelScope.launch {
            val result = repository.register(name.trim(), email.trim(), pass)
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = response.user,
                    isSuccess = true,
                    error = null
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Registrasi gagal. Silakan coba lagi."
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
