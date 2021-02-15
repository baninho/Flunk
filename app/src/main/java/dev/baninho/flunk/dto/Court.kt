package dev.baninho.flunk.dto

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class Court(var owner: String = "",
                 var ownerId: String = "",
                 var latitude: String = "",
                 var longitude: String = "",
                 var isActive: Boolean = false,
                 var players: Int = 0,
                 var capacity: Int = 0,
                 var id: String = ""
) {
    override fun toString(): String{
        return "$owner's Spielfeld "
    }

    fun save(firestore: FirebaseFirestore) {
        firestore.collection("courts")
            .document()
            .set(this)
            .addOnSuccessListener { Log.d("Firebase", "document saved") }
            .addOnFailureListener { Log.d("Firebase", "saveCourt failed") }
    }
}