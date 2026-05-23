package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UserProfile

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()
    val tasks by viewModel.tasksState.collectAsState()
    val logs by viewModel.activityHistory.collectAsState()
    val streak by viewModel.streakCount.collectAsState()

    val totalCount = tasks.size
    val completedCount = tasks.count { it.isCompleted }

    var showEditDialog by remember { mutableStateOf(false) }

    when (val state = authState) {
        is AuthState.Authenticated -> {
            val user = state.profile

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
                ) {
                    // --- Avatar & bio Header ---
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .background(
                                        Brush.linearGradient(listOf(PrimaryCyan, SecondaryPurple)),
                                        CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Draw beautiful mock SVG Avatar representation
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val icon = when (user.avatarUrl) {
                                        "avatar_1" -> Icons.Default.Engineering
                                        "avatar_2" -> Icons.Default.School
                                        "avatar_3" -> Icons.Default.Computer
                                        else -> Icons.Default.Psychology
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = "Avatar design",
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(54.dp)
                                    )
                                }
                            }

                            Text(
                                text = user.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = user.email,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = user.bio,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .widthIn(max = 300.dp)
                                    .padding(horizontal = 12.dp),
                                lineHeight = 18.sp
                            )

                            // Action buttons row (Edit Profile & Logout)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { showEditDialog = true },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("edit_profile_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit details",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Info", fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Button(
                                    onClick = { viewModel.logout() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("logout_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Log out",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Logout", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // --- User Performance metrics Card ---
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MetricColumn(
                                    title = "Created",
                                    value = totalCount.toString(),
                                    icon = Icons.Default.AddCircleOutline,
                                    color = PrimaryCyan
                                )

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )

                                MetricColumn(
                                    title = "Finished",
                                    value = completedCount.toString(),
                                    icon = Icons.Default.CheckCircle,
                                    color = PriorityColorLow
                                )

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )

                                MetricColumn(
                                    title = "Streak",
                                    value = streak.toString(),
                                    icon = Icons.Default.Whatshot,
                                    color = SecondaryPurple
                                )
                            }
                        }
                    }

                    // --- Scrollable chronological operations history ---
                    item {
                        Text(
                            text = "Historic Operations Ledger",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(logs) { log ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History tick",
                                    tint = PrimaryCyan.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = log,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Edit Profile Popup controller Dialog
                if (showEditDialog) {
                    EditProfileDialog(
                        currentUser = user,
                        onDismiss = { showEditDialog = false },
                        onSave = { updatedName, updatedBio, updatedAvatar ->
                            viewModel.updateProfile(updatedName, updatedBio, updatedAvatar)
                            showEditDialog = false
                        }
                    )
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Authenticating Workspace...")
            }
        }
    }
}

@Composable
fun MetricColumn(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = title,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EditProfileDialog(
    currentUser: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var editName by remember { mutableStateOf(currentUser.name) }
    var editBio by remember { mutableStateOf(currentUser.bio) }
    var editAvatar by remember { mutableStateOf(currentUser.avatarUrl) }

    val avatarOptions = listOf(
        "avatar_1" to Icons.Default.Engineering,
        "avatar_2" to Icons.Default.School,
        "avatar_3" to Icons.Default.Computer,
        "avatar_4" to Icons.Default.Psychology
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Refine Portfolio Info", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )

                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Professional Bio") },
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bio_input")
                )

                Text("Choose Professional Avatar Tag", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    avatarOptions.forEach { option ->
                        val isSelected = editAvatar == option.first
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PrimaryCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { editAvatar = option.first }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.second,
                                contentDescription = "Avatar icon option",
                                tint = if (isSelected) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(editName, editBio, editAvatar) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_profile_btn")
            ) {
                Text("Confirm Changes", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
