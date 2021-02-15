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
}