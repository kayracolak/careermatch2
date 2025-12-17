package com.example.careermatch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careermatch.model.Department
import com.example.careermatch.viewmodel.DepartmentViewModel

@Composable
fun DepartmentSelectionScreen(
    onSelectionSuccess: () -> Unit,
    viewModel: DepartmentViewModel = viewModel()
) {
    val departments by viewModel.departments.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Hangi bölüm seçildi?
    var selectedDepartment by remember { mutableStateOf<Department?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("Bölümünü Seç", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Devam etmek için lütfen bölümünü listeden seç.", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        // Liste
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            items(departments) { dept ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedDepartment = dept },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedDepartment?.id == dept.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dept.name, modifier = Modifier.weight(1f))
                        if (selectedDepartment?.id == dept.id) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedDepartment?.let {
                    viewModel.saveUserDepartment(it) {
                        onSelectionSuccess()
                    }
                }
            },
            enabled = selectedDepartment != null && !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet ve Devam Et")
        }
    }
}