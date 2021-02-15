package dev.baninho.flunk.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.baninho.flunk.dto.Court

class MainViewModel: ViewModel() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var _courts: MutableLiveData<ArrayList<Court>> = MutableLiveData<ArrayList<Court>>()

    fun saveCourt(court: Court) {

        court.save(firestore)

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
                        allCourts.add(court!!)
                    }
                }
                _courts.value = allCourts
            } // endif (snapshot!=null)
        }
    }

    internal var courts: MutableLiveData<ArrayList<Court>>
    get() { return _courts }
    set(value) { courts = value }
}