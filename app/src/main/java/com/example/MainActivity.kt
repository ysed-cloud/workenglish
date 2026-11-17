package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.firebase.configureAppCheck
import com.example.data.model.UserProgress
import com.example.data.repository.AiCoachRepository
import com.example.data.repository.WorkEnglishRepository
import com.example.ui.UiState
import com.example.ui.WorkEnglishViewModel
import com.example.ui.auth.authStateFlow
import com.example.ui.components.WorkEnglishTopBar
import com.example.ui.screens.AuthWelcomeScreen
import com.example.ui.screens.CareerSelectionScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.InteractiveLessonScreen
import com.example.ui.screens.LearnPathScreen
import com.example.ui.screens.LevelSelectionScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VocabularyScreen
import com.example.ui.theme.WorkEnglishTheme
import com.example.util.rememberSimpleTts
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private enum class MainTab(
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector,
) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
  LEARN("Learn", Icons.Filled.School, Icons.Outlined.School),
  PRACTICE("Practice", Icons.Filled.Mic, Icons.Outlined.MicNone),
  VOCABULARY("Vocabulary", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
  PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline),
}

private enum class ActiveScreen {
  MAIN_SHELL,
  CAREER_PICKER,
  LEVEL_PICKER,
  LESSON,
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // STEP 1: Configure Firebase App Check with launch intent before any UI or ViewModels
    configureAppCheck(this, intent)

    enableEdgeToEdge()
    setContent {
      WorkEnglishTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
          WorkEnglishRoot()
        }
      }
    }
  }
}

@Composable
fun WorkEnglishRoot(auth: FirebaseAuth = Firebase.auth) {
  val speak = rememberSimpleTts()
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser

  if (user == null) {
    AuthWelcomeScreen(
      onSpeakPhrase = speak,
      onAuthSuccess = {},
    )
  } else {
    val uid = user.uid
    val displayName = user.displayName?.takeIf { it.isNotBlank() } ?: "Sarah"
    key(uid) {
      val viewModel: WorkEnglishViewModel = viewModel(
        key = uid,
        factory = viewModelFactory {
          initializer {
            val app = checkNotNull(this[APPLICATION_KEY])
            val databaseId = app.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(databaseId)
            WorkEnglishViewModel(
              repository = WorkEnglishRepository(db, auth),
              aiCoachRepository = AiCoachRepository(),
              currentUserId = uid,
              initialDisplayName = displayName,
            )
          }
        },
      )
      AuthenticatedWorkEnglishApp(
        viewModel = viewModel,
        onSpeak = speak,
      )
    }
  }
}

@Composable
private fun AuthenticatedWorkEnglishApp(
  viewModel: WorkEnglishViewModel,
  onSpeak: (String) -> Unit,
) {
  val progressState by viewModel.progressState.collectAsStateWithLifecycle()
  val savedVocab by viewModel.savedVocabState.collectAsStateWithLifecycle()
  val recentSessions by viewModel.practiceSessionsState.collectAsStateWithLifecycle()
  val aiCoachState by viewModel.aiCoachState.collectAsStateWithLifecycle()
  val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(statusMessage) {
    val msg = statusMessage
    if (!msg.isNullOrBlank()) {
      snackbarHostState.showSnackbar(msg)
      viewModel.clearStatusMessage()
    }
  }

  when (val state = progressState) {
    UiState.Loading -> {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CircularProgressIndicator()
          Text("Loading your career track...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
    is UiState.Error -> {
      Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Unable to sync your progress", style = MaterialTheme.typography.headlineSmall)
          Text(state.message, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
          Button(onClick = { viewModel.retryInit() }) {
            Text("Retry")
          }
        }
      }
    }
    is UiState.Success -> {
      val progress = state.data
      val initials = remember(progress.displayName) {
        progress.displayName
          .split(" ")
          .filter { it.isNotBlank() }
          .take(2)
          .joinToString("") { it.first().uppercase() }
          .ifBlank { "WE" }
      }

      var activeScreen by rememberSaveable {
        mutableStateOf(if (progress.onboardingCompleted) ActiveScreen.MAIN_SHELL else ActiveScreen.CAREER_PICKER)
      }
      var activeTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

      when (activeScreen) {
        ActiveScreen.CAREER_PICKER -> {
          CareerSelectionScreen(
            selectedCareerId = progress.careerId,
            initials = initials,
            onSelectCareer = { viewModel.updateCareer(it) },
            onContinue = { activeScreen = ActiveScreen.LEVEL_PICKER },
            onBack = {
              if (progress.onboardingCompleted) activeScreen = ActiveScreen.MAIN_SHELL
            },
          )
        }
        ActiveScreen.LEVEL_PICKER -> {
          LevelSelectionScreen(
            selectedLevel = progress.cefrLevel,
            arabicEnabled = progress.arabicEnabled,
            initials = initials,
            onSelectLevel = { viewModel.updateCefrLevel(it) },
            onConfirm = {
              viewModel.completeOnboarding()
              activeScreen = ActiveScreen.MAIN_SHELL
              activeTab = MainTab.HOME
            },
            onBack = { activeScreen = ActiveScreen.CAREER_PICKER },
          )
        }
        ActiveScreen.LESSON -> {
          Box(modifier = Modifier.fillMaxSize()) {
            InteractiveLessonScreen(
              progress = progress,
              initials = initials,
              onDeductHeart = { viewModel.deductHeart() },
              onLessonCompleted = { viewModel.completeLesson("m6_l3") },
              onSpeak = onSpeak,
              onShowMessage = { msg -> viewModel.showStatusMessage(msg) },
              onExit = {
                activeScreen = ActiveScreen.MAIN_SHELL
                activeTab = MainTab.LEARN
              },
            )
            SnackbarHost(
              hostState = snackbarHostState,
              modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
          }
        }
        ActiveScreen.MAIN_SHELL -> {
          MainTabScaffold(
            progress = progress,
            initials = initials,
            activeTab = activeTab,
            onTabSelected = { activeTab = it },
            snackbarHostState = snackbarHostState,
            content = { innerPadding ->
              when (activeTab) {
                MainTab.HOME -> HomeDashboardScreen(
                  progress = progress,
                  savedVocab = savedVocab,
                  onSwitchCareer = { activeScreen = ActiveScreen.CAREER_PICKER },
                  onStartLesson = { activeScreen = ActiveScreen.LESSON },
                  onOpenLearn = { activeTab = MainTab.LEARN },
                  onOpenPractice = { activeTab = MainTab.PRACTICE },
                  onOpenVocabulary = { activeTab = MainTab.VOCABULARY },
                  onToggleSaveVocab = { cId, item -> viewModel.toggleSaveVocab(cId, item) },
                  onSpeak = onSpeak,
                  contentPadding = innerPadding,
                )
                MainTab.LEARN -> LearnPathScreen(
                  progress = progress,
                  onSwitchCareer = { activeScreen = ActiveScreen.CAREER_PICKER },
                  onStartLesson = { activeScreen = ActiveScreen.LESSON },
                  contentPadding = innerPadding,
                )
                MainTab.PRACTICE -> PracticeScreen(
                  progress = progress,
                  aiCoachState = aiCoachState,
                  recentSessions = recentSessions,
                  onEvaluateReply = { reply -> viewModel.evaluateSpokenOrTypedReply(reply) },
                  onCompleteSimulation = {
                    viewModel.completePracticeSimulation()
                    viewModel.resetAiCoach()
                  },
                  onBackHome = { activeTab = MainTab.HOME },
                  onSpeak = onSpeak,
                  onShowMessage = { msg -> viewModel.showStatusMessage(msg) },
                  contentPadding = innerPadding,
                )
                MainTab.VOCABULARY -> VocabularyScreen(
                  progress = progress,
                  savedVocab = savedVocab,
                  onToggleSaveVocab = { cId, item -> viewModel.toggleSaveVocab(cId, item) },
                  onToggleMastered = { viewModel.toggleVocabMastered(it) },
                  onSpeak = onSpeak,
                  contentPadding = innerPadding,
                )
                MainTab.PROFILE -> ProfileScreen(
                  progress = progress,
                  initials = initials,
                  onChangeCareer = { activeScreen = ActiveScreen.CAREER_PICKER },
                  onChangeLevel = { activeScreen = ActiveScreen.LEVEL_PICKER },
                  onToggleArabic = { viewModel.toggleArabicSupport() },
                  onSignedOut = {},
                  contentPadding = innerPadding,
                )
              }
            },
          )
        }
      }
    }
  }
}

@Composable
private fun MainTabScaffold(
  progress: UserProgress,
  initials: String,
  activeTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
  snackbarHostState: SnackbarHostState,
  content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isExpanded = maxWidth >= 700.dp

    Row(modifier = Modifier.fillMaxSize()) {
    if (isExpanded) {
      NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxHeight(),
      ) {
        Spacer(modifier = Modifier.height(16.dp))
        MainTab.entries.forEach { tab ->
          val selected = activeTab == tab
          NavigationRailItem(
            selected = selected,
            onClick = { onTabSelected(tab) },
            icon = {
              Icon(
                imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                contentDescription = tab.label,
              )
            },
            label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("nav_rail_${tab.name.lowercase()}"),
          )
        }
      }
    }

    Scaffold(
      topBar = {
        WorkEnglishTopBar(
          tabLabel = activeTab.label,
          streakDays = progress.streakDays,
          initials = initials,
          onAvatarClick = { onTabSelected(MainTab.PROFILE) },
        )
      },
      bottomBar = {
        if (!isExpanded) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            windowInsets = WindowInsets.navigationBars,
          ) {
            MainTab.entries.forEach { tab ->
              val selected = activeTab == tab
              NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                  Icon(
                    imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                    contentDescription = tab.label,
                  )
                },
                label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
              )
            }
          }
        }
      },
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      content = content,
    )
    }
  }
}
