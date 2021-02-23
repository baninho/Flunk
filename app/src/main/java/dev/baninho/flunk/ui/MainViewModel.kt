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
import dev.baninho.flunk.dto.UserInfo

class MainViewModel: ViewModel() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var _courts: MutableLiveData<ArrayList<Court>> = MutableLiveData<ArrayList<Court>>()
    var user: FirebaseUser? = null

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToCourts()
    }

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

    fun getUserInfo(uid: String): UserInfo? {
        return firestore.collection("users")
            .document(uid).get().result.toObject(UserInfo::class.java)
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

    fun saveUserInfo(userInfo: UserInfo) {
        val documentRef: DocumentReference = firestore.collection("courts").document(userInfo.uid)

        documentRef.set(userInfo)
            .addOnSuccessListener { Log.d("Firebase",
                "userInfo saved. uid: ${userInfo.uid}, Name: ${userInfo.name}") }
            .addOnFailureListener { Log.d("Firebase", "saveUserInfo failed") }
    }

    internal var courts: MutableLiveData<ArrayList<Court>>
    get() { return _courts }
    set(value) {
        value.also { this.courts = it }
    }
}