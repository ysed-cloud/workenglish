package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiCoachFeedback
import com.example.data.model.CurriculumData
import com.example.data.model.PracticeSession
import com.example.data.model.SavedVocab
import com.example.data.model.UserProgress
import com.example.data.model.VocabItem
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.WorkEnglishRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiCoachUiState {
  data object Idle : AiCoachUiState
  data object Loading : AiCoachUiState
  data class Success(val feedback: AiCoachFeedback) : AiCoachUiState
  data class Error(val message: String) : AiCoachUiState
}

class WorkEnglishViewModel(
  private val repository: WorkEnglishRepository,
  private val aiCoachRepository: AiCoachRepository,
  private val currentUserId: String,
  private val initialDisplayName: String,
) : ViewModel() {

  val progressState: StateFlow<UiState<UserProgress>> = repository.observeUserProgress(currentUserId)
    .map<UserProgress?, UiState<UserProgress>> { progress ->
      if (progress != null) {
        UiState.Success(progress)
      } else {
        UiState.Loading
      }
    }
    .catch { error ->
      val firestoreError = generateSequence(error) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreError == null && error is CancellationException) throw error
      val rootError = firestoreError ?: error
      Log.w(TAG, "Error observing user progress", rootError)
      emit(UiState.Error(rootError.message ?: "Failed to load learning progress"))
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
      initialValue = UiState.Loading,
    )

  val savedVocabState: StateFlow<List<SavedVocab>> = repository.observeSavedVocab(currentUserId)
    .catch { error ->
      val firestoreError = generateSequence(error) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreError == null && error is CancellationException) throw error
      Log.w(TAG, "Error observing saved vocabulary", error)
      emit(emptyList())
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
      initialValue = emptyList(),
    )

  val practiceSessionsState: StateFlow<List<PracticeSession>> = repository.observePracticeSessions(currentUserId)
    .catch { error ->
      val firestoreError = generateSequence(error) { it.cause }
        .filterIsInstance<FirebaseFirestoreException>()
        .firstOrNull()
      if (firestoreError == null && error is CancellationException) throw error
      Log.w(TAG, "Error observing practice sessions", error)
      emit(emptyList())
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
      initialValue = emptyList(),
    )

  private val _aiCoachState = MutableStateFlow<AiCoachUiState>(AiCoachUiState.Idle)
  val aiCoachState: StateFlow<AiCoachUiState> = _aiCoachState.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  init {
    retryInit()
  }

  fun retryInit() {
    viewModelScope.launch {
      repository.ensureUserProgress(currentUserId, initialDisplayName)
        .onFailure { e ->
          Log.e(TAG, "Failed to initialize user progress", e)
          _statusMessage.value = e.localizedMessage ?: "Failed to sync profile"
        }
    }
  }

  fun showStatusMessage(message: String) {
    _statusMessage.value = message
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun resetAiCoach() {
    _aiCoachState.value = AiCoachUiState.Idle
  }

  fun updateCareer(careerId: String) {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    viewModelScope.launch {
      repository.updateUserProgress(current.copy(careerId = careerId))
        .onFailure { _statusMessage.value = it.localizedMessage ?: "Failed to update career track" }
    }
  }

  fun updateCefrLevel(level: String) {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    viewModelScope.launch {
      repository.updateUserProgress(current.copy(cefrLevel = level))
        .onFailure { _statusMessage.value = it.localizedMessage ?: "Failed to update level" }
    }
  }

  fun completeOnboarding() {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    val career = CurriculumData.findCareer(current.careerId)
    viewModelScope.launch {
      repository.updateUserProgress(current.copy(onboardingCompleted = true))
        .onSuccess { _statusMessage.value = "Track set: ${career.name} (${current.cefrLevel})" }
        .onFailure { _statusMessage.value = it.localizedMessage ?: "Could not save onboarding" }
    }
  }

  fun toggleArabicSupport() {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    val nextVal = !current.arabicEnabled
    viewModelScope.launch {
      repository.updateUserProgress(current.copy(arabicEnabled = nextVal))
        .onSuccess {
          _statusMessage.value = if (nextVal) "Bilingual EN / العربية enabled" else "English-only mode enabled"
        }
    }
  }

  fun completeLesson(lessonId: String = "m6_l3") {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    val updatedCompleted = (current.completedLessons + lessonId).distinct().take(50)
    val nextMinutes = (current.dailyMinutes + 5L).coerceAtMost(current.dailyGoalMinutes)
    viewModelScope.launch {
      repository.updateUserProgress(
        current.copy(
          xp = current.xp + 10L,
          dailyMinutes = nextMinutes,
          completedLessons = updatedCompleted,
        )
      )
        .onSuccess { _statusMessage.value = "Lesson Complete! +10 XP added" }
    }
  }

  fun deductHeart() {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    val nextHearts = (current.hearts - 1L).coerceAtLeast(1L)
    viewModelScope.launch {
      repository.updateUserProgress(current.copy(hearts = nextHearts))
    }
  }

  fun completePracticeSimulation() {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    if (current.createdAt == null) return
    val nextMinutes = (current.dailyMinutes + 5L).coerceAtMost(current.dailyGoalMinutes)
    viewModelScope.launch {
      repository.updateUserProgress(
        current.copy(
          xp = current.xp + 25L,
          dailyMinutes = nextMinutes,
        )
      )
        .onSuccess { _statusMessage.value = "Simulation complete! +25 XP earned" }
    }
  }

  fun toggleSaveVocab(careerId: String, item: VocabItem) {
    val existing = savedVocabState.value.find {
      it.word.equals(item.word, ignoreCase = true) && it.careerId == careerId
    }
    viewModelScope.launch {
      if (existing != null && existing.id.isNotBlank()) {
        repository.deleteSavedVocab(existing.id)
          .onSuccess { _statusMessage.value = "Removed \"${item.word}\" from saved" }
      } else {
        repository.saveVocab(
          SavedVocab(
            word = item.word,
            ipa = item.ipa,
            arabic = item.arabic,
            example = item.example,
            tip = item.tip,
            careerId = careerId,
            mastered = false,
          )
        ).onSuccess { _statusMessage.value = "Saved \"${item.word}\" to Vocabulary" }
      }
    }
  }

  fun toggleVocabMastered(vocab: SavedVocab) {
    viewModelScope.launch {
      repository.toggleVocabMastered(vocab)
    }
  }

  fun evaluateSpokenOrTypedReply(userReply: String) {
    val current = (progressState.value as? UiState.Success)?.data ?: return
    val career = CurriculumData.findCareer(current.careerId)
    val trimmed = userReply.trim()
    if (trimmed.isEmpty()) {
      _statusMessage.value = "Speak or type a short workplace response first."
      return
    }

    viewModelScope.launch {
      _aiCoachState.value = AiCoachUiState.Loading
      val result = aiCoachRepository.evaluateWorkplaceResponse(
        careerName = career.name,
        cefrLevel = current.cefrLevel,
        scenarioTitle = career.scenarioTitle,
        customerPrompt = career.scenarioMsg,
        userResponse = trimmed,
      )
      result.onSuccess { feedback ->
        _aiCoachState.value = AiCoachUiState.Success(feedback)
        repository.savePracticeSession(
          PracticeSession(
            careerId = career.id,
            scenarioTitle = career.scenarioTitle,
            userResponse = trimmed.take(1000),
            fluencyScore = feedback.fluencyScore.toLong(),
            aiFeedback = feedback.feedback.take(2000),
            suggestedPhrase = feedback.suggestedPhrase.take(500),
          )
        )
        if (current.createdAt != null) {
          repository.updateUserProgress(current.copy(xp = current.xp + 10L))
        }
      }.onFailure { err ->
        _aiCoachState.value = AiCoachUiState.Error(err.localizedMessage ?: "AI Coach evaluation failed.")
      }
    }
  }

  private companion object {
    const val TAG = "WorkEnglishVM"
    const val STOP_TIMEOUT_MILLIS = 5000L
  }
}
