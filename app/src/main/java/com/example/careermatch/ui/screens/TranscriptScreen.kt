package com.example.careermatch.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.viewmodel.TranscriptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptScreen(
    onNavigateNext: () -> Unit,
    onLogout: () -> Unit,
    viewModel: TranscriptViewModel = viewModel()
) {
    val loading by viewModel.loading.collectAsState()
    val status by viewModel.uploadStatus.collectAsState()
    val existingUrl by viewModel.existingTranscriptUrl.collectAsState()
    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadPdf(context, uri) {
                Toast.makeText(context, "Transcript Uploaded Successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Academic Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1E88E5)
                ),
                actions = {
                    IconButton(onClick = { viewModel.logout { onLogout() } }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color(0xFF1E88E5))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (existingUrl != null) {
                // Success Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Transcript Ready!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Your academic data is secured. You can proceed to analysis.", textAlign = TextAlign.Center, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { onNavigateNext() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Start Analysis", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                    Text("Upload a different transcript", color = Color(0xFF1E88E5))
                }

            } else {
                // Upload State
                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF1E88E5), modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Upload Transcript", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upload your PDF transcript to let AI analyze your strengths.", textAlign = TextAlign.Center, color = Color.Gray)

                Spacer(modifier = Modifier.height(40.dp))

                if (loading) {
                    CircularProgressIndicator(color = Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Processing...", color = Color(0xFF1E88E5))
                } else {
                    Button(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select PDF File", fontSize = 18.sp)
                    }
                }

                if (status != null) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = status!!,
                        color = if (status!!.contains("Error")) Color.Red else Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    if (status!!.contains("Successfully")) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onNavigateNext() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    }
}