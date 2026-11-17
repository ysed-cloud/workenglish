package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.PracticeSession
import com.example.data.model.SavedVocab
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class WorkEnglishRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun ensureAndUpdateUserProgress_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = WorkEnglishRepository(firestore, auth)

    val created = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.ensureUserProgress(uid, "Sarah").getOrThrow()
    }
    assertEquals(uid, created.userId)
    assertNotNull(created.createdAt)

    val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.updateUserProgress(created.copy(careerId = "tech", cefrLevel = "B1", xp = 260L))
    }
    assertTrue(updateResult.isSuccess)

    val observed = withTimeout(FLOW_TIMEOUT_MS) {
      repo.observeUserProgress(uid).first { it != null && it.careerId == "tech" }
    }
    assertEquals("B1", observed?.cefrLevel)
    assertEquals(260L, observed?.xp)
  }

  @Test
  fun saveVocab_authenticatedOwner_createsAndObservesTerm() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = WorkEnglishRepository(firestore, auth)
    val vocab = SavedVocab(
      id = "dosage_${UUID.randomUUID().toString().take(8)}",
      word = "Dosage",
      ipa = "/ˈdoʊ.sɪdʒ/",
      arabic = "جرعة دوائية",
      example = "Take this dosage twice daily with food.",
      tip = "Soft g sound like j",
      careerId = "healthcare",
      mastered = false,
    )

    val savedId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.saveVocab(vocab).getOrThrow()
    }
    val list = withTimeout(FLOW_TIMEOUT_MS) {
      repo.observeSavedVocab(uid).first { items -> items.any { it.id == savedId } }
    }
    assertTrue(list.any { it.id == savedId && it.word == "Dosage" })
  }

  @Test
  fun savePracticeSession_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = WorkEnglishRepository(firestore, auth)
    val session = PracticeSession(
      id = "sess_${UUID.randomUUID().toString().take(8)}",
      careerId = "healthcare",
      scenarioTitle = "Outpatient Clinic Intake",
      userResponse = "I understand this is stressful, let me check with the lab.",
      fluencyScore = 92L,
      aiFeedback = "Clear and empathetic.",
      suggestedPhrase = "I understand the wait is stressful; let me check right away.",
    )
    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.savePracticeSession(session).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobRepo = WorkEnglishRepository(firestore, auth)
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        bobRepo.observeSavedVocab(aliceUid).first()
      }
      fail("Expected PERMISSION_DENIED for cross-user access")
    } catch (e: Exception) {
      val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
        "Expected nested FirebaseFirestoreException, got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }
  }

  @Test
  fun observeUserProgress_unauthenticated_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = WorkEnglishRepository(firestore, auth)
    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeUserProgress("alice_unauth").first()
      }
      fail("Expected PERMISSION_DENIED for unauthenticated caller")
    } catch (e: Exception) {
      val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
        "Expected nested FirebaseFirestoreException, got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice_workenglish@test.com"
    const val BOB_EMAIL = "bob_workenglish@test.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 3000L
  }
}
