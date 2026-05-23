package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Task
import com.example.data.network.Content
import com.example.data.network.GeminiRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.Part
import com.example.data.network.RetrofitClient
import com.example.data.repository.TaskRepository
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class UserProfile(
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@edu.dev",
    val bio: String = "Software Engineering Intern | Tech Enthusiast trying to optimize daily workflow.",
    val avatarUrl: String = "avatar_3" // avatar key
)

sealed interface AuthState {
    object Unauthenticated : AuthState
    object Authenticating : AuthState
    data class Authenticated(val profile: UserProfile) : AuthState
    data class AuthError(val error: String) : AuthState
}

sealed interface AIState {
    object Idle : AIState
    object Loading : AIState
    data class Success(
        val schedule: List<Task>,
        val tip: String,
        val insight: String,
        val isOfflineFallback: Boolean
    ) : AIState
    data class Error(val error: String) : AIState
}

class MainViewModel(
    application: Application,
    private val repository: TaskRepository
) : AndroidViewModel(application) {

    // --- Authentication State ---
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated(UserProfile()))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // --- Search & Filtering States ---
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All") // "All", "Work", "Personal", "Study", "Fitness"
    val selectedPriority = MutableStateFlow("All") // "All", "High", "Medium", "Low"

    // --- Tasks State Flow ---
    val tasksState: StateFlow<List<Task>> = repository.allTasks
        .combine(searchQuery) { list, query ->
            if (query.isEmpty()) list else list.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        }
        .combine(selectedCategory) { list, category ->
            if (category == "All") list else list.filter { it.category.equals(category, ignoreCase = true) }
        }
        .combine(selectedPriority) { list, priority ->
            if (priority == "All") list else list.filter { it.priority.equals(priority, ignoreCase = true) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Productivity Metrics & Streak States ---
    private val _streakCount = MutableStateFlow(3) // Initial mock streak
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _activityHistory = MutableStateFlow<List<String>>(
        listOf(
            "Created task 'Optimize Database Schema' in Work",
            "Completed task 'Revise Operating Systems Unit 4' in Study",
            "Completed task '30 Min Hiit Workout' in Fitness",
            "Generated smart schedule using AuraTask AI Assistant"
        )
    )
    val activityHistory: StateFlow<List<String>> = _activityHistory.asStateFlow()

    // --- AI productivity assistant states ---
    private val _aiState = MutableStateFlow<AIState>(AIState.Idle)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()

    init {
        // Compute streak dynamically if we wanted to (or keep tracking client transactions)
        viewModelScope.launch {
            repository.allTasks.collect { list ->
                val completedCount = list.count { it.isCompleted }
                if (completedCount > 0 && _streakCount.value == 0) {
                    _streakCount.value = 1
                }
            }
        }
    }

    // --- Authentication Operators ---
    fun register(name: String, email: String, pword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            kotlinx.coroutines.delay(1000) // Beautiful authentic experience delay
            if (!email.contains("@")) {
                _authState.value = AuthState.AuthError("Please enter a valid email address.")
                return@launch
            }
            if (pword.length < 6) {
                _authState.value = AuthState.AuthError("Password must be at least 6 characters.")
                return@launch
            }
            val profile = UserProfile(name = name, email = email, bio = "Getting customized tips and task organizing with AI!")
            _authState.value = AuthState.Authenticated(profile)
            logActivity("User registered & logged in: $name")
        }
    }

    fun login(email: String, pword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            kotlinx.coroutines.delay(1000)
            if (!email.contains("@")) {
                _authState.value = AuthState.AuthError("Please enter a valid email address.")
                return@launch
            }
            if (pword.isEmpty()) {
                _authState.value = AuthState.AuthError("Please enter your password.")
                return@launch
            }
            val name = email.substringBefore("@").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val profile = UserProfile(name = name, email = email)
            _authState.value = AuthState.Authenticated(profile)
            logActivity("Logged in as $name")
        }
    }

    fun loginGoogle() {
        viewModelScope.launch {
            _authState.value = AuthState.Authenticating
            kotlinx.coroutines.delay(1200)
            val profile = UserProfile(
                name = "Google User",
                email = "user.google@gmail.com",
                bio = "Signed in with Google Identity Service securely."
            )
            _authState.value = AuthState.Authenticated(profile)
            logActivity("Signed in with Google Account")
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
        logActivity("Logged out of the system")
    }

    fun updateProfile(name: String, bio: String, avatarUrl: String) {
        val current = _authState.value
        if (current is AuthState.Authenticated) {
            val updated = current.profile.copy(name = name, bio = bio, avatarUrl = avatarUrl)
            _authState.value = AuthState.Authenticated(updated)
            logActivity("Updated profile details")
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!email.contains("@")) {
            onError("Please enter a valid email address.")
            return
        }
        // Mock success
        onSuccess()
        logActivity("Password reset link sent to $email")
    }

    // --- Task Operations ---
    fun addTask(title: String, desc: String, cat: String, prio: String, deadlineDate: Date) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = desc,
                category = cat,
                priority = prio,
                deadline = deadlineDate.time
            )
            repository.insert(newTask)
            logActivity("Added task '$title' in $cat")
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.update(updated)
            if (updated.isCompleted) {
                logActivity("Completed task '${task.title}'")
                // Increase streak if completed today
                _streakCount.value += 1
            } else {
                logActivity("Re-opened task '${task.title}'")
                if (_streakCount.value > 1) _streakCount.value -= 1
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
            logActivity("Deleted task '${task.title}'")
        }
    }

    // --- AI Assistant Operations ---
    fun generateAiSchedule() {
        viewModelScope.launch {
            _aiState.value = AIState.Loading
            val currentTasks = tasksState.value.filter { !it.isCompleted }

            if (currentTasks.isEmpty()) {
                _aiState.value = AIState.Success(
                    schedule = emptyList(),
                    tip = "Keep your task list tidy! Add a few tasks first, then let AuraTask AI arrange them according to priority and difficulty.",
                    insight = "A clean slate is great! Focus on planning your week ahead with high-priority goals.",
                    isOfflineFallback = true
                )
                return@launch
            }

            // Check if key is available:
            val hasKey = RetrofitClient.hasValidApiKey()
            if (!hasKey) {
                // Return beautiful Offline Heuristics Fallback after short aesthetic delay
                kotlinx.coroutines.delay(1500)
                generateSmartLocalFallback(currentTasks)
                return@launch
            }

            // AI REST Call
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                val taskDescriptions = currentTasks.joinToString("\n") { task ->
                    "- ${task.title} (Category: ${task.category}, Priority: ${task.priority})"
                }

                val prompt = """
                    You are AuraTask ML, an expert Software Engineering Intern Productivity Coach.
                    I want you to analyze my task and productivity load, then return a response using structured JSON formatting.
                    
                    Tasks:
                    $taskDescriptions
                    
                    Return a JSON object exactly matching the following format:
                    {
                      "scheduledTasks": [
                         {
                           "title": "exact title of task 1",
                           "time": "09:00 AM",
                           "reasoning": "custom reasoning why it should be done at this time"
                         }
                      ],
                      "productivityTip": "A customized productivity tip of the day based on my tasks",
                      "assistantInsight": "General executive summary of their energy level, recommending how to tackle work."
                    }
                    
                    Be professional, brief, encouraging, and output valid JSON.
                """.trimIndent()

                val requestBody = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.7f, responseMimeType = "application/json")
                )

                val response = RetrofitClient.apiService.generateContent(apiKey, requestBody)
                val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (responseText != null) {
                    // Parse text response using Moshi or simple regex since we want a very sturdy parse
                    parseAiResponse(responseText, currentTasks)
                } else {
                    generateSmartLocalFallback(currentTasks, true)
                }
            } catch (e: Exception) {
                // Fallback on exception
                generateSmartLocalFallback(currentTasks, true)
            }
        }
    }

    private fun parseAiResponse(jsonText: String, originalTasks: List<Task>) {
        try {
            // Since AI response may have markdown code fences like ```json ... ```, let's extract
            val cleaned = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            // Constructing clean Moshi parse for the returned schema
            val schedulerMoshi = com.squareup.moshi.Moshi.Builder()
                .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val adapter = schedulerMoshi.adapter(AiResponseMap::class.java)
            val parsed = adapter.fromJson(cleaned)

            if (parsed != null) {
                val updatedTasks = originalTasks.map { task ->
                    val scheduling = parsed.scheduledTasks.find { it.title.equals(task.title, ignoreCase = true) }
                    task.copy(
                        aiScheduledTime = scheduling?.time ?: "11:00 AM",
                        aiReasoning = scheduling?.reasoning ?: "Allocated based on general cognitive load."
                    )
                }.sortedBy { getTaskSortValue(it.aiScheduledTime) }

                _aiState.value = AIState.Success(
                    schedule = updatedTasks,
                    tip = parsed.productivityTip,
                    insight = parsed.assistantInsight,
                    isOfflineFallback = false
                )
                logActivity("AI Daily Schedule configured by AuraTask Chef.")
            } else {
                generateSmartLocalFallback(originalTasks, true)
            }
        } catch (e: Exception) {
            generateSmartLocalFallback(originalTasks, true)
        }
    }

    private fun generateSmartLocalFallback(currentTasks: List<Task>, isFromException: Boolean = false) {
        // Generates brilliant local recommendations if API not setup
        val prioritizedTasks = currentTasks.sortedWith(compareBy(
            { when(it.priority.lowercase(Locale.getDefault())) { "high" -> 0; "medium" -> 1; else -> 2 } },
            { it.deadline }
        ))

        val scheduleTimes = listOf("09:00 AM", "11:00 AM", "01:30 PM", "03:30 PM", "05:00 PM")
        val scheduledList = prioritizedTasks.mapIndexed { index, task ->
            val time = scheduleTimes.getOrElse(index) { "06:30 PM" }
            val priorityReasoning = when(task.priority.lowercase(Locale.getDefault())) {
                "high" -> "High priority task scheduled early to leverage peak cognitive energy levels (Eat the Frog!)."
                "medium" -> "Mid-level priority structured following primary focus blocks for steady momentum."
                else -> "Low-demand activity allocated to the afternoon slump, ensuring low friction and easy completion."
            }
            task.copy(
                aiScheduledTime = time,
                aiReasoning = priorityReasoning
            )
        }

        val mockTips = listOf(
            "Technique: Use the 80/20 Rule. 80% of outcomes result from 20% of effort. Focus heavily on your high priority tasks before moving to emails or logs.",
            "Concept: The Pomodoro Technique. Plan to split heavy work (such as '${prioritizedTasks.firstOrNull()?.title ?: "important work"}') into 25-minute sprints followed by 5-minute walks.",
            "Framework: Time-boxing. Set dedicated non-interruptible blocks of 1.5 hours in your calendar for your most critical goals."
        )

        _aiState.value = AIState.Success(
            schedule = scheduledList,
            tip = mockTips.random(),
            insight = "Smart Smart Scheduler (Running Local Heuristics Logic). Organize tasks starting with high focus work first.",
            isOfflineFallback = true
        )
    }

    private fun getTaskSortValue(time: String?): Int {
        if (time == null) return 999
        return try {
            val format = SimpleDateFormat("hh:mm a", Locale.US)
            val date = format.parse(time)
            ((date?.hours ?: 12) * 60) + (date?.minutes ?: 0)
        } catch (e: Exception) {
            999
        }
    }

    private fun logActivity(message: String) {
        val currentList = _activityHistory.value.toMutableList()
        currentList.add(0, message)
        if (currentList.size > 20) {
            currentList.removeLast()
        }
        _activityHistory.value = currentList
    }
}

@JsonClass(generateAdapter = true)
data class AiResponseMap(
    val scheduledTasks: List<AiScheduledTask>,
    val productivityTip: String,
    val assistantInsight: String
)

@JsonClass(generateAdapter = true)
data class AiScheduledTask(
    val title: String,
    val time: String,
    val reasoning: String
)

class MainViewModelFactory(
    private val application: Application,
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
