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
        if (user.uid in players) return CourtJoinCode.PLAYER_ALREADY_JOINED
        if (playerCount == capacity) return CourtJoinCode.NO_PLAYER_CAPACITY_AVAILABLE

        players.add(user.uid)
        playerCount += 1

        return CourtJoinCode.JOIN_REQUEST_ACCEPTED
    }

    enum class CourtJoinCode {
        JOIN_REQUEST_ACCEPTED,
        PLAYER_ALREADY_JOINED,
        NO_PLAYER_CAPACITY_AVAILABLE,
    }
}