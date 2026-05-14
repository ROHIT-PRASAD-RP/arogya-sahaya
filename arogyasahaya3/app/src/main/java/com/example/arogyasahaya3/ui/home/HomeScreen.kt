package com.example.arogyasahaya3.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyasahaya3.ui.auth.AuthViewModel
import com.example.arogyasahaya3.ui.profile.ProfileUiState
import com.example.arogyasahaya3.ui.profile.ProfileViewModel
import com.example.arogyasahaya3.ui.vitals.VitalsViewModel
import com.example.arogyasahaya3.ui.pillreminder.PillReminderViewModel

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    vitalsViewModel: VitalsViewModel,
    pillReminderViewModel: PillReminderViewModel,
    onNavigateToPillReminder: () -> Unit,
    onNavigateToVitals: () -> Unit,
    onNavigateToAshaConnect: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToSOS: () -> Unit
) {
    val uid = viewModel.currentUser?.uid ?: ""
    val medicines by pillReminderViewModel.allMedicines.collectAsState()
    val bpEntries by vitalsViewModel.bpEntries.collectAsState()

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            profileViewModel.loadProfile(uid)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Greeting
            Text(
                text = "Good Morning 👋",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Next Dose Card
            val nextDose = medicines.firstOrNull()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF338899)) // Teal color from image
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Next Dose", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = nextDose?.let { "${it.name} ${it.dosage}" } ?: "No pending doses",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nextDose?.let { "Tonight - 9:00 PM" } ?: "Stay healthy!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BP Card
            val latestBP = bpEntries.firstOrNull()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BP", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = latestBP?.let { "${it.value1.toInt()}/${it.value2.toInt()}" } ?: "120/80",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            
            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions Grid (2x2)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickActionCard(
                    title = "Medicines",
                    subtitle = "Track pills",
                    iconLabel = "M",
                    iconColor = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPillReminder
                )
                QuickActionCard(
                    title = "Vitals",
                    subtitle = "Log readings",
                    iconLabel = "V",
                    iconColor = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToVitals
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                QuickActionCard(
                    title = "Camps",
                    subtitle = "ASHA schedule",
                    iconLabel = "C",
                    iconColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAshaConnect
                )
                QuickActionCard(
                    title = "Profile",
                    subtitle = "Health info",
                    iconLabel = "P",
                    iconColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToProfile(uid) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Upcoming Health Camp", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

            Spacer(modifier = Modifier.height(16.dp))

            // Health Camp Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .width(50.dp)
                            .padding(end = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("15", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                        Text("MAY", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("General Health Camp", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("PHC Center - Honnali", color = Color.Gray, fontSize = 14.sp)
                        Text("In 3 days", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main SOS Button
            Button(
                onClick = onNavigateToSOS,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Text("Emergency SOS", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Small Floating SOS square
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEF4444))
                    .clickable { onNavigateToSOS() },
                contentAlignment = Alignment.Center
            ) {
                Text("SOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    iconLabel: String,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iconLabel, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
