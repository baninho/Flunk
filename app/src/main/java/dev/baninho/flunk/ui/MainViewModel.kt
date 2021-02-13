package dev.baninho.flunk.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.baninho.flunk.dto.Court

class MainViewModel: ViewModel() {
    fun save(court: Court) {
        firestore.collection("courts")
            .document()
            .set(court)
            .addOnSuccessListener { Log.d("Firebase", "document saved") }
            .addOnFailureListener { Log.d("Firebase", "save failed") }
    }

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }
}