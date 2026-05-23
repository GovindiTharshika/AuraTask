package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.model.Task
import com.example.data.repository.TaskRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room persistence database and repositories cleanly
        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao())
        val factory = MainViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val authState by viewModel.authState.collectAsState()
                var showSplash by remember { mutableStateOf(true) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                        },
                        label = "splash_to_main_transition"
                    ) { splashActive ->
                        if (splashActive) {
                            SplashScreen(onTimeout = { showSplash = false })
                        } else {
                            AnimatedContent(
                                targetState = authState,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(450)) togetherWith fadeOut(animationSpec = tween(450))
                                },
                                label = "auth_screen_transition"
                            ) { state ->
                                if (state is AuthState.Authenticated) {
                                    // User is fully authenticated, display high-fidelity app workspace
                                    MainAppWorkspace(viewModel = viewModel)
                                } else {
                                    // Show authentication form / Google credentials portal
                                    AuthScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppWorkspace(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_bottom_nav_bar")
            ) {
                // Navigation item: Dashboard
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Workspace") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Workspace insights dashboard"
                        )
                    },
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                // Navigation item: Tasks
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Tasks") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.List else Icons.Outlined.List,
                            contentDescription = "Standard organized task sheets"
                        )
                    },
                    modifier = Modifier.testTag("nav_tab_tasks")
                )

                // Navigation item: AI Coach
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Aura ML") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Default.OfflineBolt else Icons.Outlined.OfflineBolt,
                            contentDescription = "AI intelligence coach agent"
                        )
                    },
                    modifier = Modifier.testTag("nav_tab_ai_coach")
                )

                // Navigation item: Profile
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Profile") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Developer credentials portfolio"
                        )
                    },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Display chosen Screen according to tab position
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                },
                label = "workspace_tab_transitions"
            ) { tab ->
                when (tab) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTasks = { selectedTab = 1 },
                        onNavigateToAiCoach = { selectedTab = 2 }
                    )
                    1 -> TasksScreen(
                        viewModel = viewModel,
                        onAddNewTaskClick = {
                            taskToEdit = null
                            showTaskDialog = true
                        },
                        onEditTaskClick = { task ->
                            taskToEdit = task
                            showTaskDialog = true
                        }
                    )
                    2 -> AiCoachScreen(viewModel = viewModel)
                    3 -> ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Dynamic Dialog for task additions and refinements
    if (showTaskDialog) {
        AddEditTaskView(
            taskToEdit = taskToEdit,
            onDismiss = {
                showTaskDialog = false
                taskToEdit = null
            },
            onSave = { title, desc, cat, prio, date ->
                if (taskToEdit == null) {
                    viewModel.addTask(title, desc, cat, prio, date)
                } else {
                    viewModel.updateTask(taskToEdit!!.copy(
                        title = title,
                        description = desc,
                        category = cat,
                        priority = prio,
                        deadline = date.time
                    ))
                }
                showTaskDialog = false
                taskToEdit = null
            }
        )
    }
}
