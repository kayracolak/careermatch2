package com.example.careermatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.careermatch.ui.navigation.Routes
import com.example.careermatch.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel() // ViewModel bağlandı
) {
    val scrollState = rememberScrollState()

    // Veritabanındaki kayıtlı bilgi (ViewModel'den geliyor)
    val currentExtraInfo by viewModel.extraInfo.collectAsState()

    // Dialog kontrolü
    var showExtraInfoDialog by remember { mutableStateOf(false) }
    var tempInfoText by remember { mutableStateOf("") }

    // Dialog açıldığında MEVCUT bilgiyi getir (Kullanıcı tekrar yazmak zorunda kalmasın)
    LaunchedEffect(showExtraInfoDialog) {
        if (showExtraInfoDialog) {
            tempInfoText = currentExtraInfo
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Career Assistant", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E88E5)
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFF1E88E5))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Section
            Text(
                "Hello, Student! 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E),
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Ready to find your dream job?",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card (Mavi Kart)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E88E5), Color(0xFF42A5F5))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Powered", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Your transcript is uploaded. Our AI analyzes job descriptions against your academic profile.",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 1. BUTON: FIND JOBS
            Button(
                onClick = { navController.navigate(Routes.JOB_SEARCH) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1E88E5))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Find Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                            Text("Analyze compatibility", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF1A1C1E))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. YENİ BUTON: EXTRA INFORMATION (Dinamik Gösterim)
            val hasExtraInfo = currentExtraInfo.isNotEmpty()

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showExtraInfoDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // İkon Durumu: Doluysa Yeşil, Boşsa Turuncu
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (hasExtraInfo) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasExtraInfo) Icons.Default.CheckCircle else Icons.Default.EditNote,
                            contentDescription = null,
                            tint = if (hasExtraInfo) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Extra Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                        Text(
                            text = if (hasExtraInfo) "Information Saved ✅" else "Add skills, experience (Optional)",
                            fontSize = 12.sp,
                            color = if (hasExtraInfo) Color(0xFF2E7D32) else Color.Gray,
                            fontWeight = if (hasExtraInfo) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Sağ taraftaki küçük ikon
                    if (hasExtraInfo) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    } else {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
        }
    }

    // --- POP-UP EKRANI (DIALOG) ---
    if (showExtraInfoDialog) {
        AlertDialog(
            onDismissRequest = { showExtraInfoDialog = false },
            title = { Text("Extra Experience & Skills") },
            text = {
                Column {
                    Text(
                        "Add details not in your transcript (e.g., job experience, side projects, certificates). AI will use this for better analysis.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tempInfoText,
                        onValueChange = { tempInfoText = it },
                        label = { Text("Your Experience") },
                        placeholder = { Text("e.g. I have 2 years of Flutter experience...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveExtraInfo(tempInfoText) {
                            showExtraInfoDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text(if (currentExtraInfo.isNotEmpty()) "Update Info" else "Save Info")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExtraInfoDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}