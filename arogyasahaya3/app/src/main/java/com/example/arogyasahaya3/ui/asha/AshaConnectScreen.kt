package com.example.arogyasahaya3.ui.asha

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HealthCamp(
    val id: Int,
    val name: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String
)

val dummyCamps = listOf(
    HealthCamp(1, "Senior Wellness Checkup", "Oct 25, 2026", "10:00 AM", "Community Center Hall", "Free basic health screening including BP and Sugar checkup for elderly citizens."),
    HealthCamp(2, "Eye Care Awareness", "Nov 02, 2026", "02:00 PM", "ASHA Local Office", "Free eye testing and distribution of reading glasses for seniors."),
    HealthCamp(3, "Nutrition & Diet Workshop", "Nov 10, 2026", "11:00 AM", "Town Hall", "Expert talk on healthy diet patterns for managing chronic diseases."),
    HealthCamp(4, "Diabetes Management Camp", "Nov 15, 2026", "09:00 AM", "Primary Health Center", "Free glucose testing and consultation with specialists.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AshaConnectScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("ASHA Connect", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(32.dp))
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Upcoming Local Health Camps",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(dummyCamps) { camp ->
                CampCard(camp)
            }
        }
    }
}

@Composable
fun CampCard(camp: HealthCamp) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = camp.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("${camp.date} at ${camp.time}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(camp.location, style = MaterialTheme.typography.bodyLarge)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = camp.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )
        }
    }
}
