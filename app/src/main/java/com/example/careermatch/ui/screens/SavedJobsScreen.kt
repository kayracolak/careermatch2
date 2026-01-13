package com.example.careermatch.ui.screens

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.careermatch.model.JobPosting
import com.example.careermatch.viewmodel.SavedJobsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    navController: NavController,
    viewModel: SavedJobsViewModel = viewModel()
) {
    val savedJobs by viewModel.savedJobs.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val analysisResult by viewModel.analysisResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(analysisResult) {
        if (analysisResult != null) showBottomSheet = true
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Jobs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E88E5))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E88E5)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1E88E5))
                }
            } else if (savedJobs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No saved jobs yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedJobs) { job ->
                        SavedJobCard(
                            job = job,
                            onApplyClick = {
                                val url = job.jobUrl ?: job.jobUrl
                                if (!url.isNullOrEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                }
                            },
                            onAnalyzeClick = {
                                val desc = job.descriptionText ?: "No description"
                                viewModel.analyzeJobCompatibility(desc)
                            },
                            onRemoveClick = {
                                viewModel.removeJob(job.id)
                            }
                        )
                    }
                }
            }
        }

        // BOTTOM SHEET
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.clearAnalysis()
                },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                val scrollState = rememberScrollState()
                val rawText = analysisResult ?: ""
                val scoreRegex = Regex("SCORE:\\s*(\\d+)")
                val matchResult = scoreRegex.find(rawText)
                val score = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val cleanText = rawText.replace(Regex("SCORE:.*"), "").trim().replace("**", "").replace("#", "")

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Compatibility Report", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.height(24.dp))
                    SavedCircularScoreIndicator(score)
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(cleanText, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF424242), lineHeight = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        if (isAnalyzing && !showBottomSheet) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E88E5))
            }
        }
    }
}

@Composable
fun SavedJobCard(
    job: JobPosting,
    onApplyClick: () -> Unit,
    onAnalyzeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF1E88E5))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                    Text(text = job.companyName ?: "Confidential", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(text = job.location ?: "Remote", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                // Çöp Kutusu İkonu
                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))
            Row {
                OutlinedButton(onClick = onApplyClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("View Job") }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onAnalyzeClick, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze")
                }
            }
        }
    }
}

@Composable
fun SavedCircularScoreIndicator(score: Int) {
    val color = if (score >= 70) Color(0xFF4CAF50) else if (score >= 40) Color(0xFFFFC107) else Color(0xFFF44336)
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
        Canvas(modifier = Modifier.size(160.dp)) {
            drawArc(color = Color.LightGray.copy(alpha = 0.3f), startAngle = 135f, sweepAngle = 270f, useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
            drawArc(color = color, startAngle = 135f, sweepAngle = 270f * (score / 100f), useCenter = false, style = Stroke(width = 30f, cap = StrokeCap.Round))
        }
        Text(text = "$score%", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = color)
    }
}