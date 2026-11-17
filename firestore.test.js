const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

function pastTimestamp() {
  return new Date(Date.now() - 2000);
}

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const DATABASE_ID = "ai-studio-android-workengl-11a9a651-a757-41ad-9028-ace7ec75958a";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validUserProgress(uid) {
  return {
    userId: uid,
    displayName: "Sarah",
    careerId: "healthcare",
    cefrLevel: "A2",
    arabicEnabled: true,
    onboardingCompleted: true,
    xp: 240,
    streakDays: 5,
    dailyMinutes: 15,
    dailyGoalMinutes: 20,
    hearts: 5,
    completedLessons: ["m6_l1", "m6_l2"],
    createdAt: pastTimestamp(),
    updatedAt: pastTimestamp(),
  };
}

function validVocab(uid) {
  return {
    userId: uid,
    word: "Dosage",
    ipa: "/ˈdoʊ.sɪdʒ/",
    arabic: "جرعة دوائية",
    example: "Take this dosage twice daily with food.",
    tip: "Soft g sound like j",
    careerId: "healthcare",
    mastered: false,
    createdAt: pastTimestamp(),
    updatedAt: pastTimestamp(),
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write user progress", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validUserProgress(ALICE_UID)));
});

test("Authenticated user can create and read own progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(validUserProgress(ALICE_UID)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user cannot read or write another user's progress", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore(DATABASE_ID);
  await assertSucceeds(bobDb.collection("users").doc(BOB_UID).set(validUserProgress(BOB_UID)));

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).set(validUserProgress(BOB_UID)));
});

test("Shadow update with ghost field or invalid careerId is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
  const badPayload = { ...validUserProgress(ALICE_UID), isAdmin: true };
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set(badPayload));

  const badCareer = { ...validUserProgress(ALICE_UID), careerId: "astronaut" };
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).set(badCareer));
});

test("Authenticated user can manage own saved_vocab but not others", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("saved_vocab").doc("dosage").set(validVocab(ALICE_UID))
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("saved_vocab").get()
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore(DATABASE_ID);
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("saved_vocab").get()
  );
});

test("Authenticated user can create practice_sessions and cannot update immutable log", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore(DATABASE_ID);
  const sessionRef = aliceDb.collection("users").doc(ALICE_UID).collection("practice_sessions").doc("s_1");
  await assertSucceeds(
    sessionRef.set({
      userId: ALICE_UID,
      careerId: "healthcare",
      scenarioTitle: "Outpatient Clinic Intake",
      userResponse: "I understand this is stressful, let me check with the lab right away.",
      fluencyScore: 92,
      aiFeedback: "Polite and empathetic phrasing.",
      suggestedPhrase: "I understand the wait is stressful; let me check with the lab right away.",
      createdAt: pastTimestamp(),
    })
  );
  await assertFails(sessionRef.update({ fluencyScore: 100 }));
});
