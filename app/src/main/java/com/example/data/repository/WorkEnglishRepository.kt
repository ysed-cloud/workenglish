package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.PracticeSession
import com.example.data.model.SavedVocab
import com.example.data.model.UserProgress
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class WorkEnglishRepository(
  private val db: FirebaseFirestore,
  private val auth: FirebaseAuth = Firebase.auth,
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    ),
    Firebase.auth,
  )

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  fun observeUserProgress(userId: String): Flow<UserProgress?> {
    val docRef = db.collection("users").document(userId)
    return docRef.snapshots()
      .map { snapshot ->
        if (snapshot.exists()) {
          snapshot.toObject(UserProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } else {
          null
        }
      }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
          handleFirestoreError(exceptionToLog, OperationType.GET, docRef.path)
        }
        throw error
      }
  }

  suspend fun ensureUserProgress(userId: String, displayName: String): Result<UserProgress> {
    val uid = requireUserId()
    val docRef = db.collection("users").document(userId)
    return try {
      val existing = docRef.get().await()
      if (existing.exists()) {
        val loaded = existing.toObject(
          UserProgress::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        ) ?: UserProgress(id = userId, userId = uid, displayName = displayName)
        Result.success(loaded)
      } else {
        val safeName = displayName.ifBlank { "Sarah" }.take(80)
        val payload = mapOf(
          "userId" to uid,
          "displayName" to safeName,
          "careerId" to "healthcare",
          "cefrLevel" to "A2",
          "arabicEnabled" to true,
          "onboardingCompleted" to false,
          "xp" to 240L,
          "streakDays" to 5L,
          "dailyMinutes" to 15L,
          "dailyGoalMinutes" to 20L,
          "hearts" to 5L,
          "completedLessons" to listOf("m6_l1", "m6_l2"),
          "createdAt" to FieldValue.serverTimestamp(),
          "updatedAt" to FieldValue.serverTimestamp(),
        )
        docRef.set(payload).await()
        val created = docRef.get().await().toObject(
          UserProgress::class.java,
          DocumentSnapshot.ServerTimestampBehavior.ESTIMATE,
        ) ?: UserProgress(id = userId, userId = uid, displayName = safeName)
        Result.success(created)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.WRITE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun updateUserProgress(progress: UserProgress): Result<Unit> {
    val uid = requireUserId()
    val docId = progress.id.ifBlank { uid }
    val docRef = db.collection("users").document(docId)
    return try {
      requireNotNull(progress.createdAt) { "UserProgress createdAt must not be null for update" }
      docRef.set(progress.copy(userId = uid, updatedAt = null)).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  fun observeSavedVocab(userId: String): Flow<List<SavedVocab>> {
    val colRef = db.collection("users").document(userId).collection("saved_vocab")
    return colRef.snapshots()
      .map { snapshot ->
        snapshot.toObjects(SavedVocab::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
          .sortedByDescending { it.createdAt?.seconds ?: 0L }
      }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
          handleFirestoreError(exceptionToLog, OperationType.LIST, colRef.path)
        }
        throw error
      }
  }

  suspend fun saveVocab(vocab: SavedVocab): Result<String> {
    val uid = requireUserId()
    val sanitizedWord = vocab.word.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    val docId = vocab.id.ifBlank { "${vocab.careerId}_${sanitizedWord}".take(100) }
    val docRef = db.collection("users").document(uid).collection("saved_vocab").document(docId)
    return try {
      val payload = mapOf(
        "userId" to uid,
        "word" to vocab.word,
        "ipa" to vocab.ipa,
        "arabic" to vocab.arabic,
        "example" to vocab.example,
        "tip" to vocab.tip,
        "careerId" to vocab.careerId,
        "mastered" to vocab.mastered,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
      )
      docRef.set(payload).await()
      Result.success(docId)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun toggleVocabMastered(vocab: SavedVocab): Result<Unit> {
    val uid = requireUserId()
    require(vocab.id.isNotBlank()) { "Vocab ID must not be blank for update" }
    requireNotNull(vocab.createdAt) { "Vocab createdAt must not be null for update" }
    val docRef = db.collection("users").document(uid).collection("saved_vocab").document(vocab.id)
    return try {
      docRef.set(vocab.copy(userId = uid, mastered = !vocab.mastered, updatedAt = null)).await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  suspend fun deleteSavedVocab(vocabId: String): Result<Unit> {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("saved_vocab").document(vocabId)
    return try {
      docRef.delete().await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      Result.failure(e)
    }
  }

  fun observePracticeSessions(userId: String): Flow<List<PracticeSession>> {
    val colRef = db.collection("users").document(userId).collection("practice_sessions")
    return colRef.orderBy("createdAt", Query.Direction.DESCENDING)
      .limit(10)
      .snapshots()
      .map { snapshot ->
        snapshot.toObjects(PracticeSession::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
      }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == userId) {
          handleFirestoreError(exceptionToLog, OperationType.LIST, colRef.path)
        }
        throw error
      }
  }

  suspend fun savePracticeSession(session: PracticeSession): Result<String> {
    val uid = requireUserId()
    val docId = session.id.ifBlank { UUID.randomUUID().toString() }
    val docRef = db.collection("users").document(uid).collection("practice_sessions").document(docId)
    return try {
      val payload = mapOf(
        "userId" to uid,
        "careerId" to session.careerId,
        "scenarioTitle" to session.scenarioTitle,
        "userResponse" to session.userResponse,
        "fluencyScore" to session.fluencyScore,
        "aiFeedback" to session.aiFeedback,
        "suggestedPhrase" to session.suggestedPhrase,
        "createdAt" to FieldValue.serverTimestamp(),
      )
      docRef.set(payload).await()
      Result.success(docId)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      Result.failure(e)
    }
  }
}
