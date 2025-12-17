package com.example.careermatch.viewmodel

import androidx.lifecycle.ViewModel
import com.example.careermatch.model.Department
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DepartmentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Ekranda göstereceğimiz bölüm listesi
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments = _departments.asStateFlow()

    // Yükleniyor mu?
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // Hata var mı?
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchDepartments()
    }

    // Firestore'dan Bölümleri Çek
    private fun fetchDepartments() {
        _loading.value = true
        // Senin oluşturduğun koleksiyon adı "departments"
        db.collection("departments")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.map { doc ->
                    // id: dökümanın id'si (android_bilmuh vb.)
                    // name: içindeki field (Bilgisayar Mühendisliği vb.)
                    Department(
                        id = doc.id,
                        name = doc.getString("name") ?: ""
                    )
                }
                _departments.value = list
                _loading.value = false
            }
            .addOnFailureListener { exception ->
                _error.value = exception.localizedMessage
                _loading.value = false
            }
    }

    // Seçilen Bölümü Kullanıcıya Kaydet
    fun saveUserDepartment(selectedDept: Department, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) return

        _loading.value = true

        val userData = hashMapOf(
            "email" to (auth.currentUser?.email ?: ""),
            "departmentId" to selectedDept.id,
            "departmentName" to selectedDept.name
        )

        // users koleksiyonunda kullanıcının kendi ID'si ile döküman oluşturuyoruz
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                _loading.value = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                _loading.value = false
                _error.value = e.localizedMessage
            }
    }
}