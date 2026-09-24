package com.cat.androidcat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cat.androidcat.data.model.*
import com.cat.androidcat.data.repository.ExamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExamUiState(
    val isLoading: Boolean = false,
    val examId: String? = null,
    val mode: String = "EXAM", // "EXAM" or "STUDY"
    val questions: List<QuestionDto> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(), // questionId -> selectedOption
    val doubtfulQuestions: Set<String> = emptySet(),
    val studyFeedback: Map<String, StudyAnswerResponse> = emptyMap(),
    val remainingSeconds: Long = 0,
    val isFinished: Boolean = false,
    val examResult: ExamResultDto? = null,
    val error: String? = null,
    val categories: List<CategoryDto> = emptyList()
)

class ExamViewModel(
    private val repository: ExamRepository = ExamRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamUiState())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().onSuccess { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
    }

    fun startSession(
        mode: String,
        category: String? = null,
        questionCount: Int = 20,
        durationMinutes: Int? = if (mode == "STUDY") null else 60,
        onStarted: (String) -> Unit = {}
    ) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, mode = mode)
        viewModelScope.launch {
            val result = repository.startExam(
                type = mode,
                category = category,
                count = questionCount,
                durationMinutes = durationMinutes
            )
            result.onSuccess { session ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    examId = session.examId,
                    mode = mode,
                    questions = session.questions,
                    currentIndex = 0,
                    answers = emptyMap(),
                    doubtfulQuestions = emptySet(),
                    studyFeedback = emptyMap(),
                    remainingSeconds = (session.duration ?: durationMinutes ?: 0) * 60L,
                    isFinished = false,
                    examResult = null
                )
                if (mode == "EXAM") {
                    startTimer()
                }
                onStarted(session.examId)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Gagal memulai sesi ujian."
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && !_uiState.value.isFinished) {
                delay(1000)
                val newSeconds = _uiState.value.remainingSeconds - 1
                _uiState.value = _uiState.value.copy(remainingSeconds = newSeconds)
                if (newSeconds <= 0) {
                    finishExam()
                    break
                }
            }
        }
    }

    fun selectOption(optionKey: String) {
        val state = _uiState.value
        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
        val examId = state.examId ?: return

        // Update local answers
        val newAnswers = state.answers.toMutableMap()
        newAnswers[currentQ.id] = optionKey
        _uiState.value = state.copy(answers = newAnswers)

        viewModelScope.launch {
            if (state.mode == "STUDY") {
                val result = repository.submitStudyAnswer(examId, currentQ.id, optionKey)
                result.onSuccess { feedback ->
                    val newFeedback = _uiState.value.studyFeedback.toMutableMap()
                    newFeedback[currentQ.id] = feedback
                    _uiState.value = _uiState.value.copy(studyFeedback = newFeedback)
                }
            } else {
                repository.submitExamAnswer(examId, currentQ.id, optionKey)
            }
        }
    }

    fun toggleDoubt() {
        val state = _uiState.value
        val currentQ = state.questions.getOrNull(state.currentIndex) ?: return
        val currentDoubt = state.doubtfulQuestions.toMutableSet()
        if (currentDoubt.contains(currentQ.id)) {
            currentDoubt.remove(currentQ.id)
        } else {
            currentDoubt.add(currentQ.id)
        }
        _uiState.value = state.copy(doubtfulQuestions = currentDoubt)
    }

    fun goToIndex(index: Int) {
        if (index in 0 until _uiState.value.questions.size) {
            _uiState.value = _uiState.value.copy(currentIndex = index)
        }
    }

    fun nextQuestion() {
        val nextIdx = _uiState.value.currentIndex + 1
        if (nextIdx < _uiState.value.questions.size) {
            _uiState.value = _uiState.value.copy(currentIndex = nextIdx)
        }
    }

    fun previousQuestion() {
        val prevIdx = _uiState.value.currentIndex - 1
        if (prevIdx >= 0) {
            _uiState.value = _uiState.value.copy(currentIndex = prevIdx)
        }
    }

    fun finishExam(onFinished: (String) -> Unit = {}) {
        val examId = _uiState.value.examId ?: return
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = repository.finishExam(examId)
            result.onSuccess { examResult ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isFinished = true,
                    examResult = examResult
                )
                onFinished(examId)
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = err.message ?: "Gagal menyelesaikan ujian."
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
