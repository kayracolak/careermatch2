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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
import androidx.navigation.NavController // Eklendi
import com.example.careermatch.model.JobPosting
import com.example.careermatch.viewmodel.JobSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSearchScreen(
    navController: NavController,
    viewModel: JobSearchViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val jobs by viewModel.jobs.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val savedJobIds by viewModel.savedJobIds.collectAsState()

    var titleQuery by remember { mutableStateOf("Software Engineer") }
    var locationQuery by remember { mutableStateOf("Turkey") }

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(analysisResult) {
        if (analysisResult != null) showBottomSheet = true
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Job Search", fontWeight = FontWeight.Bold) },

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E88E5))
                    }
                },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ARAMA KISMI
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = titleQuery,
                        onValueChange = { titleQuery = it },
                        label = { Text("Job Title") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1E88E5)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1E88E5), unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = locationQuery,
                        onValueChange = { locationQuery = it },
                        label = { Text("Location") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1E88E5)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1E88E5), unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.searchJobs(titleQuery, locationQuery) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Search Jobs", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Text(text = error!!, color = Color(0xFFD32F2F), modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // İŞ LİSTESİ
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jobs) { job ->
                    val isFavorite = savedJobIds.contains(job.id)

                    JobCard(
                        job = job,
                        isFavorite = isFavorite,
                        onFavoriteClick = { viewModel.toggleFavorite(job) },
                        onApplyClick = {
                            val url = job.jobUrl ?: job.jobUrl
                            if (!url.isNullOrEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                context.startActivity(intent)
                            }
                        },
                        onAnalyzeClick = {
                            val desc = job.descriptionText ?: "No description available."
                            viewModel.analyzeJobCompatibility(desc)
                        }
                    )
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
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
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
                    CircularScoreIndicator(score = score)
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
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(10.dp)) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color(0xFF1E88E5), strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("AI is analyzing...", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E88E5))
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: JobPosting,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onApplyClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF1E88E5))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = job.companyName ?: "Confidential", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = job.location ?: "Remote", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save",
                        tint = if (isFavorite) Color(0xFFE91E63) else Color.Gray // Pembe veya Gri
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

            Row {
                OutlinedButton(
                    onClick = onApplyClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E88E5))
                ) {
                    Text("View Job")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze")
                }
            }
        }
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    radius: Dp = 80.dp,
    strokeWidth: Dp = 12.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1500)
    )

    val color = when {
        score >= 70 -> Color(0xFF4CAF50) // Yeşil
        score >= 40 -> Color(0xFFFFC107) // Sarı
        else -> Color(0xFFF44336) // Kırmızı
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(radius * 2)) {
        Canvas(modifier = Modifier.size(radius * 2)) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "Match",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}