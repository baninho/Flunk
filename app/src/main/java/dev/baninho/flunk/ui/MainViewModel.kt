package dev.baninho.flunk.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.baninho.flunk.dto.Court

class MainViewModel: ViewModel() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var _courts: MutableLiveData<ArrayList<Court>> = MutableLiveData<ArrayList<Court>>()
    var user: FirebaseUser? = null

    fun saveCourt(court: Court) {
        val documentRef: DocumentReference

        if (court.id.isBlank()) {
            documentRef = firestore.collection("courts").document()
            court.id = documentRef.id
        } else {
            documentRef = firestore.collection("courts").document(court.id)
        }

        documentRef.set(court)
            .addOnSuccessListener { Log.d("Firebase",
                "document saved. Players: ${court.playerCount}/${court.capacity}") }
            .addOnFailureListener { Log.d("Firebase", "saveCourt failed") }
    }

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToCourts()
    }

    /*
    Get Updates from Firestore
     */
    private fun listenToCourts() {
        firestore.collection("courts").addSnapshotListener {
                snapshot, error ->
            // if there is an exception, skip
            if (error != null) {
                Log.w(TAG, "Listen failed", error)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                val allCourts = ArrayList<Court>()
                val documents = snapshot.documents
                documents.forEach {
                    val court = it.toObject(Court::class.java)
                    if (court != null) {
                        allCourts.add(court)
                    }
                }
                _courts.value = allCourts
            }
        }
    }

    internal var courts: MutableLiveData<ArrayList<Court>>
    get() { return _courts }
    set(value) {
        value.also { this.courts = it }
    }
}