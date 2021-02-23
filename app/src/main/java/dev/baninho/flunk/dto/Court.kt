package dev.baninho.flunk.dto

import com.google.firebase.auth.FirebaseUser

data class Court(var owner: String = "",
                 var ownerId: String = "",
                 var latitude: String = "",
                 var longitude: String = "",
                 var isActive: Boolean = false,
                 var playerCount: Int = 0,
                 var capacity: Int = 0,
                 var id: String = "",
                 var players: ArrayList<String> = ArrayList(),
) {
    override fun toString(): String{
        return "$owner's Spielfeld "
    }

    fun join(user: FirebaseUser): CourtJoinCode {
        return when {
            user.uid in players -> CourtJoinCode.PLAYER_ALREADY_JOINED
            playerCount == capacity -> CourtJoinCode.NO_PLAYER_CAPACITY_AVAILABLE
            else -> CourtJoinCode.JOIN_OK
        }
    }

    enum class CourtJoinCode {
        JOIN_OK,
        PLAYER_ALREADY_JOINED,
        NO_PLAYER_CAPACITY_AVAILABLE,
    }
}