package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProgress(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "Learner",
  val careerId: String = "healthcare",
  val cefrLevel: String = "A2",
  val arabicEnabled: Boolean = true,
  val onboardingCompleted: Boolean = false,
  val xp: Long = 240L,
  val streakDays: Long = 5L,
  val dailyMinutes: Long = 15L,
  val dailyGoalMinutes: Long = 20L,
  val hearts: Long = 5L,
  val completedLessons: List<String> = listOf("m6_l1", "m6_l2"),
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class SavedVocab(
  @DocumentId val id: String = "",
  val userId: String = "",
  val word: String = "",
  val ipa: String = "",
  val arabic: String = "",
  val example: String = "",
  val tip: String = "",
  val careerId: String = "",
  val mastered: Boolean = false,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class PracticeSession(
  @DocumentId val id: String = "",
  val userId: String = "",
  val careerId: String = "",
  val scenarioTitle: String = "",
  val userResponse: String = "",
  val fluencyScore: Long = 0L,
  val aiFeedback: String = "",
  val suggestedPhrase: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
)

data class VocabItem(
  val word: String,
  val ipa: String,
  val arabic: String,
  val example: String,
  val tip: String,
  val isSecondaryAccent: Boolean = false,
)

data class LessonPrompt(
  val person: String,
  val quote: String,
  val arabic: String,
  val instruction: String,
  val expectedWords: List<String>,
  val distractors: List<String>,
  val tip: String,
)

data class CareerTrack(
  val id: String,
  val name: String,
  val description: String,
  val tags: List<String>,
  val trackTitle: String,
  val heroModule: String,
  val heroLesson: String,
  val heroQuote: String,
  val scenarioTitle: String,
  val scenarioPerson: String,
  val scenarioLocation: String,
  val scenarioDesc: String,
  val scenarioPrev: String,
  val scenarioMsg: String,
  val scenarioArabic: String,
  val challengeTitle: String,
  val proTip: String,
  val vocab: List<VocabItem>,
  val lessonPrompt: LessonPrompt,
)

data class CefrLevelInfo(
  val code: String,
  val name: String,
  val arabic: String,
  val description: String,
  val focus: String,
)

data class PlacementQuestion(
  val question: String,
  val context: String,
  val options: List<PlacementOption>,
)

data class PlacementOption(
  val text: String,
  val score: Int,
)

data class PracticeChoiceOption(
  val label: String,
  val text: String,
  val isOptimal: Boolean,
  val feedbackTitle: String,
  val feedbackText: String,
)

data class AiCoachFeedback(
  val fluencyScore: Int,
  val feedback: String,
  val suggestedPhrase: String,
  val pronunciationTip: String,
)
