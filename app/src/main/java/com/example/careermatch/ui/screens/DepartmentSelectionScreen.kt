package com.example.careermatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.model.Department
import com.example.careermatch.viewmodel.DepartmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentSelectionScreen(
    onSelectionSuccess: () -> Unit,
    viewModel: DepartmentViewModel = viewModel()
) {
    val departments by viewModel.departments.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedDepartment by remember { mutableStateOf<Department?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Select Department", fontWeight = FontWeight.Bold) },
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
                .background(Color(0xFFF5F7FA))
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                "What's your major?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                "This helps us tailor the AI analysis for you.",
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1E88E5))
                }
            }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(departments) { dept ->
                    val isSelected = selectedDepartment?.id == dept.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDepartment = dept },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E88E5)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF1E88E5) else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                dept.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF1E88E5) else Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E88E5))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    selectedDepartment?.let {
                        viewModel.saveUserDepartment(it) { onSelectionSuccess() }
                    }
                },
                enabled = selectedDepartment != null && !loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Confirm & Continue", fontSize = 18.sp)
            }
        }
    }
}