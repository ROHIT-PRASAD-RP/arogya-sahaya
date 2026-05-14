package com.example.arogyasahaya3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyasahaya3.data.local.MedicationData
import com.example.arogyasahaya3.data.local.MedicationRecommendation
import com.example.arogyasahaya3.data.local.Medicine
import com.example.arogyasahaya3.data.local.UserProfile
import com.example.arogyasahaya3.ui.pillreminder.PillReminderViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uid: String,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel,
    pillReminderViewModel: PillReminderViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isEditMode by remember { mutableStateOf(false) }
    
    LaunchedEffect(uid) {
        viewModel.loadProfile(uid)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileEvent.SaveSuccess -> {
                    android.widget.Toast.makeText(context, "Profile Saved Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    isEditMode = false // Switch back to view mode after save
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Medical Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileUiState.Success -> {
                    if (isEditMode) {
                        ProfileEditContent(
                            profile = state.profile,
                            onSave = { viewModel.saveProfile(it) },
                            onCancel = { isEditMode = false },
                            onAddMedication = { recommendation ->
                                val medicine = Medicine(
                                    name = recommendation.medicineName,
                                    dosage = recommendation.dosage,
                                    time = recommendation.timing,
                                    morning = recommendation.timing.contains("Morning", ignoreCase = true),
                                    afternoon = recommendation.timing.contains("Afternoon", ignoreCase = true),
                                    night = recommendation.timing.contains("Night", ignoreCase = true) || recommendation.timing.contains("Evening", ignoreCase = true),
                                    notes = "Recommended for ${recommendation.condition}. ${recommendation.notes}"
                                )
                                pillReminderViewModel.insert(medicine)
                                android.widget.Toast.makeText(context, "${recommendation.medicineName} added to Pill Reminders", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        ProfileViewContent(
                            profile = state.profile,
                            onEditClick = { isEditMode = true },
                            onLogout = onLogout
                        )
                    }
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileViewContent(
    profile: UserProfile,
    onEditClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Edit Button in Header
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable { onEditClick() },
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.align(Alignment.Center).size(64.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    profile.name.ifBlank { "No Name Set" },
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Information Sections
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            InfoSection(title = "Personal Information") {
                InfoRow(label = "Full Name", value = profile.name, icon = Icons.Default.Badge)
                InfoRow(label = "Age", value = profile.age, icon = Icons.Default.CalendarToday)
                InfoRow(label = "Gender", value = profile.gender, icon = Icons.Default.Person)
                InfoRow(label = "Blood Group", value = profile.bloodGroup, icon = Icons.Default.WaterDrop)
            }

            InfoSection(title = "Medical Condition") {
                InfoRow(label = "Chronic Diseases", value = profile.chronicDiseases, icon = Icons.Default.MedicalServices)
            }

            InfoSection(title = "Emergency Contact") {
                InfoRow(label = "Contact Number", value = profile.emergencyContact, icon = Icons.Default.Phone)
            }

            Spacer(Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(Modifier.width(8.dp))
                Text("LOGOUT", fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun InfoSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value.ifBlank { "Not specified" }, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProfileEditContent(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onCancel: () -> Unit,
    onAddMedication: (MedicationRecommendation) -> Unit
) {
    var name by remember(profile) { mutableStateOf(profile.name) }
    var age by remember(profile) { mutableStateOf(profile.age) }
    var gender by remember(profile) { mutableStateOf(profile.gender) }
    var bloodGroup by remember(profile) { mutableStateOf(profile.bloodGroup) }
    var chronicDiseases by remember(profile) { mutableStateOf(profile.chronicDiseases) }
    var emergencyContact by remember(profile) { mutableStateOf(profile.emergencyContact) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileTextField(label = "Full Name", value = name, onValueChange = { name = it })
        ProfileTextField(label = "Age", value = age, onValueChange = { age = it })
        ProfileTextField(label = "Gender", value = gender, onValueChange = { gender = it })
        ProfileTextField(label = "Blood Group", value = bloodGroup, onValueChange = { bloodGroup = it })
        ProfileTextField(label = "Chronic Diseases", value = chronicDiseases, onValueChange = { chronicDiseases = it })
        
        // Dynamic Recommendations
        val matchedRecommendations = MedicationData.recommendations.filter { 
            chronicDiseases.contains(it.condition, ignoreCase = true) 
        }

        if (matchedRecommendations.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Recommended Medications",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                matchedRecommendations.forEach { rec ->
                    RecommendationCard(
                        recommendation = rec,
                        onClick = { onAddMedication(rec) }
                    )
                }
                
                // Medical Disclaimer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "DISCLAIMER: These recommendations are for educational purposes. Always consult your doctor before taking any medication.",
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        ProfileTextField(label = "Emergency Contact", value = emergencyContact, onValueChange = { emergencyContact = it })

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("CANCEL")
            }
            Button(
                onClick = {
                    onSave(profile.copy(
                        name = name,
                        age = age,
                        gender = gender,
                        bloodGroup = bloodGroup,
                        chronicDiseases = chronicDiseases,
                        emergencyContact = emergencyContact
                    ))
                },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("SAVE PROFILE", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: MedicationRecommendation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recommendation.medicineName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text("Condition: ${recommendation.condition}", fontSize = 14.sp)
            Text("Dose: ${recommendation.dosage}", fontWeight = FontWeight.SemiBold)
            Text("Timing: ${recommendation.timing}", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.notes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 20.sp) },
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
