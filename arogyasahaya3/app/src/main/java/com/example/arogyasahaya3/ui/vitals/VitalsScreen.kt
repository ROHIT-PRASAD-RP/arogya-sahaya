package com.example.arogyasahaya3.ui.vitals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arogyasahaya3.data.local.VitalEntry
import com.example.arogyasahaya3.ui.profile.ProfileUiState
import com.example.arogyasahaya3.ui.profile.ProfileViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(
    onNavigateBack: () -> Unit,
    viewModel: VitalsViewModel,
    profileViewModel: ProfileViewModel,
    uid: String
) {
    val bpEntries by viewModel.bpEntries.collectAsState()
    val sugarEntries by viewModel.sugarEntries.collectAsState()
    val heartRateEntries by viewModel.heartRateEntries.collectAsState()

    val profileState by profileViewModel.uiState.collectAsState()
    
    LaunchedEffect(uid) {
        profileViewModel.loadProfile(uid)
    }

    val chronicDiseases = if (profileState is ProfileUiState.Success) {
        (profileState as ProfileUiState.Success).profile.chronicDiseases
    } else ""

    val isBPRecommended = chronicDiseases.contains("Hypertension", ignoreCase = true) || 
                          chronicDiseases.contains("BP", ignoreCase = true) ||
                          chronicDiseases.contains("Pressure", ignoreCase = true)
    
    val isSugarRecommended = chronicDiseases.contains("Diabetes", ignoreCase = true) || 
                             chronicDiseases.contains("Sugar", ignoreCase = true)

    var showAddDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Vitals Tracker", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sort sections based on recommendations
            val sections = listOf(
                Triple("Blood Pressure (mmHg)", bpEntries, "BP"),
                Triple("Sugar Level (mg/dL)", sugarEntries, "Sugar"),
                Triple("Heart Rate (bpm)", heartRateEntries, "HeartRate")
            ).sortedByDescending { triple ->
                when (triple.third) {
                    "BP" -> isBPRecommended
                    "Sugar" -> isSugarRecommended
                    else -> false
                }
            }

            sections.forEach { (title, entries, type) ->
                val isRecommended = when (type) {
                    "BP" -> isBPRecommended
                    "Sugar" -> isSugarRecommended
                    else -> false
                }
                VitalSection(
                    title = title,
                    entries = entries,
                    type = type,
                    isRecommended = isRecommended,
                    onAddClick = { showAddDialog = type }
                )
            }
        }

        showAddDialog?.let { type ->
            AddVitalDialog(
                type = type,
                onDismiss = { showAddDialog = null },
                onConfirm = { v1, v2 ->
                    viewModel.addVital(type, v1, v2)
                    showAddDialog = null
                }
            )
        }
    }
}

@Composable
fun VitalSection(
    title: String,
    entries: List<VitalEntry>,
    type: String,
    isRecommended: Boolean = false,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isRecommended) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isRecommended) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "RECOMMENDED FOR YOU",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Text("No data recorded yet.", style = MaterialTheme.typography.bodyLarge)
            } else {
                VitalChart(entries, type)
            }
        }
    }
}

@Composable
fun VitalChart(entries: List<VitalEntry>, type: String) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                xAxis.setDrawGridLines(false)
                axisRight.isEnabled = false
                legend.textSize = 12f
            }
        },
        update = { chart ->
            val dataSets = mutableListOf<LineDataSet>()
            
            val values1 = entries.reversed().mapIndexed { index, vitalEntry ->
                Entry(index.toFloat(), vitalEntry.value1)
            }
            val set1 = LineDataSet(values1, if (type == "BP") "Systolic" else "Value").apply {
                color = primaryColor
                setCircleColor(primaryColor)
                lineWidth = 3f
                circleRadius = 5f
                valueTextSize = 10f
            }
            dataSets.add(set1)

            if (type == "BP") {
                val values2 = entries.reversed().mapIndexed { index, vitalEntry ->
                    Entry(index.toFloat(), vitalEntry.value2)
                }
                val set2 = LineDataSet(values2, "Diastolic").apply {
                    color = secondaryColor
                    setCircleColor(secondaryColor)
                    lineWidth = 3f
                    circleRadius = 5f
                    valueTextSize = 10f
                }
                dataSets.add(set2)
            }

            chart.data = LineData(dataSets.toList())
            chart.invalidate()
        },
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
}

@Composable
fun AddVitalDialog(
    type: String,
    onDismiss: () -> Unit,
    onConfirm: (Float, Float) -> Unit
) {
    var v1 by remember { mutableStateOf("") }
    var v2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add $type Log", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = v1,
                    onValueChange = { v1 = it },
                    label = { Text(if (type == "BP") "Systolic" else "Value") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (type == "BP") {
                    OutlinedTextField(
                        value = v2,
                        onValueChange = { v2 = it },
                        label = { Text("Diastolic") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                onConfirm(v1.toFloatOrNull() ?: 0f, v2.toFloatOrNull() ?: 0f)
            }) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
